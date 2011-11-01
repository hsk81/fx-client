package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public abstract class ORDER {

    protected PAIR pair;
    protected long units;
    protected double price;
    protected long timestamp;
    protected int transactionNumber;
    protected TAKE_PROFIT_ORDER takeProfitOrder;
    protected STOP_LOSS_ORDER stopLossOrder;
    protected double lowPriceLimit;
    protected double highPriceLimit;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ORDER()
    {
        // pass
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public PAIR getPair() { return this.pair; }
    public void setPair(PAIR value) { this.pair = value; }

    public long getUnits() { return this.units; }
    public void setUnits(long value) { this.units = value; }

    public double getPrice() { return this.price; }
    public long getTimestamp() { return this.timestamp; }
    public int getTransactionNumber() { return this.transactionNumber; }

    public double getHighPriceLimit() { return this.highPriceLimit; }
    public void setHighPriceLimit(double value) { this.highPriceLimit = value; }

    public double getLowPriceLimit() { return this.lowPriceLimit; }
    public void setLowPriceLimit(double value) { this.lowPriceLimit = value; }

    public STOP_LOSS_ORDER getStopLoss() { return this.stopLossOrder; }
    public void setStopLoss(STOP_LOSS_ORDER value) { this.stopLossOrder = value; }

    public TAKE_PROFIT_ORDER getTakeProfit() { return this.takeProfitOrder; }
    public void setTakeProfit(TAKE_PROFIT_ORDER value) { this.takeProfitOrder = value; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public String toString()
    {
        return String.format("%s %s @ %s", this.pair, this.units, this.price);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
