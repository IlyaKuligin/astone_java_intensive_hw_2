package org.example;

import org.example.dao.UserDao;
import org.example.util.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private UserDao userDao;
    private MockedStatic<HibernateUtil> hibernateUtilMockedStatic;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        userDao = mock(UserDao.class);
        hibernateUtilMockedStatic = mockStatic(HibernateUtil.class);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        if (hibernateUtilMockedStatic != null) {
            hibernateUtilMockedStatic.close();
        }
    }

    @Test
    void testMain_DisplayMenu() {
        String input = "0\n"; // Exit immediately
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("=== User Service ==="));
        assertTrue(output.contains("1. Create User"));
        assertTrue(output.contains("0. Exit"));
    }
}