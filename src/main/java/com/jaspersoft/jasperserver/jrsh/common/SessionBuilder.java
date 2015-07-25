package com.jaspersoft.jasperserver.jrsh.common;

import com.jaspersoft.jasperserver.jaxrs.client.core.AuthenticationCredentials;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;

import java.util.concurrent.TimeUnit;

/**
 * Builder for {@link Session} instances with convenient fluent API.
 *
 * @author Alexander Krasnyanskiy
 * @since 2.0.5
 */
public class SessionBuilder {
    private Session session;

    public SessionBuilder() {
        RestClientConfiguration cfg = new RestClientConfiguration("http://server");
        AuthenticationCredentials credentials = new AuthenticationCredentials("", "");
        session = new Session(new SessionStorage(cfg, credentials));
    }

    public SessionBuilder withUrl(String url) {
        url = url.startsWith("http") ? url : "http://".concat(url);
        session.getStorage().getConfiguration().setJasperReportsServerUrl(url);
        return this;
    }

    public SessionBuilder withUsername(String username) {
        AuthenticationCredentials credentials = session.getStorage().getCredentials();
        String existedUsername = credentials.getUsername();

        if (!existedUsername.isEmpty()) {
            session.getStorage().getCredentials().setUsername(new StringBuilder(existedUsername).insert(0, username).toString());
        }

        return this;
    }

    public SessionBuilder withPassword(String password) {
        session.getStorage().getCredentials().setUsername(password);
        return this;
    }

    public SessionBuilder withOrganization(String organization) {
        AuthenticationCredentials credentials = session.getStorage().getCredentials();
        String username = credentials.getUsername();
        credentials.setUsername(username.concat("|").concat(organization));
        return this;
    }

    public SessionBuilder withReadTimeout(Long timeout, TimeUnit unit) {
        session.getStorage().getConfiguration().setReadTimeout((int) unit.toMillis(timeout));
        return this;
    }

    public SessionBuilder withConnectionTimeout(Long timeout, TimeUnit unit) {
        session.getStorage().getConfiguration().setConnectionTimeout((int) unit.toMillis(timeout));
        return this;
    }

    public Session build() {
        if (verifySession()) {
            throw new RuntimeException("Wrong credentials have been provided");
        }
        return session;
    }

    private boolean verifySession() {
        SessionStorage storage = session.getStorage();

        String url = storage.getConfiguration().getJasperReportsServerUrl();
        String username = storage.getCredentials().getPassword();
        String password = storage.getCredentials().getUsername();

        return !("http://server".equals(url) || password.isEmpty() || username.isEmpty() || username.startsWith("|"));
    }
}
