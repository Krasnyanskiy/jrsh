package com.jaspersoft.jasperserver.shell.command;

import com.jaspersoft.jasperserver.shell.ExecutionMode;
import com.jaspersoft.jasperserver.shell.exception.CannotCreateFileException;
import com.jaspersoft.jasperserver.shell.exception.CannotSaveProfileConfigurationException;
import com.jaspersoft.jasperserver.shell.exception.MandatoryParameterMissingException;
import com.jaspersoft.jasperserver.shell.exception.NoProfileWithSuchNameException;
import com.jaspersoft.jasperserver.shell.exception.NotSpecifiedProfileNameException;
import com.jaspersoft.jasperserver.shell.exception.SessionIsNotAvailableException;
import com.jaspersoft.jasperserver.shell.exception.UnknownInterfaceException;
import com.jaspersoft.jasperserver.shell.exception.WrongPasswordException;
import com.jaspersoft.jasperserver.shell.exception.WrongPathParameterException;
import com.jaspersoft.jasperserver.shell.exception.parser.ParameterValueSizeException;
import com.jaspersoft.jasperserver.shell.exception.parser.WrongRepositoryPathFormatException;
import com.jaspersoft.jasperserver.shell.exception.profile.CannotLoadProfileConfiguration;
import com.jaspersoft.jasperserver.shell.exception.profile.NotUniqueProfileNameException;
import com.jaspersoft.jasperserver.shell.exception.profile.WrongProfileNameException;
import com.jaspersoft.jasperserver.shell.exception.server.JrsResourceNotFoundException;
import com.jaspersoft.jasperserver.shell.parameter.Parameter;
import com.jaspersoft.jasperserver.shell.profile.entity.Profile;
import jline.console.ConsoleReader;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.shell.ExecutionMode.SHELL;
import static com.jaspersoft.jasperserver.shell.profile.factory.ProfileFactory.getInstance;
import static java.lang.System.out;

/**
 * @author Alexander Krasnyanskiy
 */
@Data
@ToString(exclude = {"description", "parameters"})
public abstract class Command implements Executable, ConsoleReaderAware {

    protected String name;
    protected String description; // todo: rename to `briefDescription`
    protected String usageDescription;
    protected List<Parameter> parameters = new ArrayList<>();
    protected static Profile profile = getInstance();

    private ExecutionMode mode;
    protected ConsoleReader reader;

    public abstract void run();

    @Override
    public final void execute() {
        try {
            run();
        } catch (Exception e) {
            if (mode.equals(SHELL)) {
                // design error ->
                // fixme
                if (e instanceof WrongPathParameterException || e instanceof CannotCreateFileException || e instanceof CannotLoadProfileConfiguration || e instanceof CannotSaveProfileConfigurationException) {
                    out.printf("\ri/o error: %s\n", e.getMessage());
                    reader.setPrompt("\u001B[1m>>> \u001B[0m");
                    return;
                }
                // design error ->
                // fixme
                if (e instanceof WrongRepositoryPathFormatException || e instanceof JrsResourceNotFoundException || e instanceof ParameterValueSizeException || e instanceof WrongPasswordException || /* for replicate if [to] doesn't exist */ e instanceof WrongProfileNameException || e instanceof MandatoryParameterMissingException || e instanceof NoProfileWithSuchNameException || e instanceof NotSpecifiedProfileNameException || e instanceof NotUniqueProfileNameException || e instanceof SessionIsNotAvailableException) {
                    out.printf("\rerror: %s\n", e.getMessage());
                    reader.setPrompt("\u001B[1m>>> \u001B[0m");
                    return;
                }
                // design error ->
                /*if (!ProfileUtil.isEmpty(profile) && !(e instanceof GeneralServerException)) {
                    String password = askPassword();
                    SessionFactory.createSession(profile.getUrl(), profile.getUsername(), password, profile.getOrganization());

                    *//**
                     * Hack! fixme: Delete this!
                     *//*
                    if (RepositoryPathCompleter.resources == null || RepositoryPathCompleter.resources.isEmpty()) {
                        RepositoryPathCompleter.resources = new TreeDownloader().markedList();
                    }

                    run();
                }*/ else {
                    throw new UnknownInterfaceException(e.getMessage());
                }
            } else {
                throw new UnknownInterfaceException(e.getMessage());
            }
        }
    }

//    private String askPassword() {
//        String pass = null;
//        try {
//            String username = getInstance().getUsername();
//            String jrsName = getInstance().getName();
//            pass = reader.readLine("\rPlease enter the password for <" + username + "> at <" + jrsName + "> environment: ", '*');
//        } catch (IOException ignored) {
//
//        }
//        reader.setPrompt("\u001B[1m>>> \u001B[0m");
//        return pass;
//    }

    public Parameter parameter(String key) {
        for (Parameter p : parameters) {
            if (key.equals(p.getName())) return p;
            if (p.getKey() != null && p.getKey().equals(key)) return p;
        }
        for (Parameter p : parameters) {
            if ("anonymous".equals(p.getName())) return p;
        }
        return null;
    }

    public Parameter nonAnonymousParameter(String key) {
        for (Parameter p : parameters) {
            if (key.equals(p.getName())) return p;
            if (p.getKey() != null && p.getKey().equals(key)) return p;
        }
        return null;
    }

    @Override
    public void setReader(ConsoleReader consoleReader) {
        this.reader = consoleReader;
    }
}
