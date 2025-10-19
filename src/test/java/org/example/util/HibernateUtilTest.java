package org.example.util;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HibernateUtilTest {
    @Test
    void testGetSessionFactory_Success() {
        try (MockedStatic<HibernateUtil> hibernateUtilMock = mockStatic(HibernateUtil.class)) {
            SessionFactory mockSessionFactory = mock(SessionFactory.class);
            when(mockSessionFactory.isClosed()).thenReturn(false);

            hibernateUtilMock.when(HibernateUtil::getSessionFactory).thenReturn(mockSessionFactory);

            SessionFactory result = HibernateUtil.getSessionFactory();

            assertNotNull(result);
            assertSame(mockSessionFactory, result);
        }
    }

    @Test
    void testShutdown() {
        try (MockedStatic<HibernateUtil> hibernateUtilMock = mockStatic(HibernateUtil.class)) {
            assertDoesNotThrow(HibernateUtil::shutdown);

            hibernateUtilMock.verify(HibernateUtil::shutdown, times(1));
        }
    }

    @Test
    void testMultipleGetSessionFactoryCalls() {
        try (MockedStatic<HibernateUtil> hibernateUtilMock = mockStatic(HibernateUtil.class)) {
            SessionFactory mockSessionFactory = mock(SessionFactory.class);
            when(mockSessionFactory.isClosed()).thenReturn(false);

            hibernateUtilMock.when(HibernateUtil::getSessionFactory).thenReturn(mockSessionFactory);

            SessionFactory result1 = HibernateUtil.getSessionFactory();
            SessionFactory result2 = HibernateUtil.getSessionFactory();

            assertSame(result1, result2);
            hibernateUtilMock.verify(HibernateUtil::getSessionFactory, times(2));
        }
    }
}