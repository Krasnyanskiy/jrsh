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

import com.jaspersoft.jasperserver.jrsh.operation.annotation.Parameter;
import com.jaspersoft.jasperserver.jrsh.operation.annotation.Value;
import com.jaspersoft.jasperserver.jrsh.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.CannotFindSetterException;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.NoSuitableSetterException;
import com.jaspersoft.jasperserver.jrsh.operation.parser.exception.OperationParseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Initializer for setting the operation state.
 *
 * @author Alexander Krasnyanskiy
 */
public class OperationStateInitializer {

    /**
     * Initialize the given operation.
     * @param operation           operation
     * @param operationRuleTokens operation tokens
     * @param userInputTokens     user input
     */
    public static void initialize(Operation operation, List<Token> operationRuleTokens, List<String> userInputTokens) {
        Class<? extends Operation> clazz = operation.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Parameter param = field.getAnnotation(Parameter.class);
            if (param != null) {
                Value[] values = param.values();
                for (Value value : values) {
                    String alias = value.tokenAlias();
                    int idx = getTokenIndex(operationRuleTokens, alias);
                    if (idx >= 0) {
                        field.setAccessible(true); // setup accessibility
                        Method setter = findSetterForField(clazz.getMethods(), field.getName());
                        if (setter == null) {
                            throw new CannotFindSetterException(field.getName());
                        } else try {
                            setter.invoke(operation, userInputTokens.get(idx));
                        } catch (Exception err) {
                            //
                            // Reflection wraps any custom exceptions,
                            // however we can get them through the cause
                            //
                            rethrowException(err);
                        }
                        field.setAccessible(false);
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    //                            Helper methods
    // ---------------------------------------------------------------------

    protected static void rethrowException(Exception err) {
        Throwable cause = err.getCause();
        if (OperationParseException.class.isAssignableFrom(cause.getClass())) {
            throw (RuntimeException) cause;
        }
    }

    protected static int getTokenIndex(List<Token> tokens, String tokenAlias) {
        for (int idx = 0; idx < tokens.size(); idx++) {
            Token token = tokens.get(idx);
            if (tokenAlias.equals(token.getName()/*alias*/)) {
                return idx;
            }
        }
        return -1;
    }

    protected static Method findSetterForField(Method[] methods, String fieldName) {
        for (Method method : methods) {
            String methodName = method.getName();
            if ("set".concat(fieldName.toLowerCase()).equals(methodName.toLowerCase())) {
                return method;
            }
        }
        throw new NoSuitableSetterException();
    }
}
