package core;

public abstract class RATE_EVENT extends EVENT {

    public RATE_EVENT()
    {
        throw new UnsupportedOperationException();
    }

    public RATE_EVENT(boolean transientFlag)
    {
        throw new UnsupportedOperationException();
    }

    public RATE_EVENT(String key)
    {
        throw new UnsupportedOperationException();
    }

    public RATE_EVENT(String key, boolean transientFlag)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean match(EVENT_INFO ei) {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract void handle(EVENT_INFO ei, EVENT_MANAGER em);
}
