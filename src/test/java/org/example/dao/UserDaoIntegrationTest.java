package org.example.dao;

import org.example.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserDao userDao;
    private SessionFactory testSessionFactory;


    @BeforeAll
    void setUpAll() {
        // Настраиваем Hibernate для использования PostgreSQL Testcontainer
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");

        configuration.addAnnotatedClass(org.example.entity.User.class);

        testSessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(testSessionFactory);
    }

    @BeforeEach
    void setUp() {
        clearDatabase();
    }

    @AfterAll
    void tearDownAll() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
    }

    private void clearDatabase() {
        try (var session = testSessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            try {
                session.createMutationQuery("DELETE FROM User").executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }
    }

    @Test
    void testSaveAndFindUser() {
        User user = new User("John Doe", "john@example.com", 30);

        User savedUser = userDao.save(user);
        Optional<User> foundUser = userDao.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
        assertEquals(30, foundUser.get().getAge());
        assertNotNull(foundUser.get().getCreatedAt());
    }

    @Test
    void testFindAllUsers() {
        userDao.save(new User("User1", "user1@example.com", 25));
        userDao.save(new User("User2", "user2@example.com", 30));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        User user = userDao.save(new User("Original", "original@example.com", 25));

        user.setName("Updated");
        user.setEmail("updated@example.com");
        user.setAge(30);
        User updatedUser = userDao.update(user);

        assertEquals("Updated", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(30, updatedUser.getAge());
    }

    @Test
    void testDeleteUser() {
        User user = userDao.save(new User("ToDelete", "delete@example.com", 25));
        Long userId = user.getId();

        userDao.delete(userId);
        Optional<User> deletedUser = userDao.findById(userId);

        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testFindByEmail() {
        String email = "find@example.com";
        userDao.save(new User("Find User", email, 25));

        Optional<User> foundUser = userDao.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    void testFindByName() {
        userDao.save(new User("John Smith", "john.smith@example.com", 25));
        userDao.save(new User("John Doe", "john.doe@example.com", 30));
        userDao.save(new User("Alice Smith", "alice@example.com", 28)); // Не содержит "John" вообще

        List<User> johnUsers = userDao.findByName("John");

        assertEquals(2, johnUsers.size(), "Should find exactly 2 users with 'John' in name");

        List<String> foundNames = johnUsers.stream()
                .map(User::getName)
                .toList();

        assertTrue(foundNames.contains("John Smith"));
        assertTrue(foundNames.contains("John Doe"));
        assertFalse(foundNames.contains("Alice Smith"));
    }

    @Test
    void testFindByName_PartialMatch() {
        userDao.save(new User("Johny Bravo", "johny@example.com", 25));
        userDao.save(new User("Mike Johnson", "mike@example.com", 30));
        userDao.save(new User("Alice Smith", "alice@example.com", 28));

        List<User> johnUsers = userDao.findByName("John");

        assertEquals(2, johnUsers.size());
    }

    @Test
    void testFindByName_NoMatches() {
        userDao.save(new User("Alice Smith", "alice@example.com", 25));
        userDao.save(new User("Bob Brown", "bob@example.com", 30));

        List<User> result = userDao.findByName("Nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testUniqueEmailConstraint() {
        String email = "duplicate@example.com";
        userDao.save(new User("User1", email, 25));

        User duplicateUser = new User("User2", email, 30);

        assertThrows(RuntimeException.class, () -> userDao.save(duplicateUser));
    }

    @Test
    void testFindById_UserNotFound() {
        Optional<User> result = userDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_NonExistentUser() {
        assertDoesNotThrow(() -> userDao.delete(999L));
    }

    @Test
    void testFindByEmail_UserNotFound() {
        Optional<User> result = userDao.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testSave_NullUser() {
        assertThrows(RuntimeException.class, () -> userDao.save(null));
    }
}