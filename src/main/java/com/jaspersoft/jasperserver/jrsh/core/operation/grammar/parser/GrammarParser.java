package com.jaspersoft.jasperserver.jrsh.core.operation.grammar.parser;

import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.Grammar;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationParseException;

public interface GrammarParser {

    Grammar parseGrammar(Operation operation) throws OperationParseException;

}
