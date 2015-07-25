package com.jaspersoft.jasperserver.jrsh.operation.parser;

import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.OperationNotFoundException;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.WrongOperationFormatException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link Conditions} class.
 */
public class ConditionsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowAnExceptionIfOperationIsNull() {
        thrown.expect(OperationNotFoundException.class);
        Conditions.checkOperation(null);
    }

    @Test
    public void shouldThrowAnExceptionIfMatchedRuleDoesNotExist() {
        thrown.expect(WrongOperationFormatException.class);
        Conditions.checkTokenMatching(false);
    }

}
