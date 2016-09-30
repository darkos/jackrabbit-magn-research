package info.magnolia.jaas.sp.jcr;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Login module for internal Jackrabbit authentication, validates the JackRabbit 'admin' user and uses the Subject
 * provided by the magnolia context.
 *
 * Note that Jackrabbit requires the login module to be serializable.
 */
public class JackrabbitAuthenticationModule implements LoginModule, Serializable {

    private static final Logger log = LoggerFactory.getLogger(JackrabbitAuthenticationModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;
    private String name;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    public boolean login() throws LoginException {

        if (this.callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available");
        }

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("name");
        callbacks[1] = new PasswordCallback("pswd", false);

        char[] password;
        try {
            this.callbackHandler.handle(callbacks);
            this.name = ((NameCallback) callbacks[0]).getName();
            password = ((PasswordCallback) callbacks[1]).getPassword();
        } catch (IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException ce) {
            throw new LoginException(ce.getCallback().toString() + " not available");
        }

        if (getAdminUser().equals(this.name)) {
            if (!Arrays.equals(password, getAdminPassword().toCharArray())) {
                throw new FailedLoginException();
            }
            return true;
        }
        return true;
    }

    public boolean commit() throws LoginException {
        return true;
    }

    public boolean abort() throws LoginException {
        return false;
    }

    public boolean logout() throws LoginException {
        callbackHandler = null;
        name = null;
        return true;
    }

    private void compileUserPrincipals(Subject magnoliaSubject) {
        subject.getPrincipals().addAll(magnoliaSubject.getPrincipals());
        subject.getPrincipals().add(new UserPrincipal(name));
    }

    protected String getAdminUser() {
        return "admin";
    }

    protected String getAdminPassword() {
        return "admin";
    }

}
