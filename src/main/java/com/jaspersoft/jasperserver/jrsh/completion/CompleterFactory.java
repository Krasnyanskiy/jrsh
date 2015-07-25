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
package com.jaspersoft.jasperserver.jrsh.completion;

import com.jaspersoft.jasperserver.jrsh.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.operation.OperationFactory;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.Grammar;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.rule.Rule;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.parser.PlainGrammarParser;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;

import java.util.List;
import java.util.Set;

/**
 * @author Alexander Krasnyanskiy
 */
public class CompleterFactory {

    public static Completer create() {
        Set<Operation> operations = OperationFactory.createOperationsByAvailableTypes();
        AggregateCompleter aggregatedCompleter = new AggregateCompleter();

        for (Operation operation : operations) {
            Grammar grammar = new PlainGrammarParser().parseGrammar(operation);
            List<Rule> rules = grammar.getRules();
            ArgumentCompleter ruleCompleter = new ArgumentCompleter();

            for (Rule rule : rules) {
                List<Token> tokens = rule.getTokens();

                for (Token token : tokens) {
                    Completer completer = token.getCompleter();
                    ruleCompleter.getCompleters().add(completer);
                }

                ruleCompleter.getCompleters().add(new NullCompleter());
                aggregatedCompleter.getCompleters().add(ruleCompleter);
                ruleCompleter = new ArgumentCompleter();
            }
        }

        return aggregatedCompleter;
    }

}
