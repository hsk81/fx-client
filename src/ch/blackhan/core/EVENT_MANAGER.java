package ch.blackhan.core;

import java.util.*;

public abstract class EVENT_MANAGER extends Thread {

    protected Vector<EVENT> events = new Vector<EVENT>();

    protected EVENT_MANAGER()
    {
        // pass
    }

    public Vector<EVENT> getEvents()
    {
        return (Vector<EVENT>)this.events.clone();
    }

    public boolean add(EVENT e)
    {
        return this.events.add(e);
    }

    public boolean remove(EVENT e)
    {
        return this.events.remove(e);
    }
}
