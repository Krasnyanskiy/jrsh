package com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.impl;

import com.jaspersoft.jasperserver.jrsh.core.completion.impl.IgnoreCompleter;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.AbstractToken;
import jline.console.completer.Completer;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AnyStringToken extends AbstractToken {

    public AnyStringToken(String name, String value, boolean mandatory, boolean tailOfRule) {
        super(name, value, mandatory, tailOfRule);
    }

    @Override
    public boolean isTailOfRule() {
        return tailOfRule;
    }

    @Override
    public Completer getCompleter() {
        return new IgnoreCompleter();
    }

    @Override
    public boolean match(String input) {
        return true;
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
