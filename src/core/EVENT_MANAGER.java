package core;

import java.util.*;

public abstract class EVENT_MANAGER {

    private Vector<EVENT> events = new Vector<EVENT>();

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
        if (this.events.contains(e) != true)
        {
            return this.events.add(e);
        }
        else
        {
            return false;
        }
    }

    boolean remove(EVENT e)
    {
        return this.events.remove(e);
    }
}
