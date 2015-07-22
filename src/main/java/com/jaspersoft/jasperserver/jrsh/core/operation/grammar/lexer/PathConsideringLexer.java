package com.jaspersoft.jasperserver.jrsh.core.operation.grammar.lexer;

import java.util.ArrayList;
import java.util.List;

public class PathConsideringLexer implements Lexer {

    @Override
    public List<String> convert(String line) {
        String word = "";
        ArrayList<String> tokens = new ArrayList<String>();
        for (String part : line.split("\\s+")) {
            if (part.endsWith("\\")) {
                word = word.concat(part).concat(" ");
            } else {
                if (word.isEmpty()) {
                    tokens.add(part);
                } else {
                    word = word.concat(part);
                    tokens.add(word);
                    word = "";
                }
            }
        }
        return tokens;
    }
}
