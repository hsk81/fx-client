package ch.blackhan.core;

import java.util.*;

public abstract class EVENT_MANAGER extends Thread {

    protected final Vector<EVENT> events = new Vector<EVENT>();

    protected EVENT_MANAGER()
    {
        // pass
    }

    public Vector<EVENT> getEvents()
    {
        synchronized(this.events)
        {
            return (Vector<EVENT>)this.events.clone();
        }
    }

    public boolean add(EVENT event)
    {
        if (event != null)
        {
            synchronized(this.events)
            {
                if (!this.events.contains(event))
                {
                    return this.events.add(event);
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    public boolean remove(EVENT event)
    {
        if (event != null)
        {
            synchronized(this.events)
            {
                return this.events.remove(event);
            }
        }
        else
        {
            return false;
        }
    }
}