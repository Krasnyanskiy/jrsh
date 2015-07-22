package com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception;

import static java.lang.String.format;

public class CouldNotCreateOperationInstanceException extends OperationParseException {
    public CouldNotCreateOperationInstanceException(Exception err) {
        super(format("Could not create an operation instance (%s)", err.getMessage()));
    }
}
