package com.jaspersoft.jasperserver.jrsh;

import com.jaspersoft.jasperserver.jrsh.evaluation.strategy.impl.ShellEvaluationStrategyTest;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.lexer.LexerTest;
import com.jaspersoft.jasperserver.jrsh.operation.impl.ImportOperationTest;
import com.jaspersoft.jasperserver.jrsh.operation.impl.LoginOperationTest;
import com.jaspersoft.jasperserver.jrsh.operation.parser.ConditionsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite for unit tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConditionsTest.class,
        LoginOperationTest.class,
        ShellEvaluationStrategyTest.class,
        ImportOperationTest.class,
        LexerTest.class
})
public class UnitTestSuite {
}
