package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import ch.blackhan.representation.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public abstract class ORDER implements Representable {
    
    private long timestamp;
    private int transaction_number;

    protected PAIR pair;
    protected long units;
    protected double price;
    protected double low_price_limit;
    protected double high_price_limit;
    protected TAKE_PROFIT_ORDER take_profit;
    protected STOP_LOSS_ORDER stop_loss;

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

    public double getHighPriceLimit() { return this.high_price_limit; }
    public void setHighPriceLimit(double value) { this.high_price_limit = value; }

    public double getLowPriceLimit() { return this.low_price_limit; }
    public void setLowPriceLimit(double value) { this.low_price_limit = value; }

    public STOP_LOSS_ORDER getStopLoss() { return this.stop_loss; }
    public void setStopLoss(STOP_LOSS_ORDER value) { this.stop_loss = value; }

    public TAKE_PROFIT_ORDER getTakeProfit() { return this.take_profit; }
    public void setTakeProfit(TAKE_PROFIT_ORDER value) { this.take_profit = null; } //TODO!

    public long getTimestamp() { return this.timestamp; }
    public int getTransactionNumber() { return this.transaction_number; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public String toRepresentation()
    {
        return RepresentableUtil.toJson(this);
    }

    @Override public String toString()
    {
        return String.format("%s %s @ %s", this.pair, this.units, this.price);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
