package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting User Service Application");

        try {
            displayMenu();
            boolean running = true;

            while (running) {
                System.out.print("\nEnter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        createUser();
                        break;
                    case "2":
                        getUserById();
                        break;
                    case "3":
                        getAllUsers();
                        break;
                    case "4":
                        updateUser();
                        break;
                    case "5":
                        deleteUser();
                        break;
                    case "6":
                        findUserByEmail();
                        break;
                    case "7":
                        findUsersByName();
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

                if (running) {
                    displayMenu();
                }
            }

        } catch (Exception e) {
            logger.error("Application error", e);
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
            HibernateUtil.shutdown();
            logger.info("User Service Application stopped");
            System.out.println("Application stopped. Goodbye!");
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1. Create User");
        System.out.println("2. Get User by ID");
        System.out.println("3. Get All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("6. Find User by Email");
        System.out.println("7. Find Users by Name");
        System.out.println("0. Exit");
    }

    private static void createUser() {
        try {
            System.out.println("\n--- Create New User ---");

            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = new User(name, email, age);
            User savedUser = userDao.save(user);

            System.out.println("User created successfully: " + savedUser);

        } catch (NumberFormatException e) {
            System.out.println("Invalid age format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private static void getUserById() {
        try {
            System.out.println("\n--- Get User by ID ---");

            System.out.print("Enter user ID: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }
    }

    private static void getAllUsers() {
        try {
            System.out.println("\n--- All Users ---");

            List<User> users = userDao.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(System.out::println);
            }

        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.println("\n--- Update User ---");

            System.out.print("Enter user ID to update: ");
            Long id = Long.parseLong(scanner.nextLine());

            Optional<User> existingUser = userDao.findById(id);
            if (existingUser.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            User user = existingUser.get();

            System.out.print("Enter new name (current: " + user.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                user.setName(name);
            }

            System.out.print("Enter new email (current: " + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) {
                user.setEmail(email);
            }

            System.out.print("Enter new age (current: " + user.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.trim().isEmpty()) {
                user.setAge(Integer.parseInt(ageInput));
            }

            User updatedUser = userDao.update(user);
            System.out.println("User updated successfully: " + updatedUser);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.println("\n--- Delete User ---");

            System.out.print("Enter user ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());

            // Проверяем существование пользователя
            Optional<User> user = userDao.findById(id);
            if (user.isPresent()) {
                userDao.delete(id);
                System.out.println("User deleted successfully with ID: " + id);
            } else {
                System.out.println("User not found with ID: " + id);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    private static void findUserByEmail() {
        try {
            System.out.println("\n--- Find User by Email ---");

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            Optional<User> user = userDao.findByEmail(email);
            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
            } else {
                System.out.println("User not found with email: " + email);
            }

        } catch (Exception e) {
            System.out.println("Error finding user by email: " + e.getMessage());
        }
    }

    private static void findUsersByName() {
        try {
            System.out.println("\n--- Find Users by Name ---");

            System.out.print("Enter name (or part of name): ");
            String name = scanner.nextLine();

            List<User> users = userDao.findByName(name);
            if (users.isEmpty()) {
                System.out.println("No users found with name containing: " + name);
            } else {
                users.forEach(System.out::println);
            }

        } catch (Exception e) {
            System.out.println("Error finding users by name: " + e.getMessage());
        }
    }
}