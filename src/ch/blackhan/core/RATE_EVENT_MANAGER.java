package ch.blackhan.core;

import java.util.*;
import org.zeromq.ZMQ.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.models.*;

public final class RATE_EVENT_MANAGER extends EVENT_MANAGER {

    protected MQ_MANAGER mqm = MQ_MANAGER.singleton;
    private Socket sub = null;
    
    public RATE_EVENT_MANAGER()
    {
        super();
    }
    
    @Override
    protected void finalize()
    {
        if (this.sub != null)
        {
            this.sub.unsubscribe("EUR/USD".getBytes());
            this.sub.unsubscribe("USD/CHF".getBytes());
            this.sub.unsubscribe("EUR/CHF".getBytes());

            this.sub.close();
        }
    }

    @Override
    public void run()
    {
        this.sub = this.mqm.sub();
        
        this.sub.subscribe("EUR/USD".getBytes());
        this.sub.subscribe("USD/CHF".getBytes());
        this.sub.subscribe("EUR/CHF".getBytes());

        int index = 0;
        EVENT event = null;

        while (true)
        {
            StringTokenizer st = new StringTokenizer(
                new String(this.sub.recv(0)), "|"
            );

            RATE_EVENT_INFO rei = new RATE_EVENT_INFO(
                this.getPair(st.nextToken()), new TICK(
                    Long.parseLong(st.nextToken()),
                    Double.parseDouble(st.nextToken()),
                    Double.parseDouble(st.nextToken())
                )
            );

            index = 0; while (true)
            {
                event = null; synchronized (this.events)
                {
                    if (index < this.events.size())
                    {
                        event = this.events.get(index++);
                    }
                    else
                    {
                        break;
                    }
                }

                if (event != null)
                {
                    if (event.match(rei)) //@TODO: Separate thread?
                    {
                        event.handle(rei, this);
                    }
                }
                else
                {
                    throw new NullPointerException("event");
                }
            }
        }
    }

    private Hashtable<String,PAIR> pairs = new Hashtable<String,PAIR>();
    private PAIR getPair(String q2b)
    {
        if (this.pairs.containsKey(q2b) == false)
        {
            this.pairs.put(q2b, new PAIR(q2b));
        }

        return this.pairs.get(q2b);
    }
}
