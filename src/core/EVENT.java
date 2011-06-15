package core;

public abstract class EVENT {

    public EVENT()
    {
        throw new UnsupportedOperationException();
    }

    public EVENT(boolean transientFlag)
    {
        throw new UnsupportedOperationException();
    }

    public abstract boolean match(EVENT_INFO ei);
    public abstract void handle(EVENT_INFO ei, EVENT_MANAGER em);

    public boolean isTransient()
    {
        throw new UnsupportedOperationException();
    }

    public void setTransient(boolean t)
    {
        throw new UnsupportedOperationException();
    }
}
