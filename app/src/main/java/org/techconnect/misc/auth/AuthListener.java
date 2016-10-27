package org.techconnect.misc.auth;

import org.techconnect.model.UserAuth;

/**
 * Created by Phani on 10/25/2016.
 */

public interface AuthListener {

    void onLoginSucces(UserAuth auth);

    void onLogout();

}
