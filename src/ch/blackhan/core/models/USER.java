package ch.blackhan.core.models;

import java.util.Vector;
import ch.blackhan.core.exceptions.*;

public class USER {

    String username = null;
    String password = null;

    public USER(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public String getUserName() { return this.username; }
    public String getPassword() { return this.password; }

    public Vector<ACCOUNT> getAccounts() throws SESSION_EXCEPTION {
        return new Vector<ACCOUNT>(); //throw new UnsupportedOperationException();
    }

}
