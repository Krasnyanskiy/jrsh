//package com.jaspersoft.jasperserver.jrsh.evaluation.strategy.impl;
//
//import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
//import com.jaspersoft.jasperserver.jrsh.operation.impl.ExportOperation;
//import com.jaspersoft.jasperserver.jrsh.operation.impl.LoginOperation;
//import com.jaspersoft.jasperserver.jrsh.operation.parser.OperationParser;
//import com.jaspersoft.jasperserver.jrsh.operation.result.OperationResult;
//import com.jaspersoft.jasperserver.jrsh.operation.result.ResultCode;
//import jline.console.ConsoleReader;
//import jline.console.UserInterruptException;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//
//import java.io.IOException;
//
//import static com.jaspersoft.jasperserver.jrsh.common.SessionHolder.save;
//import static java.util.Collections.singletonList;
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.*;
//import static org.mockito.MockitoAnnotations.initMocks;
//
///**
// * Unit tests for {@link ShellEvaluationStrategy} class.
// */
//public class ShellEvaluationStrategyTest {
//
//    public static final String OPERATION_1 = "login superuser%superuser@localhost:8080/jrs-test";
//    public static final String OPERATION_2 = "login wrong%credentials@localhost:8080/jrs-test";
//    public static final String OPERATION_3 = "export all";
//
//    private ShellEvaluationStrategy strategySpy = spy(new ShellEvaluationStrategy());
//    private Exception thrown = new UserInterruptException("Let's pretend we've pressed `Ctrl+C` key");
//
//    @Mock private OperationParser operationParserMock;
//    @Mock private LoginOperation loginOperationMock;
//    @Mock private ExportOperation exportOperationMock;
//    @Mock private OperationResult loginOperationResultMock;
//    @Mock private OperationResult failedLoginOperationResultMock;
//    @Mock private OperationResult exportOperationResultMock;
//    @Mock private Session sessionMock;
//    @Mock private ConsoleReader consoleReaderMock;
//
//
//    @Before
//    public void before() {
//        initMocks(this);
//        strategySpy.setParser(operationParserMock);
//        strategySpy.setConsole(consoleReaderMock);
//        save(sessionMock);
//    }
//
//    @Test
//    public void shouldExecuteTwoOperationsInShellModeAndInterruptItDueToTheExitKeyBeenPressed() throws Exception {
//
//        // given
//        doReturn(OPERATION_3).doThrow(thrown).when(consoleReaderMock).readLine();
//        doReturn(loginOperationMock).when(operationParserMock).parseOperation(OPERATION_1);
//        doReturn(exportOperationMock).when(operationParserMock).parseOperation(OPERATION_3);
//        doReturn(loginOperationResultMock).when(loginOperationMock).execute(sessionMock);
//        doReturn(exportOperationResultMock).when(exportOperationMock).execute(sessionMock);
//        doReturn("Message1").when(loginOperationResultMock).getResultMessage();
//        doReturn("Message2").when(exportOperationResultMock).getResultMessage();
//        doReturn(ResultCode.SUCCESS).when(loginOperationResultMock).getResultCode();
//        doReturn(ResultCode.SUCCESS).when(exportOperationResultMock).getResultCode();
//
//        doNothing().when(strategySpy).print("Message1");
//        doNothing().when(strategySpy).print("Message2");
//
//        // when
//        OperationResult result = strategySpy.eval(singletonList(OPERATION_1));
//
//        // then
//        assertEquals(result.getResultCode(), ResultCode.INTERRUPTED);
//
//        verify(loginOperationMock, times(1)).execute(sessionMock);
//        verify(exportOperationMock, times(1)).execute(sessionMock);
//        verify(loginOperationResultMock, times(1)).getResultCode();
//        verify(loginOperationResultMock, times(1)).getResultMessage();
//        verify(exportOperationResultMock, times(1)).getResultCode();
//        verify(exportOperationResultMock, times(1)).getResultMessage();
//        verify(strategySpy, times(1)).print("Message1");
//        verify(strategySpy, times(1)).print("Message2");
//        verify(strategySpy, times(1)).logout();
//        verifyNoMoreInteractions(loginOperationMock);
//        verifyNoMoreInteractions(exportOperationMock);
//    }
//
//    @Test
//    public void shouldExitShellModeIfLoginFailed() throws IOException {
//
//        // given
//        doReturn(loginOperationMock).when(operationParserMock).parseOperation(OPERATION_2);
//        doReturn(failedLoginOperationResultMock).when(loginOperationMock).execute(sessionMock);
//        doReturn(ResultCode.FAILED).when(failedLoginOperationResultMock).getResultCode();
//        doReturn("Failed").when(failedLoginOperationResultMock).getResultMessage();
//        doNothing().when(strategySpy).print("Failed");
//
//        // when
//        OperationResult result = strategySpy.eval(singletonList(OPERATION_2));
//
//        // then
//        assertEquals(result.getResultCode(), ResultCode.FAILED);
//        assertEquals(result.getResultMessage(), "Failed");
//        assertEquals(result.getContext(), loginOperationMock);
//
//        verify(loginOperationMock, times(1)).execute(sessionMock);
//        verify(operationParserMock, times(1)).parseOperation(OPERATION_2);
//        verify(failedLoginOperationResultMock, times(2)).getResultMessage();
//        verify(strategySpy, times(1)).print("Failed");
//    }
//
//    @After
//    public void after() {
//        reset(
//                operationParserMock,
//                loginOperationMock,
//                sessionMock,
//                loginOperationResultMock,
//                exportOperationResultMock,
//                consoleReaderMock,
//                exportOperationMock,
//                failedLoginOperationResultMock
//        );
//    }
//}
