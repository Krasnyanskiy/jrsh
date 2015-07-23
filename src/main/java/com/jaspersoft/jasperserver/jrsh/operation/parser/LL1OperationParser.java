/*
 * Copyright (C) 2005 - 2015 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jrsh.operation.parser;

import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationFactory;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationStateConfigurer;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.Grammar;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.lexer.Lexer;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.parser.GrammarParser;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.rule.Rule;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.OperationParseException;

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
