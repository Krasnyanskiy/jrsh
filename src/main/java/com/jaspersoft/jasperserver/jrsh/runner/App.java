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
package com.jaspersoft.jasperserver.jrsh.runner;

import com.jaspersoft.jasperserver.jrsh.evaluation.strategy.EvaluationStrategy;
import com.jaspersoft.jasperserver.jrsh.evaluation.strategy.EvaluationStrategyFactory;
import com.jaspersoft.jasperserver.jrsh.operation.result.OperationResult;

import java.util.logging.LogManager;

import static com.jaspersoft.jasperserver.jrsh.common.ArgumentUtil.convertToScript;

/**
 * This class used to bootstrap and launch a JRSH application.
 */
public class App {

    /**
     * Application entry point. It defines execution strategy
     * and evaluates scripted expression.
     *
     * @param args application arguments
     */
    public static void main(String[] args) {
        disableLogger();
        EvaluationStrategy strategy = EvaluationStrategyFactory.getStrategy(args);
        OperationResult result = strategy.eval(convertToScript(args));
        System.exit(result.getResultCode().getValue());
    }

    /**
     * Disables Jersey logger to prevent console pollution,
     * which appeared in 6.0.4 version of Rest Client after
     * Jersey version upgrade.
     */
    private static void disableLogger() {
        LogManager.getLogManager().reset();
    }

}
