package core.models;

public class TICK implements Cloneable {

    private long timestamp = 0L;
    private double bid = 0;
    private double ask = 0;

    public TICK() { }

    public TICK(long timestamp, double bid, double ask)
    {
        this.timestamp = timestamp;
        this.bid = bid;
        this.ask = ask;
    }

    @Override
    public Object clone()
    {
        return new TICK(this.timestamp, this.bid, this.ask);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null) return false;
        if (object == this) return true;
        if (this.getClass() != object.getClass()) return false;
        
        TICK tick = (TICK)object;

        return this.timestamp == tick.timestamp &&
               this.bid == tick.bid &&
               this.ask == tick.ask;
    }

    public TICK getInverse()
    {
        return new TICK(this.timestamp, this.ask, this.bid);
    }

    public long getTimestamp() { return this.timestamp; }
    public void setTimestamp(long value) { this.timestamp = value; }

    public double getAsk() { return this.ask; }
    public void setAsk(double value) { this.ask = value; }

    public double getBid() { return this.bid; }
    public void setBid(double value) { this.bid = value; }

    public double getMean()
    {
        return (this.bid + this.ask) / 2.0;
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    @Override
    public String toString()
    {
        return String.format(
            "[%.6f,%.6f] @ %s", this.bid, this.ask, this.timestamp
        );
    }
}
