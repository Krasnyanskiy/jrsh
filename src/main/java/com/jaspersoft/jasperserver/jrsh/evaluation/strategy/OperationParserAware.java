package com.jaspersoft.jasperserver.jrsh.evaluation.strategy;

import com.jaspersoft.jasperserver.jrsh.operation.parser.OperationParser;

/**
 * Interface to be implemented by any object that wishes to be
 * notified of the {@link OperationParser}.
 *
 * @author Alexander Krasnyanskiy
 */
public interface OperationParserAware {

    /**
     * @param parser parser to set
     */
    void setParser(OperationParser parser);

}
