package ch.blackhan.core;

import java.util.*;

import ch.blackhan.core.exceptions.*;
import ch.blackhan.core.models.USER;

public class CLIENT extends Observable {

    public static final String CONNECTED = "CONNECTED"; //@TODO!
    public static final String DISCONNECTED = "DISCONNECTED"; //@TODO!

    public void logout() {
        //throw new UnsupportedOperationException();
    }

    public void setTimeout(int DEFAULT_TIMEOUT) {
        //throw new UnsupportedOperationException();
    }

    public void setWithRateThread(boolean b) {
        //throw new UnsupportedOperationException();
    }

    public void login(String username, String password) throws
        INVALID_USER_EXCEPTION,
        INVALID_PASSWORD_EXCEPTION,
        SESSION_EXCEPTION {
        
        //throw new UnsupportedOperationException();
    }

    public boolean isLoggedIn() {
        return true; //throw new UnsupportedOperationException();
    }

    private USER user = null;
    public USER getUser() {

        if (this.user != null)
        {
            return this.user;
        }
        else
        {
            return this.user = new USER();
        }
    }

    private RATE_TABLE rateTable = null;
    public RATE_TABLE getRateTable() throws SESSION_DISCONNECTED_EXCEPTION {
        
        if (this.rateTable != null)
        {
            return this.rateTable;
        }
        else
        {
            return this.rateTable = new RATE_TABLE();
        }
    }

}
