package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void testCreateUser_Success() {
        String name = "John Doe";
        String email = "john@example.com";
        Integer age = 30;
        User expectedUser = new User(name, email, age);
        expectedUser.setId(1L);

        when(userDao.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser(name, email, age);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(age, result.getAge());

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_InvalidData() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "john@example.com", 30));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("John", "", 30));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("John", "invalid-email", 30));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("John", "john@example.com", -1));

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        User expectedUser = new User("John", "john@example.com", 30);
        expectedUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_InvalidId() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(null));

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(0L));

        verify(userDao, never()).findById(anyLong());
    }

    @Test
    void testGetAllUsers() {
        List<User> expectedUsers = List.of(
                new User("User1", "user1@example.com", 25),
                new User("User2", "user2@example.com", 30)
        );

        when(userDao.findAll()).thenReturn(expectedUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        Long userId = 1L;
        User existingUser = new User("Old Name", "old@example.com", 25);
        existingUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userDao.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(userId, "New Name", "new@example.com", 30);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userDao, times(1)).findById(userId);
        verify(userDao, times(1)).update(existingUser);
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;
        User existingUser = new User("John", "john@example.com", 30);
        existingUser.setId(userId);

        when(userDao.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userDao).delete(userId);

        userService.deleteUser(userId);

        verify(userDao, times(1)).findById(userId);
        verify(userDao, times(1)).delete(userId);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, "New Name", "new@example.com", 30));

        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void testDeleteUser_UserNotFound() {
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(userId));

        verify(userDao, never()).delete(anyLong());
    }

    @Test
    void testFindUserByEmail_InvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.findUserByEmail(""));

        assertThrows(IllegalArgumentException.class,
                () -> userService.findUserByEmail(null));
    }

    @Test
    void testFindUsersByName_InvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.findUsersByName(""));

        assertThrows(IllegalArgumentException.class,
                () -> userService.findUsersByName(null));
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
        verify(userDao, times(1)).findById(userId);
    }
}