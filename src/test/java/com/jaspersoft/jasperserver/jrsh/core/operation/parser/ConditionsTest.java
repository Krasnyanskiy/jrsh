package com.jaspersoft.jasperserver.jrsh.core.operation.parser;

import com.jaspersoft.jasperserver.jrsh.operation.parser.Conditions;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.WrongOperationFormatException;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.OperationNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConditionsTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Test public void shouldThrowAnExceptionIfOperationIsNull() {
        thrown.expect(OperationNotFoundException.class);
        Conditions.checkOperation(null);
    }

    @Test public void shouldThrowAnExceptionIfMatchedRuleDoesNotExist() {
        thrown.expect(WrongOperationFormatException.class);
        Conditions.checkTokenMatching(false);
    }

}
