package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

import ch.blackhan.core.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class ACCOUNT {

    private USER user = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ACCOUNT(USER user)
    {
        this.user = user;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
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

    public void execute(LIMIT_ORDER lo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void execute(MARKET_ORDER mo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public long getAccountId()
    {
        throw new UnsupportedOperationException();
    }

    public String getAccountName()
    {
        throw new UnsupportedOperationException();
    }

    public double getBalance() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public long getCreateDate()
    {
        throw new UnsupportedOperationException();
    }

    public EVENT_MANAGER getEventManager() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public String getHomeCurrency()
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginAvailable() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginCallRate()
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginRate()
    {
        throw new UnsupportedOperationException();
    }

    public double getMarginUsed() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public Vector<ORDER> getOrders() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public LIMIT_ORDER getOrderWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

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

    public String getProfile()
    {
        throw new UnsupportedOperationException();
    }

    public double getRealizedPL() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public Vector<MARKET_ORDER> getTrades() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public MARKET_ORDER getTradeWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public Vector<TRANSACTION> getTransactions() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public TRANSACTION getTransactionWithId(int transactionNumber) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public double getUnrealizedPL() throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void modify(LIMIT_ORDER lo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void modify(MARKET_ORDER mo) throws FX_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    public void setProfile(String newprofile) throws ACCOUNT_EXCEPTION
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public String toString()
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
