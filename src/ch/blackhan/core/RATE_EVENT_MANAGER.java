package ch.blackhan.core;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.models.*;

import org.zeromq.ZMQ;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public final class RATE_EVENT_MANAGER extends EVENT_MANAGER {

    protected static final Logger logger = Logger.getLogger(RATE_EVENT_MANAGER.class.getName());
    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private ZMQ.Socket subSocket = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public RATE_EVENT_MANAGER()
    {
        super();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override protected void finalize() throws Throwable
    {
        if (this.subSocket != null)
        {
            this.subSocket.unsubscribe("EUR/USD".getBytes());
            this.subSocket.unsubscribe("USD/CHF".getBytes());
            this.subSocket.unsubscribe("EUR/CHF".getBytes());

            this.subSocket.close(); this.subSocket = null;
        }

        super.finalize();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public void run()
    {
        this.subSocket = this.mqm.getSubscriber();
        
        this.subSocket.subscribe("EUR/USD".getBytes());
        this.subSocket.subscribe("USD/CHF".getBytes());
        this.subSocket.subscribe("EUR/CHF".getBytes());

        int index = 0;
        EVENT event = null;

        while (true)
        {
            StringTokenizer st = new StringTokenizer(
                new String(this.subSocket.recv(0)), "|"
            );

            //UUID uuid = UUID.fromString(st.nextToken());
            //assert(uuid != null);

            RATE_EVENT_INFO rei = new RATE_EVENT_INFO(
                this.getPair(st.nextToken()), new TICK(
                    Long.parseLong(st.nextToken()),
                    Double.parseDouble(st.nextToken()),
                    Double.parseDouble(st.nextToken())
                )
            );

            logger.log(Level.INFO, rei.toString());
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
                    if (event.match(rei))
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

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private Hashtable<String,PAIR> pairs = new Hashtable<String,PAIR>();
    private PAIR getPair(String q2b)
    {
        if (this.pairs.containsKey(q2b) == false)
        {
            this.pairs.put(q2b, new PAIR(q2b));
        }

        return this.pairs.get(q2b);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
