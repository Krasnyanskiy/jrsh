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
package com.jaspersoft.jasperserver.jrsh.common;

import com.google.common.base.Preconditions;
import com.jaspersoft.jasperserver.jaxrs.client.core.AuthenticationCredentials;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage;

/**
 * @author Alexander Krasnyanskiy
 */
public class SessionFactory {
    private static Session SHARED_SESSION;

    public static Session getSharedSession() {
        return SHARED_SESSION;
    }

    public static Session createUnsharedSession(String url, String username, String password, String organization) {
        return createSession(url, username, password, organization);
    }

    public static Session createSharedSession(String url, String username, String password, String organization) {
        SHARED_SESSION = createSession(url, username, password, organization);
        return SHARED_SESSION;
    }

    protected static Session createSession(String url, String username, String password, String organization) {
        Preconditions.checkNotNull(username, "Username shouldn't be 'null'");
        Preconditions.checkNotNull(password, "Password shouldn't be 'null'");
        Preconditions.checkNotNull(url, "URL shouldn't be 'null'");

        username = (organization == null)
                ? username
                : username.concat("|").concat(organization);

        url = (url.startsWith("http"))
                ? url
                : "http://".concat(url);

        Session session = new Session(
                new SessionStorage(
                        new RestClientConfiguration(url),
                        new AuthenticationCredentials(username, password)));

        session.getStorage()
                .getConfiguration()
                .setConnectionTimeout(4500);

        session.getStorage()
                .getConfiguration()
                .setReadTimeout(4500);

        return session;
    }

    public static void updateSharedSession(Session session) {
        SHARED_SESSION = session;
    }
}
