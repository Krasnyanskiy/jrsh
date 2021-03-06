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
package com.jaspersoft.jasperserver.jrsh.evaluation.strategy.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jrsh.common.SessionFactory;
import com.jaspersoft.jasperserver.jrsh.evaluation.strategy.AbstractEvaluationStrategy;
import com.jaspersoft.jasperserver.jrsh.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.operation.result.OperationResult;

import java.util.List;

import static com.jaspersoft.jasperserver.jrsh.operation.OperationFactory.createOperationByName;
import static com.jaspersoft.jasperserver.jrsh.operation.result.ResultCode.FAILED;

/**
 * @author Alexander Krasnyanskiy
 */
public class ToolEvaluationStrategy extends AbstractEvaluationStrategy {

    @Override
    public OperationResult eval(List<String> source) {
        Operation operationInstance = null;
        OperationResult result = null;

        try {
            for (String operation : source) {
                Session session = SessionFactory.getSharedSession();
                operationInstance = parser.parseOperation(operation);
                OperationResult temp = result;
                result = operationInstance.execute(session);
                System.out.println(result.getResultMessage());
                result.setPrevious(temp);
            }
        } catch (Exception error) {
            try {
                System.out.println(error.getMessage());
                Operation help = createOperationByName("help");
                System.out.println(help.execute(null).getResultMessage());
                if (result != null) {
                    result = new OperationResult(
                            error.getMessage(),
                            FAILED,
                            operationInstance,
                            result
                    );
                } else {
                    result = new OperationResult(
                            error.getMessage(),
                            FAILED,
                            operationInstance,
                            null
                    );
                }
            } catch (Exception err) {
                //
                // We get here if there are no available operations
                // in system.
                //
                result = new OperationResult(
                        err.getMessage(),
                        FAILED,
                        operationInstance,
                        result
                );
            }
        }
        return result;
    }
}
