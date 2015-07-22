package com.jaspersoft.jasperserver.jrsh.core.evaluation.strategy.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jrsh.core.common.SessionFactory;
import com.jaspersoft.jasperserver.jrsh.core.evaluation.strategy.AbstractEvaluationStrategy;
import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.result.OperationResult;

import java.util.List;

import static com.jaspersoft.jasperserver.jrsh.core.operation.OperationFactory.createOperationByName;
import static com.jaspersoft.jasperserver.jrsh.core.operation.result.ResultCode.FAILED;

public class ToolEvaluationStrategy extends AbstractEvaluationStrategy {

    @Override
    public OperationResult eval(List<String> source) {
        Operation operationInstance = null;
        OperationResult result = null;

        try {
            for (String operation : source) {
                Session session = SessionFactory.getSharedSession();
                operationInstance = parser.parseOperation(operation);
                OperationResult temp = result;
                result = operationInstance.execute(session);
                System.out.println(result.getResultMessage());
                result.setPrevious(temp);
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            Operation help = createOperationByName("help");
            System.out.println(help.execute(null).getResultMessage());
            if (result != null) {
                result = new OperationResult(
                      error.getMessage(),
                      FAILED,
                      operationInstance,
                      result
                );
            } else {
                result = new OperationResult(
                      error.getMessage(),
                      FAILED,
                      operationInstance,
                      null
                );
            }
        }
        return result;
    }
}
