package com.jaspersoft.jasperserver.jrsh.core.operation;

import com.jaspersoft.jasperserver.jrsh.core.operation.annotation.Parameter;
import com.jaspersoft.jasperserver.jrsh.core.operation.annotation.Value;
import com.jaspersoft.jasperserver.jrsh.core.operation.grammar.token.Token;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.CannotFindSetterException;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.NoSuitableSetterException;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationParseException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class OperationStateConfigurer {

    /**
     * Сonfigures operation state based on the operation metadata.
     * All metadata is stored in annotations.
     *
     * @param operation           unconfigured operation
     * @param operationRuleTokens operation tokens
     * @param userInputTokens     user input
     */
    public static void configure(Operation operation,
                                 List<Token> operationRuleTokens,
                                 List<String> userInputTokens) {
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
