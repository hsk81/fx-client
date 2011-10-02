package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

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
        public int userId;
        public String userName;
        public String address;
        public long createDate;
        public String email;
        public String name;
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
        String req = String.format(MESSAGE.USER.GET_INFO, this.sessionToken);
        String rep = this.mqm.communicate(req);
        StringTokenizer st = new StringTokenizer(rep.substring(req.length()), "|");

        String result = st.nextToken();
        if (result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            INFO nfo = new INFO();
            
            nfo.userId = Integer.parseInt(st.nextToken());
            nfo.userName = st.nextToken();
            nfo.address = st.nextToken();
            nfo.createDate = Long.parseLong(st.nextToken());
            nfo.email = st.nextToken();
            nfo.name = st.nextToken();
            nfo.password = st.nextToken();
            nfo.telephone = st.nextToken();
            nfo.profile = st.nextToken();

            return nfo;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<ACCOUNT> getAccounts() throws SESSION_EXCEPTION
    {
        String req = String.format(MESSAGE.USER.GET_ACCOUNTS, this.sessionToken);
        String rep = this.mqm.communicate(req);
        StringTokenizer st = new StringTokenizer(rep.substring(req.length()), "|");

        String result = st.nextToken();
        if (result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            Vector<ACCOUNT> accounts = new Vector<ACCOUNT>();

            while (st.hasMoreTokens())
            {
                int accountId = Integer.parseInt(st.nextToken());
                ACCOUNT account = new ACCOUNT(this.sessionToken, accountId);
                accounts.add(account);
            }

            return accounts;
        }
    }

    public ACCOUNT getAccountWithId(int accountId) throws SESSION_EXCEPTION, ACCOUNT_EXCEPTION
    {
        if (accountId <= 0) throw new IllegalArgumentException("accountId");

        String req = String.format(MESSAGE.USER.GET_ACCOUNT, this.sessionToken, accountId);
        String rep = this.mqm.communicate(req);
        StringTokenizer st = new StringTokenizer(rep.substring(req.length()), "|");

        String result = st.nextToken();
        if (result.compareTo("SESSION_ERROR") == 0)
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

    public int getUserId()
    {
        return this.info.userId;
    }

    
    public String getUserName()
    {
        return this.info.userName;
    }

    public String getPassword()
    {
        return this.info.password;
    }

    public long getCreateDate()
    {
        return this.info.createDate;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public String getName()
    {
        return this.info.name;
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

        String req = String.format(MESSAGE.USER.SET_PROFILE, this.sessionToken, profile);
        String rep = this.mqm.communicate(req);
        StringTokenizer st = new StringTokenizer(rep.substring(req.length()), "|");

        String result = st.nextToken();
        if (result.compareTo("SESSION_ERROR") == 0)
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
