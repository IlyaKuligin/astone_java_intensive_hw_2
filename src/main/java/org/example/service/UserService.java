package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);

        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        validateUserData(name, email, age);

        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);

        return userDao.update(existingUser);
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }

        if (!userDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        userDao.delete(id);
    }

    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return userDao.findByEmail(email);
    }

    public List<User> findUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return userDao.findByName(name);
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }
}
