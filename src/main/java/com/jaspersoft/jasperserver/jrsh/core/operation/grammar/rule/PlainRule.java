package com.jaspersoft.jasperserver.jrsh.core.operation.grammar.rule;

import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.Token;

import java.util.ArrayList;
import java.util.List;

public class PlainRule implements Rule {
    private List<Token> tokens;

    public PlainRule() {
        tokens = new ArrayList<Token>();
    }

    @Override
    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public void addToken(Token token) {
        tokens.add(token);
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
