package com.jaspersoft.jasperserver.jrsh.operation.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.AuthenticationFailedException;
import com.jaspersoft.jasperserver.jrsh.common.SessionHolder;
import com.jaspersoft.jasperserver.jrsh.common.SessionHolder.SessionFlyweightBuilder;
import com.jaspersoft.jasperserver.jrsh.operation.result.OperationResult;
import com.jaspersoft.jasperserver.jrsh.operation.result.ResultCode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.ProcessingException;

import static com.jaspersoft.jasperserver.jrsh.common.SessionHolder.createSharedSession;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Unit tests for {@link LoginOperation} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionHolder.class)
public class LoginOperationTest {

    private LoginOperation login;
    private Exception thrown = new ProcessingException("java.net.UnknownHostException: epicfail");

    @Mock private Session sessionMock;

    @Before
    public void before() {
        initMocks(this);
        login = new LoginOperation();
    }

    @Test
    public void shouldLoginAndReturnCorrectOperationResult() {

        // given
        login.setUsername("superuser");
        login.setPassword("superuser");
        login.setServer("http://localhost:8080/jasperserver-pro");

        mockStatic(SessionHolder.class);
        when(createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null))
                .thenReturn(sessionMock);

        // when
        OperationResult receivedResult = login.execute(null);

        // then
        verifyStatic();
        createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);
        assertEquals("You have logged in as superuser", receivedResult.getResultMessage());
        assertEquals(ResultCode.SUCCESS, receivedResult.getResultCode());
        assertEquals(login, receivedResult.getContext());
    }

    @Test
    public void shouldFailLoginDueToTheWrongCredentials() {

        // given
        login.setUsername("wrongUsername");
        login.setPassword("wrongPassword");
        login.setServer("http://localhost:8080/jasperserver-pro");

        mockStatic(SessionHolder.class);
        when(createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null))
                .thenThrow(new AuthenticationFailedException("Unauthorized"));

        // when
        OperationResult receivedResult = login.execute(null);

        // then
        verifyStatic();
        createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);
        assertEquals("Login failed (Unauthorized)", receivedResult.getResultMessage());
        assertEquals(ResultCode.FAILED, receivedResult.getResultCode());
        assertEquals(login, receivedResult.getContext());
    }

    @Test
    public void shouldFailWithProcessingExceptionAndReturnCorrectOperationResult() {

        // given
        login.setUsername("superuser");
        login.setPassword("superuser");
        login.setServer("http://epicfail:8088/jasperserver-pro");

        mockStatic(SessionHolder.class);
        Session session = new SessionFlyweightBuilder().withPassword("").withUrl("").withUsername("").build();
        when(createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null)).thenThrow(thrown);

        // when
        OperationResult receivedResult = login.execute(null);

        // then
        verifyStatic();
        createSharedSession(login.getServer(), login.getUsername(), login.getPassword(), null);
        assertEquals("Login failed (java.net.UnknownHostException: epicfail)", receivedResult.getResultMessage());
        assertEquals(ResultCode.FAILED, receivedResult.getResultCode());
        assertEquals(login, receivedResult.getContext());
    }

    @After
    public void after() {
        reset(sessionMock);
    }
}
