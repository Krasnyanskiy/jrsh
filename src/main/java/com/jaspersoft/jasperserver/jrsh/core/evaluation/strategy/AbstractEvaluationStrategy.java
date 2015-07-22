package com.jaspersoft.jasperserver.jrsh.core.evaluation.strategy;

import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.lexer.PathConsideringLexer;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.LL1OperationParser;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.OperationParser;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.parser.PlainGrammarParser;
import lombok.Data;

@Data
public abstract class AbstractEvaluationStrategy implements EvaluationStrategy {
    protected OperationParser parser;

    public AbstractEvaluationStrategy() {
        this.parser = new LL1OperationParser(new PathConsideringLexer(), new PlainGrammarParser());
    }
}
