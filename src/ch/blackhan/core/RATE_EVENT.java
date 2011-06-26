package ch.blackhan.core;

import ch.blackhan.core.models.*;

public abstract class RATE_EVENT extends EVENT {

    protected PAIR pair = null;

    public RATE_EVENT()
    {
        super();
    }

    public RATE_EVENT(boolean transientFlag)
    {
        super(transientFlag);
    }

    public RATE_EVENT(String key)
    {
        this.pair = new PAIR(key);
    }

    public RATE_EVENT(String key, boolean transientFlag)
    {
        this.pair = new PAIR(key);
        this.transientFlag = transientFlag;
    }

    @Override
    public boolean match(EVENT_INFO ei) {

        if (ei != null && ei.getClass() != RATE_EVENT_INFO.class)
        {
            return this.pair == null || this.pair.equals(
                ((RATE_EVENT_INFO)ei).getPair()
            );
        }
        else
        {
            return false;
        }
    }

    @Override
    public abstract void handle(EVENT_INFO ei, EVENT_MANAGER em);
}
