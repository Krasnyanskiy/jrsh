package com.jaspersoft.jasperserver.jrsh.core.operation.impl;

import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jrsh.core.operation.Operation;
import com.jaspersoft.jasperserver.jrsh.core.operation.OperationFactory;
import com.jaspersoft.jasperserver.jrsh.core.operation.result.OperationResult;
import com.jaspersoft.jasperserver.jrsh.core.operation.annotation.Master;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Set;

import static com.jaspersoft.jasperserver.jrsh.core.operation.result.ResultCode.SUCCESS;

@Data
@Master(name = "help",
        tail = true,
        usage = "help [operation]",
        description = "Operation <help> demonstrates how to use cli")
public class HelpOperation implements Operation {

    private static final String PREFIX = StringUtils.repeat(" ", 3);
    private static final String LF = "\n\n";

    @Override
    public OperationResult execute(Session session) {
        Set<Operation> operations = OperationFactory.createOperationsByAvailableTypes();

        StringBuilder builder = new StringBuilder("*** HOW TO USE ***")
                //.append(LF)
                .append("To start working with 'jrsh' in shell mode you need to specify server: ")
                //.append(LF)
                .append("   $ jrsh username%password@url")
                .append(LF)
                .append("Available operations:")
                .append(LF);

        for (Operation operation : operations) {
            Master master = operation.getClass().getAnnotation(Master.class);

            Field field;
            String description;
            String usage;

            if (master != null) {
                description = master.description();
                usage = master.usage();
                builder
                        .append(PREFIX)
                        .append(description)
                        .append("\n")
                        .append(PREFIX)
                        .append("usage: ")
                        .append(usage)
                        .append(LF);
            }
        }

        return new OperationResult(
                builder.toString(),
                SUCCESS,
                this,
                null);
    }
}
