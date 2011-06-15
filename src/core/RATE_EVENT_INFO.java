package core;

import core.models.*;

public final class RATE_EVENT_INFO extends EVENT_INFO   {

    @Override
    public int compareTo(Object other)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTimestamp()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this != other)
        {
            if (other instanceof RATE_EVENT_INFO)
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
    public int hashCode()
    {
        throw new UnsupportedOperationException();
    }

    public PAIR getPair()
    {
        throw new UnsupportedOperationException();
    }

    public TICK getTick()
    {
        throw new UnsupportedOperationException();
    }
}
