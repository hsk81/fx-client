package ch.blackhan.core.models.util;

import ch.blackhan.core.models.*;

public class HISTORY_POINT implements Cloneable {

    long timestamp;
    double openBid, openAsk;
    double closeBid, closeAsk;
    double minBid, maxBid;
    double minAsk, maxAsk;
    boolean corrected = false;

    public HISTORY_POINT()
    { }

    public HISTORY_POINT(long timestamp,
        double openBid, double openAsk,
        double closeBid, double closeAsk,
        double minBid, double maxBid,
        double minAsk, double maxAsk)
    {
        this.timestamp = timestamp;
        this.openBid = openBid;
        this.openAsk = openAsk;
        this.closeBid = closeBid;
        this.closeAsk = closeAsk;
        this.minBid = minBid;
        this.maxBid = maxBid;
        this.minAsk = minAsk;
        this.maxAsk = maxAsk;
    }

    @Override
    public Object clone()
    {
        return new HISTORY_POINT(timestamp,
            openBid, openAsk,
            closeBid, closeAsk,
            minBid, maxBid,
            minAsk, maxAsk
        );
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    private TICK open = null;
    public TICK getOpen()
    {
        if (this.open != null) {
            return this.open;
        } else {
            return this.open = new TICK(
                this.timestamp, this.openBid, this.openAsk
            );
        }
    }

    private TICK close = null;
    public TICK getClose()
    {
        if (this.close != null) {
            return this.close;
        } else {
            return this.close = new TICK(
                this.timestamp, this.closeBid, this.closeAsk
            );
        }
    }

    private TICK min = null;
    public TICK getMin()
    {
        if (this.min != null) {
            return this.min;
        } else {
            return this.min = new TICK(
                this.timestamp, this.minBid, this.minAsk
            );
        }
    }

    private TICK max = null;
    public TICK getMax()
    {
        if (this.max != null) {
            return this.max;
        } else {
            return this.max = new TICK(
                this.timestamp, this.maxBid, this.maxAsk
            );
        }
    }

    private CANDLE_POINT candlePoint = null;
    public CANDLE_POINT getCandlePoint()
    {
        if (this.candlePoint != null) {
            return this.candlePoint;
        } else {
            return this.candlePoint = new CANDLE_POINT(this.timestamp,
                (this.openBid + this.openAsk) * 0.5,
                (this.closeBid + this.closeAsk) * 0.5,
                (this.minBid + this.minAsk) * 0.5,
                (this.maxBid + this.maxAsk) * 0.5
            );
        }
    }

    private MIN_MAX_POINT minMaxPoint = null;
    public MIN_MAX_POINT getMinMaxPoint()
    {
        if (this.minMaxPoint != null) {
            return this.minMaxPoint;
        } else {
            return this.minMaxPoint = new MIN_MAX_POINT(this.timestamp,
                (this.minBid + this.minAsk) * 0.5,
                (this.maxBid + this.maxAsk) * 0.5
            );
        }
    }

    public boolean getCorrected()
    {
        return this.corrected;
    }

    public void setCorrected(boolean corrected)
    {
        this.corrected = corrected;
    }

    @Override
    public String toString()
    {
        return String.format(
            "%d %.6f %.6f %.6f %.6f %.6f %.6f %.6f %.6f", this.timestamp,
            this.maxBid, this.maxAsk,
            this.openBid, this.openAsk,
            this.closeBid, this.closeAsk,
            this.minBid, this.minAsk
        );
    }

}
