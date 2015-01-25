package com.jaspersoft.jasperserver.shell.factory;

import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.shell.exception.SessionIsNotAvailableException;
import com.jaspersoft.jasperserver.shell.exception.server.GeneralServerException;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

/**
 * @author Alexander Krasnyanskiy
 */
public final class SessionFactory {

    private static Session instance;
    private static Long sessionStartTime;
    private static final int TIMEOUT = 5000;

    private SessionFactory() {/*NOP*/}

    public static Session create(String url, String username, String pass, String tenantName) {
        if (url != null && username != null && pass != null) {
            try {
                RestClientConfiguration config = new RestClientConfiguration(url);
                config.setConnectionTimeout(TIMEOUT);
                config.setReadTimeout(TIMEOUT);
                JasperserverRestClient client = new JasperserverRestClient(config);

                if (tenantName != null && !("".equals(tenantName))) {
                    username = format("%s|%s", username, tenantName);
                }
                instance = client.authenticate(username, pass);
                sessionStartTime = currentTimeMillis();
                return instance;
            } catch (Exception e) {
                throw new GeneralServerException(e.getMessage()); // todo :: extend it
            }
        }
        return null;
    }

    public static Session getInstance() {
        if (instance == null) {
            throw new SessionIsNotAvailableException();
        } else {
            return instance;
        }
    }

    public static String uptime() {
        PeriodFormatter hoursMinutesSeconds = new PeriodFormatterBuilder()
                .appendHours().appendSuffix(" hour ", " hours ")
                .appendMinutes().appendSuffix(" minute ", " minutes ")
                .appendSeconds().appendSuffix(" second ", " seconds ")
                .toFormatter();
        Period period = new Period(sessionStartTime, currentTimeMillis());
        return hoursMinutesSeconds.print(period);
    }
}