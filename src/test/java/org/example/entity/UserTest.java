package org.example.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultConstructor() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getAge());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("John", "john@test.com", 30);

        assertEquals("John", user.getName());
        assertEquals("john@test.com", user.getEmail());
        assertEquals(30, user.getAge());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(25);
        user.setCreatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("Test", user.getName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals(25, user.getAge());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void testToString() {
        User user = new User("John", "john@test.com", 30);
        user.setId(1L);

        LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        user.setCreatedAt(fixedTime);

        String result = user.toString();

        assertTrue(result.contains("User{id=1"));
        assertTrue(result.contains("name='John'"));
        assertTrue(result.contains("email='john@test.com'"));
        assertTrue(result.contains("age=30"));
        assertTrue(result.contains("createdAt=" + fixedTime));
    }

    @Test
    void testCreatedAtInitialization() throws Exception {
        User user1 = new User();

        Thread.sleep(10);

        User user2 = new User();

        assertNotNull(user1.getCreatedAt());
        assertNotNull(user2.getCreatedAt());
        assertTrue(user1.getCreatedAt().isBefore(user2.getCreatedAt()));
    }
}