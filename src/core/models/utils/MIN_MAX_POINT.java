package core.models.utils;

public class MIN_MAX_POINT implements Cloneable {

    long timestamp;
    double min, max;

    public MIN_MAX_POINT()
    { }

    public MIN_MAX_POINT(long timestamp, double min, double max)
    {
        this.timestamp = timestamp;
        this.min = min;
        this.max = max;
    }

    @Override
    public Object clone()
    {
        return new MIN_MAX_POINT(this.timestamp, this.min, this.max);
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public double getMax()
    {
        return this.max;
    }

    public double getMin()
    {
        return this.min;
    }

    @Override
    public String toString()
    {
        return String.format("%d %.6f %.6f", this.timestamp,
            this.max, this.min
        );
    }
}