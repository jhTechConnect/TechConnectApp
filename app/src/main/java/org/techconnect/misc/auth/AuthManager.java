package org.techconnect.misc.auth;

import android.content.Context;
import android.content.SharedPreferences;

import org.techconnect.model.UserAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phani on 10/25/2016.
 */

public class AuthManager {

    private static final String USER_ID = "org.techconnect.authmanager.userid";
    private static final String AUTH_TOKEN = "org.techconnect.authmanager.authToken";

    private static AuthManager instance = null;
    private SharedPreferences prefs;
    private UserAuth auth = null;

    private List<AuthListener> listeners = new ArrayList<>();

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    public static AuthManager get(Context c) {
        if (instance == null) {
            instance = new AuthManager(c);
        }
        return instance;
    }


    public boolean hasAuth() {
        return prefs.contains(USER_ID) && prefs.contains(AUTH_TOKEN);
    }

    public UserAuth getAuth() {
        if (auth == null && hasAuth()) {
            auth = new UserAuth();
            auth.setAuthToken(prefs.getString(AUTH_TOKEN, null));
            auth.setAuthToken(prefs.getString(USER_ID, null));
        }
        return null;
    }

    public void setAuth(UserAuth userAuth) {
        this.auth = userAuth;
        if (userAuth == null) {
            prefs.edit().remove(USER_ID).remove(AUTH_TOKEN).apply();
        } else {
            prefs.edit().putString(USER_ID, userAuth.getUserId())
                    .putString(AUTH_TOKEN, userAuth.getAuthToken()).apply();
        }
        for (AuthListener listener : listeners) {
            if (this.auth == null) {
                listener.onLogout();
            } else {
                listener.onLoginSucces(this.auth);
            }
        }
    }

    public void addAuthListener(AuthListener listener) {
        if (!listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeAuthListener(AuthListener listener) {
        this.listeners.remove(listener);
    }

}
