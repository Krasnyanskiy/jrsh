package com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token;

import jline.console.completer.Completer;

public interface Token {

    String getName(); // alias in metadata

    String getValue();

    boolean isMandatory();

    boolean isTailOfRule();

    Completer getCompleter();

    boolean match(String input);

}
