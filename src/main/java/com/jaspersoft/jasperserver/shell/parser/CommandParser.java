package com.jaspersoft.jasperserver.shell.parser;

import com.jaspersoft.jasperserver.shell.command.Command;
import com.jaspersoft.jasperserver.shell.command.HelpCommand;
import com.jaspersoft.jasperserver.shell.command.LoginCommand;
import com.jaspersoft.jasperserver.shell.context.Context;
import com.jaspersoft.jasperserver.shell.context.ContextAware;
import com.jaspersoft.jasperserver.shell.exception.parser.UnknownInputContentException;
import com.jaspersoft.jasperserver.shell.exception.parser.UnknownParserException;
import com.jaspersoft.jasperserver.shell.parameter.Parameter;
import com.jaspersoft.jasperserver.shell.validator.CommandParameterValidator;

import java.util.LinkedList;
import java.util.Queue;

import static com.jaspersoft.jasperserver.shell.factory.CommandFactory.create;

/**
 * @author Alexander Krasnyanskiy
 */
public class CommandParser implements ContextAware {

    private Context context;
    private CommandParameterValidator validator;

    public CommandParser(CommandParameterValidator validator) {
        this.validator = validator;
    }

    public Queue<Command> parse(String... splits) {
        if (splits.length == 1) {
            splits = splits[0].split("\\s+");
        }
        Queue<Command> cmdQueue = new LinkedList<>();
        boolean anonymousParam = true;
        Command current = null;
        LoginCommand defaultCmd = null;
        Parameter currentParam = null;
        for (String v : splits) {
            if (context.getDictionary().contains(v)) {
                if (current != null && !(current instanceof HelpCommand)) {
                    cmdQueue.offer(current);
                } else if (current != null) {
                    // add param to help command
                    current.getParameters().get(0).setAvailable(true)/* <?> */.getValues().add(v);
                    continue;
                }
                current = create(v);
                if (current instanceof HelpCommand) {
                    ((HelpCommand) current).setContext(context);
                }
                anonymousParam = false;
            } else if (anonymousParam) {
                if (defaultCmd == null) {
                    defaultCmd = new LoginCommand();
                    cmdQueue.offer(defaultCmd);
                }
                Parameter p = defaultCmd.parameter(v);
                if (p != null) {
                    currentParam = p;
                    currentParam.setAvailable(true); // todo :: <?>
                } else if (currentParam == null) {
                    throw new UnknownInputContentException(v);
                } else {
                    currentParam.getValues().add(v);
                    currentParam.setAvailable(true); // todo :: <?>
                }
            } else {
                Parameter p = current.parameter(v);
                if (p != null) {
                    if ("anonymous".equals(p.getName())) {
                        p.getValues().add(v);
                        p.setAvailable(true); // todo :: <?>
                    }
                    currentParam = p;
                    currentParam.setAvailable(true); // todo :: <?>
                } else if (currentParam == null) {
                    throw new UnknownParserException(); // todo :: <?>
                } else {
                    currentParam.getValues().add(v);
                    currentParam.setAvailable(true);
                }
            }
        }
        cmdQueue.offer(current);
        /*
        cmdQueue.stream().filter(command -> command != null).forEach(command -> {
            validator.validate(command);
            //ParameterCleaner.cleanUp(command);
        });
        */
        cmdQueue.stream()
                .filter(c -> c != null)
                .forEach(validator::validate);
        return cmdQueue;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    /*
    private static class ParameterCleaner {
        static void cleanUp(Command command) {
            List<Parameter> params = command.getParameters();
            List<Parameter> temp = params.stream()
                    .filter(Parameter::isAvailable)
                    .collect(toList());
            command.setParameters(temp);
        }
    }
    */
}