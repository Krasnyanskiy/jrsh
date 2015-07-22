package com.jaspersoft.jasperserver.jrsh.core.operation.parser;

import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationNotFoundException;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.WrongOperationFormatException;

public class Conditions {
    public static void checkOperation(Operation operation) {
        if (operation == null) {
            throw new OperationNotFoundException();
        }
    }

    public static void checkTokenMatching(boolean matchedRuleExist) {
        if (!matchedRuleExist) {
            throw new WrongOperationFormatException();
        }
    }
}
