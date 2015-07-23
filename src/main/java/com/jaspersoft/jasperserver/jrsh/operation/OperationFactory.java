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
package com.jaspersoft.jasperserver.jrsh.operation;

import com.jaspersoft.jasperserver.jrsh.operation.annotation.Master;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.CouldNotCreateOperationInstanceException;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.OperationNotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.jaspersoft.jasperserver.jrsh.core.operation.OperationTypeReader.readTypes;

public class OperationFactory {

    private static final Map<String, Class<? extends Operation>> operations;
    public static final String basePackage = "com.jaspersoft.jasperserver.jrsh.core.operation.impl";

    static {
        operations = new HashMap<String, Class<? extends Operation>>();
        for (Class<? extends Operation> operationType : readTypes(basePackage)) {
            Master annotation = operationType.getAnnotation(Master.class);
            if (annotation != null) {
                String operationName = annotation.name();
                operations.put(operationName, operationType);
            }
        }
    }

    public static Operation createOperationByName(String operationName) {
        Class<? extends Operation> operationType = operations.get(operationName);
        if (operationType == null) {
            throw new OperationNotFoundException();
        }
        return createInstance(operationType);
    }

    public static Set<Operation> createOperationsByAvailableTypes() {
        HashSet<Operation> setOfOperations = new HashSet<Operation>();
        for (Class<? extends Operation> type : operations.values()) {
            setOfOperations.add(createInstance(type));
        }
        return setOfOperations;
    }

    protected static Operation createInstance(Class<? extends Operation> operationType) {
        try {
            return operationType.newInstance();
        } catch (Exception err) {
            throw new CouldNotCreateOperationInstanceException(err);
        }
    }
}
