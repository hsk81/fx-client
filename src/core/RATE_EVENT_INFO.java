package core;

import core.models.*;

public final class RATE_EVENT_INFO extends EVENT_INFO {

    protected PAIR pair = null;
    protected TICK tick = null;

    public RATE_EVENT_INFO(PAIR pair, TICK tick)
    {
        this.pair = pair;
        this.tick = tick;
    }

    public PAIR getPair()
    {
        return this.pair;
    }

    public TICK getTick()
    {
        return this.tick;
    }

    @Override
    public long getTimestamp()
    {
        return this.getTick().getTimestamp();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this != object)
        {
            if (object instanceof RATE_EVENT_INFO)
            {
                RATE_EVENT_INFO rai = (RATE_EVENT_INFO)object; return
                    
                    this.getPair().equals(rai.getPair()) &&
                    this.getTick().equals(rai.getTick());
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
        return String.format(
            "{0} {1}", this.getPair().toString(), this.getTick().toString()
        ).hashCode();
    }

    @Override
    public int compareTo(Object object)
    {
        if (this != object)
        {
            if (object instanceof RATE_EVENT_INFO)
            {
                RATE_EVENT_INFO that = (RATE_EVENT_INFO)object;
                long this_dts = this.getTimestamp();
                long that_dts = that.getTimestamp();

                if (this_dts != that_dts)
                {
                    return (this_dts > that_dts) ? 1 : -1;
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                throw new ClassCastException(String.format(
                    "{0} to {1}", object.getClass(), RATE_EVENT_INFO.class
                ));
            }
        }
        else
        {
            return 0;
        }
    }
}
