package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

import ch.blackhan.*;
import ch.blackhan.core.*;
import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class ACCOUNT {

    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public class INFO
    {
        public Long id;
        public String name;
        public Long createDate;
        public String homeCurrency;
        public String profile;
        public Double marginCallRate;
        public Double marginRate;
    }

    private INFO info = null;
    private UUID sessionToken = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ACCOUNT(UUID sessionToken, long accountId) throws SESSION_EXCEPTION
    {
        if (sessionToken == null) throw new IllegalArgumentException("sessionToken");
        if (accountId <= 0) throw new IllegalArgumentException("accountId");

        this.sessionToken = sessionToken;
        this.info = this.getInfo(accountId);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private INFO getInfo(long accountId) throws SESSION_EXCEPTION
    {
        if (accountId <= 0) throw new IllegalArgumentException("accountId");

        String req = String.format(MESSAGE.ACCOUNT.GET_INFO, this.sessionToken, accountId);
        String rep = this.mqm.communicate(req);
        DefaultTokenizer st = new DefaultTokenizer(rep.substring(req.length()), "|", "None");

        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0)
        {
            throw new SESSION_EXCEPTION(this.sessionToken.toString());
        }
        else
        {
            INFO nfo = new INFO();

            nfo.id = accountId;
            nfo.name = st.nextStringOrDefault();
            nfo.createDate = st.nextLongOrDefault();
            nfo.homeCurrency = st.nextStringOrDefault();
            nfo.profile = st.nextStringOrDefault();
            nfo.marginCallRate = st.nextDoubleOrDefault();
            nfo.marginRate = st.nextDoubleOrDefault();

            return nfo;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private ACCOUNT_EVENT_MANAGER eventManager = null;
    public EVENT_MANAGER getEventManager()
    {
        if (this.eventManager != null)
        {
            return this.eventManager;
        }
        else
        {
            this.eventManager = new ACCOUNT_EVENT_MANAGER();
            this.eventManager.start();

            return this.eventManager;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public long getAccountId()
    {
        return this.info.id;
    }

    public String getAccountName()
    {
        return this.info.name;
    }

    public long getCreateDate()
    {
        return this.info.createDate;
    }

    public String getHomeCurrency()
    {
        return this.info.homeCurrency;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public String getProfile()
    {
        return this.info.profile;
    }

    public void setProfile(String profile) throws SESSION_EXCEPTION
    {
        if (profile == null) throw new IllegalArgumentException("profile");

        String req = String.format(MESSAGE.ACCOUNT.SET_PROFILE, this.sessionToken, profile);
        String rep = this.mqm.communicate(req);
        DefaultTokenizer st = new DefaultTokenizer(rep.substring(req.length()), "|", "None");

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

    public double getMarginAvailable() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginUsed() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginCallRate()
    {
        return this.info.marginCallRate;
    }

    public double getMarginRate()
    {
        return this.info.marginRate;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public double getBalance() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public void close(LIMIT_ORDER lo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void close(MARKET_ORDER mo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void close(String position) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public void execute(LIMIT_ORDER lo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void execute(MARKET_ORDER mo) throws FX_EXCEPTION
    {
        String req = String.format(MESSAGE.ACCOUNT.EXECUTE_MARKET_ORDER, this.sessionToken,
            this.getAccountId(), mo.toRepresentation());
        String rep = this.mqm.communicate(req);
        
        DefaultTokenizer st = new DefaultTokenizer(rep.substring(req.length()), "|", "None");
        String result = st.nextTokenOrDefault(true);
        if (result == null || result.compareTo("SESSION_ERROR") == 0) {
            throw new FX_EXCEPTION(new SESSION_EXCEPTION(this.sessionToken.toString()));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public void modify(LIMIT_ORDER lo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void modify(MARKET_ORDER mo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<ORDER> getOrders() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public LIMIT_ORDER getOrderWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public POSITION getPosition(PAIR pair) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public Vector<POSITION> getPositions() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getPositionValue() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public double getRealizedPL() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getUnrealizedPL() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<MARKET_ORDER> getTrades() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public MARKET_ORDER getTradeWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<TRANSACTION> getTransactions() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public TRANSACTION getTransactionWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public String toString()
    {
        return this.info.name;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
