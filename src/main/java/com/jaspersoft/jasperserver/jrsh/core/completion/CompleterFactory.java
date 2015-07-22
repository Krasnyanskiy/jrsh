package com.jaspersoft.jasperserver.jrsh.core.completion;

import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationFactory;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.Grammar;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.rule.Rule;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.parser.PlainGrammarParser;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;

import java.util.List;
import java.util.Set;

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
