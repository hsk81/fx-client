package core;

import java.util.*;
import core.util.*;
import core.models.*;
import org.zeromq.ZMQ.*;

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
        /**
         * @TODO: Implement try-catch-finally around infinite loop!
         */
        
        this.sub = this.mqm.sub();
        
        this.sub.subscribe("EUR/USD".getBytes());
        this.sub.subscribe("USD/CHF".getBytes());
        this.sub.subscribe("EUR/CHF".getBytes());

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
            
            for (EVENT e : this.events)
            {
                if (e.match(rei))
                {
                    e.handle(rei, this);
                }
            }
        }
    }

    private Hashtable<String,PAIR> pairs = new Hashtable<String,PAIR>();
    private PAIR getPair(String q2b)
    {
        if (pairs.containsKey(q2b))
        {
            return pairs.get(q2b);
        }
        else
        {
            return pairs.put(q2b, new PAIR(q2b));
        }
    }
}
