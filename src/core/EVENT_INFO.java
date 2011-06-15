package core;

public abstract class EVENT_INFO {

    public EVENT_INFO()
    {
        throw new UnsupportedOperationException();
    }

    public abstract int compareTo(Object other);
    public abstract long getTimestamp();
}
