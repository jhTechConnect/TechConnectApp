package org.centum.techconnect.services;

import java.io.IOException;

/**
 * This Exception class will be used in cases where there are issues in Authorization
 * Two cases: Not Logged in, Wrong Login
 * Created by doranwalsten on 10/11/16.
 */
public class AuthorizationException extends IOException {


    public AuthorizationException(String message) {
        super(message);
    }



}
