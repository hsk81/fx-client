package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

import ch.blackhan.*;
import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class USER {

    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public class INFO
    {
        public Long id;
        public String username;
        public String address;
        public Long insertDate;
        public String email;
        public String fullname;
        public String password;
        public String telephone;
        public String profile;
    }

    private INFO info = null;
    private UUID sessionToken = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public USER(String sessionToken) throws SESSION_EXCEPTION
    {
        if (sessionToken == null) throw new IllegalArgumentException("sessionToken");
        
        this.setSessionToken(sessionToken);
        this.info = this.getInfo();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private INFO getInfo() throws SESSION_EXCEPTION
    {
        DefaultTokenizer st = this.mqm.talk(
            String.format(MESSAGE.USER.GET_INFO, this.sessionToken)
        );

        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            INFO nfo = new INFO();

            nfo.id = st.nextLongOrDefault();
            nfo.username = st.nextStringOrDefault();
            nfo.address = st.nextStringOrDefault();
            nfo.insertDate = st.nextLongOrDefault();
            nfo.email = st.nextStringOrDefault();
            nfo.fullname = st.nextStringOrDefault();
            nfo.password = st.nextStringOrDefault();
            nfo.telephone = st.nextStringOrDefault();
            nfo.profile = st.nextStringOrDefault();

            return nfo;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<ACCOUNT> getAccounts() throws FX_EXCEPTION
    {
        DefaultTokenizer st = this.mqm.talk(
            String.format(MESSAGE.USER.GET_ACCOUNTS, this.sessionToken)
        );

        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            Vector<ACCOUNT> accounts = new Vector<ACCOUNT>();

            while (st.hasMoreTokens())
            {
                Integer accountId = st.nextIntegerOrDefault();
                ACCOUNT account = new ACCOUNT(this.sessionToken, accountId);
                accounts.add(account);
            }

            return accounts;
        }
    }

    public ACCOUNT getAccountWithId(int accountId) throws FX_EXCEPTION, ACCOUNT_EXCEPTION
    {
        if (accountId <= 0) throw new IllegalArgumentException("accountId");

        DefaultTokenizer st = this.mqm.talk(
            String.format(MESSAGE.USER.GET_ACCOUNT, this.sessionToken, accountId)
        );

        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else if (result.compareTo("ACCOUNT_ERROR") == 0)
        {
            throw new ACCOUNT_EXCEPTION(String.format("%s", accountId));
        }
        else
        {
            return new ACCOUNT(this.sessionToken, accountId);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public long getUserId()
    {
        return this.info.id;
    }

    
    public String getUserName()
    {
        return this.info.username;
    }

    public String getPassword()
    {
        return this.info.password;
    }

    public long getCreateDate()
    {
        return this.info.insertDate;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public String getName()
    {
        return this.info.fullname;
    }

    public String getAddress()
    {
        return this.info.address;
    }

    public String getEmail()
    {
        return this.info.email;
    }

    public String getTelephone()
    {
        return this.info.telephone;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public String getProfile()
    {
        return this.info.profile;
    }

    public void setProfile(String profile) throws SESSION_EXCEPTION
    {
        if (profile == null) throw new IllegalArgumentException("profile");

        DefaultTokenizer st = this.mqm.talk(
            String.format(MESSAGE.USER.SET_PROFILE, this.sessionToken, profile)
        );

        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            this.info.profile = profile;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public final UUID getSessionToken()
    {
        return this.sessionToken;
    }

    public final void setSessionToken(String sessionToken) throws SESSION_EXCEPTION
    {
        if (sessionToken == null) throw new IllegalArgumentException("sessionToken");
        
        try
        {
            this.sessionToken = UUID.fromString(sessionToken);
        }
        catch (IllegalArgumentException ex)
        {
            throw new SESSION_EXCEPTION(ex.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
