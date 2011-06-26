package ch.blackhan.core.models.util;

public class CANDLE_POINT implements Cloneable {

    long timestamp;
    double open, close;
    double min, max;

    public CANDLE_POINT()
    { }

    public CANDLE_POINT(long timestamp,
        double open, double close, double min, double max)
    {
        this.timestamp = timestamp;
        this.open = open;
        this.close = close;
        this.min = min;
        this.max = max;
    }

    @Override
    public Object clone()
    {
        return new CANDLE_POINT(this.timestamp,
            this.open, this.close, this.min, this.max
        );
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }
    
    public double getClose()
    {
        return this.close;
    }

    public double getMax()
    {
        return this.max;
    }

    public double getMin()
    {
        return this.min;
    }

    public  double getOpen()
    {
        return this.open;
    }

    @Override
    public String toString()
    {
        return String.format("%d %.6f %.6f %.6f %.6f", this.timestamp,
            this.max, this.open, this.close, this.min
        );
    }
}
