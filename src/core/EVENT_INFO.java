package core;

public abstract class EVENT_INFO {

    public EVENT_INFO()
    {
        // pass
    }

    public abstract int compareTo(Object object);
    public abstract long getTimestamp();
}
