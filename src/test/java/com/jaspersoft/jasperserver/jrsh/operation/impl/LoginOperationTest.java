package com.jaspersoft.jasperserver.jrsh.operation.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.AuthenticationFailedException;
import com.jaspersoft.jasperserver.jrsh.common.SessionFactory;
import com.jaspersoft.jasperserver.jrsh.operation.result.OperationResult;
import com.jaspersoft.jasperserver.jrsh.operation.result.ResultCode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.ProcessingException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionFactory.class)
public class LoginOperationTest {

    private LoginOperation login;

    @Mock private Session sessionMock;

    @Before public void before() {
        MockitoAnnotations.initMocks(this);
        login = new LoginOperation();
    }

    @Test public void shouldLoginAndReturnCorrectOperationResult() {

        // Given
        login.setUsername("superuser");
        login.setPassword("superuser");
        login.setServer("http://localhost:8080/jasperserver-pro");

        PowerMockito.mockStatic(SessionFactory.class);
        PowerMockito.when(SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null))
                .thenReturn(sessionMock);

        // When
        OperationResult result = login.execute(null);

        // Then
        // Verify that the createSharedSession method was actually called
        PowerMockito.verifyStatic();
        SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);

        Assert.assertEquals("You have logged in as superuser", result.getResultMessage());
        Assert.assertEquals(ResultCode.SUCCESS, result.getResultCode());
        Assert.assertEquals(login, result.getContext());
    }

    @Test public void shouldFailLoginDueToTheWrongCredentials() {

        // Given
        login.setUsername("wrongUsername");
        login.setPassword("wrongPassword");
        login.setServer("http://localhost:8080/jasperserver-pro");

        PowerMockito.mockStatic(SessionFactory.class);
        PowerMockito.when(SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null))
                .thenThrow(new AuthenticationFailedException("Unauthorized"));

        // When
        OperationResult result = login.execute(null);

        // Then
        PowerMockito.verifyStatic();
        SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);

        Assert.assertEquals("Login failed (Unauthorized)", result.getResultMessage());
        Assert.assertEquals(ResultCode.FAILED, result.getResultCode());
        Assert.assertEquals(login, result.getContext());
    }

    @Test public void shouldFailWithProcessingExceptionAndReturnCorrectOperationResult() {
        // Given
        login.setUsername("superuser");
        login.setPassword("superuser");
        login.setServer("http://epicfail:8088/jasperserver-pro");

        PowerMockito.mockStatic(SessionFactory.class);
        PowerMockito.when(SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null))
                .thenThrow(new ProcessingException("java.net.UnknownHostException: epicfail"));

        // When
        OperationResult result = login.execute(null);

        // Then
        PowerMockito.verifyStatic();
        SessionFactory.createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);

        Assert.assertEquals("Login failed (java.net.UnknownHostException: epicfail)", result.getResultMessage());
        Assert.assertEquals(ResultCode.FAILED, result.getResultCode());
        Assert.assertEquals(login, result.getContext());
    }

    @After public void after() {
        Mockito.reset(sessionMock);
        login = null;
    }
}
