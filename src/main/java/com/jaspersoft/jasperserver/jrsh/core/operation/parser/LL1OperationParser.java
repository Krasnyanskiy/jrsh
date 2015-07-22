
package com.jaspersoft.jasperserver.jrsh.core.operation.parser;

import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationFactory;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationStateConfigurer;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.Grammar;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.lexer.Lexer;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.parser.GrammarParser;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.rule.Rule;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationParseException;

import java.util.List;

public class LL1OperationParser implements OperationParser {

    private Lexer lexer;
    private GrammarParser grammarParser;

    public LL1OperationParser(Lexer lexer, GrammarParser grammarParser) {
        this.grammarParser = grammarParser;
        this.lexer = lexer;
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    public Operation parseOperation(String line) throws OperationParseException {
        List<String> userInputTokens = lexer.convert(line);
        String operationName = userInputTokens.get(0);
        Operation operation = OperationFactory.createOperationByName(operationName);
        Conditions.checkOperation(operation);
        //
        // Build operation grammar based on operation metadata
        // that is stored in @annotations.
        //
        Grammar grammar = grammarParser.parseGrammar(operation);
        List<Rule> rules = grammar.getRules();
        boolean isTokenMatched = false;

        for (Rule rule : rules) {
            List<Token> operationRuleTokens = rule.getTokens();
            //
            // Check if operation grammar tokens are matched
            // to user input (input tokens).
            //
            isTokenMatched = matchTokens(operationRuleTokens, userInputTokens);
            if (isTokenMatched) {
                //
                // Configure operation state
                //
                OperationStateConfigurer.configure(
                        operation,
                        operationRuleTokens,
                        userInputTokens);
                break;
            }
        }
        Conditions.checkTokenMatching(isTokenMatched);
        return operation;
    }

    protected boolean matchTokens(List<Token> operationRuleTokens, List<String> userInputTokens) {
        if (operationRuleTokens.size() != userInputTokens.size()) {
            return false;
        }
        for (int i = 0; i < operationRuleTokens.size(); i++) {
            if (!operationRuleTokens.get(i).match(userInputTokens.get(i))) {
                return false;
            }
        }
        return true;
    }
}
