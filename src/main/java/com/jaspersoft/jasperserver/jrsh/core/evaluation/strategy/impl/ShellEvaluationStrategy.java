package com.jaspersoft.jasperserver.jrsh.core.evaluation.strategy.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jrsh.core.common.ConsoleBuilder;
import com.jaspersoft.jasperserver.jrsh.core.common.SessionFactory;
import com.jaspersoft.jasperserver.jrsh.core.completion.CompleterFactory;
import com.jaspersoft.jasperserver.jrsh.core.completion.CustomCompletionHandler;
import com.jaspersoft.jasperserver.jrsh.core.evaluation.strategy.AbstractEvaluationStrategy;
import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.annotation.Master;
import com.jaspersoft.jasperserver.jrsh.core.operation.impl.LoginOperation;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationParseException;
import com.jaspersoft.jasperserver.jrsh.core.operation.result.OperationResult;
import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import jline.console.completer.Completer;

import java.io.IOException;
import java.util.List;

import static com.jaspersoft.jasperserver.jrsh.core.operation.result.ResultCode.FAILED;
import static com.jaspersoft.jasperserver.jrsh.core.operation.result.ResultCode.INTERRUPTED;

public class ShellEvaluationStrategy extends AbstractEvaluationStrategy {

    private ConsoleReader console;

    public ShellEvaluationStrategy() {
        this.console = new ConsoleBuilder()
                .withPrompt("$> ")
                .withHandler(new CustomCompletionHandler())
                .withInterruptHandling()
                .withCompleter(getCompleter())
                .build();
    }

    @Override
    public OperationResult eval(List<String> source) {
        String line = source.get(0);
        Operation operation = null;
        OperationResult result = null;
        while (true) {
            try {
                Session session = SessionFactory.getSharedSession();
                if (line == null) {
                    line = console.readLine();
                }
                if (line.isEmpty()) {
                    print("");
                } else {
                    OperationResult temp = result;
                    operation = parser.parseOperation(line);
                    result = operation.execute(session);
                    result.setPrevious(temp);
                    print(result.getResultMessage());

                    if (result.getResultCode() == FAILED) {
                        if (operation instanceof LoginOperation) {
                            return new OperationResult(
                                    result.getResultMessage(),
                                    FAILED,
                                    operation,
                                    null);
                        } else {
                            Master master = operation.getClass().getAnnotation(Master.class);
                            String usage = master.usage();
                            print("usage: " + usage);
                        }
                    }
                }
                line = null;
            } catch (UserInterruptException unimportant) {
                logout();
                return new OperationResult(
                        "Interrupted by user",
                        INTERRUPTED,
                        operation,
                        null);
            } catch (OperationParseException err) {
                try {
                    print(err.getMessage());
                } finally {
                    line = null;
                }
            } catch (IOException ignored) {
            } finally {
                operation = null;
            }
        }
    }

    // ---------------------------------------------------------------------
    //                           Helper methods
    // ---------------------------------------------------------------------

    protected void print(String message) {
        try {
            console.println(message);
            console.flush();
        } catch (IOException ignored) {
        }
    }

    protected Completer getCompleter() {
        return CompleterFactory.create();
    }

    protected void logout() {
        try {
            SessionFactory.getSharedSession().logout();
        } catch (Exception ignored) {
        }
    }

    public void setConsole(ConsoleReader console) {
        this.console = console;
    }
}
