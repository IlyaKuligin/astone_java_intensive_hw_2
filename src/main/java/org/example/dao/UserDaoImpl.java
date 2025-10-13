package org.example.dao;

import org.example.entity.User;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public Optional<User> findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            transaction.commit();

            logger.info("User found by id {}: {}", id, user != null ? "exists" : "not found");
            return Optional.ofNullable(user);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding user by id: {}", id, e);
            throw new RuntimeException("Failed to find user by id: " + id, e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            List<User> users = session.createQuery("FROM User", User.class).list();
            transaction.commit();

            logger.info("Found {} users", users.size());
            return users;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding all users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        } finally {
            session.close();
        }
    }

    @Override
    public User save(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();

            logger.info("User saved successfully with id: {}", user.getId());
            return user;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving user: {}", user, e);
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public User update(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();

            logger.info("User updated successfully with id: {}", updatedUser.getId());
            return updatedUser;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating user: {}", user, e);
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("User deleted successfully with id: {}", id);
            } else {
                logger.warn("User with id {} not found for deletion", id);
            }
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException("Failed to delete user with id: " + id, e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            transaction.commit();

            logger.info("User found by email {}: {}", email, user != null ? "exists" : "not found");
            return Optional.ofNullable(user);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding user by email: {}", email, e);
            throw new RuntimeException("Failed to find user by email: " + email, e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<User> findByName(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE name LIKE :name", User.class);
            query.setParameter("name", "%" + name + "%");
            List<User> users = query.list();
            transaction.commit();

            logger.info("Found {} users with name containing: {}", users.size(), name);
            return users;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error finding users by name: {}", name, e);
            throw new RuntimeException("Failed to find users by name: " + name, e);
        } finally {
            session.close();
        }
    }
}