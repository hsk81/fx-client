package core;

import core.models.*;

public final class TRANSACTION implements Cloneable {

    public static final int FX_CSBALACECORRECTION = 110;
    public static final int FX_CSCLOSEWITHOUTPENALTY = 115;
    public static final int FX_CSINTERESTCORRECTION = 111;
    public static final int FX_CSINTERESTTODAYCORRECTION = 112;
    public static final int FX_CSPLCORRECTION = 113;
    public static final int FX_CSTRADECANCEL = 117;
    public static final int FX_CSTRADECORRECTION = 114;
    public static final int FX_DELFUNDS = 109;
    public static final int FX_DURATION = 101;
    public static final int FX_INTDEFERRED = 116;
    public static final int FX_INTEREST = 107;
    public static final int FX_MARGIN = 104;
    public static final int FX_NSF = 108;
    public static final int FX_ORDERCANCELBOUNDSVIOLATION = 118;
    public static final int FX_ROLLOVER = 106;
    public static final int FX_SL = 102;
    public static final int FX_TP = 103;
    public static final int FX_USER = 100;
    public static final int FX_XFR_ORDER = 105;

    @Override
    public Object clone()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this != other)
        {
            if (other instanceof TRANSACTION)
            {
                throw new UnsupportedOperationException();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public double getAmount()
    {
        throw new UnsupportedOperationException();
    }

    public double getBalance()
    {
        throw new UnsupportedOperationException();
    }

    public int getCompletionCode()
    {
        throw new UnsupportedOperationException();
    }

    public int getDiaspora()
    {
        throw new UnsupportedOperationException();
    }

    public double getInterest()
    {
        throw new UnsupportedOperationException();
    }

    public double getMargin()
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

    public double getStopLoss()
    {
        throw new UnsupportedOperationException();
    }

    public double getTakeProfit()
    {
        throw new UnsupportedOperationException();
    }

    public long getTimestamp()
    {
        throw new UnsupportedOperationException();
    }

    public int getTransactionLink()
    {
        throw new UnsupportedOperationException();
    }

    public int getTransactionNumber()
    {
        throw new UnsupportedOperationException();
    }

    public String getType()
    {
        throw new UnsupportedOperationException();
    }

    public long getUnits()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isBuy()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isSell()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        throw new UnsupportedOperationException();
    }
}
