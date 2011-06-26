package ch.blackhan.core;

public abstract class ACCOUNT_EVENT extends EVENT {

    public ACCOUNT_EVENT()
    {
        throw new UnsupportedOperationException();
    }

    public ACCOUNT_EVENT(boolean transientFlag)
    {
        throw new UnsupportedOperationException();
    }

    public ACCOUNT_EVENT(String key)
    {
        throw new UnsupportedOperationException();
    }

    public ACCOUNT_EVENT(String key, boolean transientFlag)
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
