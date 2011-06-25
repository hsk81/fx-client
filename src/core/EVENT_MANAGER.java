package core;

import core.utils.*;
import java.util.*;

public abstract class EVENT_MANAGER extends Thread {

    protected Hashtable<String,Vector<EVENT>> eventMap
        = new Hashtable<String,Vector<EVENT>>();
    protected MQ_MANAGER mqm = MQ_MANAGER.singleton;

    protected EVENT_MANAGER()
    {
        mqm.sub.subscribe("EUR/USD".getBytes());
        mqm.sub.subscribe("USD/CHF".getBytes());
        mqm.sub.subscribe("EUR/CHF".getBytes());
    }

    @Override
    protected void finalize()
    {
        mqm.sub.unsubscribe("EUR/USD".getBytes());
        mqm.sub.unsubscribe("USD/CHF".getBytes());
        mqm.sub.unsubscribe("EUR/CHF".getBytes());
    }

    public Vector<EVENT> getEvents()
    {
        Vector<EVENT> result = new Vector<EVENT>();
        for (Vector<EVENT> es : this.eventMap.values())
        {
            result.addAll(es);
        }

        return result;
    }

    public boolean add(EVENT e)
    {
        if (this.eventMap.containsKey(e.toString()))
        {
            Vector<EVENT> es = this.eventMap.get(e.toString());
            if (es.contains(e))
            {
                return false;
            }
            else
            {
                es.add(e);
                return true;
            }
        }
        else
        {
            this.eventMap.put(e.toString(), new Vector<EVENT>());
            return this.add(e);
        }
    }

    boolean remove(EVENT e)
    {
        if (this.eventMap.containsKey(e.toString()))
        {
            Vector<EVENT> es = this.eventMap.get(e.toString());
            if (es.remove(e))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
