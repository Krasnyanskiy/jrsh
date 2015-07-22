package com.jaspersoft.jasperserver.jrsh.core.completion.impl;

import jline.console.completer.Completer;

import java.util.List;

/**
 * This computer is involved in the chain of completion
 * as a part of argument completer, although it's do
 * nothing useful.
 * <p/>
 * We need it to mark user input and to continue the
 * search of next proper completer in the completion chain.
 */
public class IgnoreCompleter implements Completer {
    @Override
    public int complete(String s, int i, List<CharSequence> list) {
        // ignore completion
        return 0;
    }
}
