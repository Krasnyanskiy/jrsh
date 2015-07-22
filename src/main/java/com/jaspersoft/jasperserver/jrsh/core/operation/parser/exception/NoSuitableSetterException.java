package com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception;

import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationParseException;

public class NoSuitableSetterException extends OperationParseException {
    public NoSuitableSetterException() {
        super("There is no suitable setter");
    }
}
