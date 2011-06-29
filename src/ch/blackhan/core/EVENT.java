package ch.blackhan.core;

public abstract class EVENT {

    protected boolean transientFlag = false;

    public EVENT()
    {
        // pass
    }

    public EVENT(boolean transientFlag)
    {
        this.transientFlag = transientFlag;
    }

    public abstract boolean match(EVENT_INFO ei);
    public abstract void handle(EVENT_INFO ei, EVENT_MANAGER em);

    @Deprecated
    public boolean isTransient()
    {
        return this.transientFlag;
    }

    public boolean getTransient() 
    {
        return this.transientFlag;
    }
    
    public void setTransient(boolean transientFlag)
    {
        this.transientFlag = transientFlag;
    }
}