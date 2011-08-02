package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public abstract class ORDER {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ORDER()
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public double getHighPriceLimit()
    {
        throw new UnsupportedOperationException();
    }
    
    public double getLowPriceLimit()
    {
        throw new UnsupportedOperationException();
    }

    public PAIR getPair()
    {
        throw new UnsupportedOperationException();
    }

    public double getPrice()
    {
        throw new UnsupportedOperationException();
    }

    public STOP_LOSS_ORDER getStopLoss()
    {
        throw new UnsupportedOperationException();
    }

    public TAKE_PROFIT_ORDER getTakeProfit()
    {
        throw new UnsupportedOperationException();
    }

    public long getTimestamp()
    {
        throw new UnsupportedOperationException();
    }

    public int getTransactionNumber()
    {
        throw new UnsupportedOperationException();
    }

    public long getUnits()
    {
        throw new UnsupportedOperationException();
    }

    public void setHighPriceLimit(double limit)
    {
        throw new UnsupportedOperationException();
    }

    public void setLowPriceLimit(double limit)
    {
        throw new UnsupportedOperationException();
    }

    public void setPair(PAIR pair)
    {
        throw new UnsupportedOperationException();
    }

    public void setStopLoss(STOP_LOSS_ORDER stoploss)
    {
        throw new UnsupportedOperationException();
    }

    public void setTakeProfit(TAKE_PROFIT_ORDER takeprofit)
    {
        throw new UnsupportedOperationException();
    }

    public void setUnits(long units)
    {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
