package ch.blackhan.core.mqm;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

import ch.blackhan.*;
import ch.blackhan.core.mqm.exception.*;

import org.zeromq.ZMQ;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class MQ_MANAGER {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final String requestorHost = "tcp://localhost";
    public static final long requestorPort = 6666;
    public static final String subscriberHost = "tcp://localhost";
    public static final long subscriberPort = 6667;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private static final ZMQ.Context context = ZMQ.context(1);
    
    private static final ThreadLocal<ZMQ.Poller> poller = new ThreadLocal<ZMQ.Poller>() {
        @Override protected ZMQ.Poller initialValue() {
            return MQ_MANAGER.context.poller(1);
        }
    };

    private static final ThreadLocal<ZMQ.Socket> reqSocket = new ThreadLocal<ZMQ.Socket>();
    private String reqSocketUri = null;
    private static final ThreadLocal<ZMQ.Socket> subSocket = new ThreadLocal<ZMQ.Socket>();
    private String subSocketUri = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public static final MQ_MANAGER unique = new MQ_MANAGER(
        requestorHost, requestorPort, subscriberHost, subscriberPort
    );

    private MQ_MANAGER(String reqHost, long reqPort, String subHost, long subPort)
    {
        this.reqSocketUri = String.format("%s:%d", reqHost, reqPort);
        this.subSocketUri = String.format("%s:%d", subHost, subPort);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Universally unique identification of the logical *server side* counter party; *not* the
     * identification of another client but either the id of a simulator (for the GAME client)
     * or the id of an actual price matching engine (for the TRADE client).
     */

    private byte[] uuid = "00000000-0000-0000-0000-000000000000".getBytes();

    public void setUuid(UUID uuid)
    {
        synchronized (this)
        {
            if (uuid == null)
            {
                this.uuid = "00000000-0000-0000-0000-000000000000".getBytes();
            }
            else
            {
                this.uuid = uuid.toString().getBytes();
            }
        }
    }

    public UUID getUuid()
    {
        return UUID.fromString(this.uuid.toString());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private long timeout = -1L; // indefinite [microsecs]
    public long getTimeout() { return this.timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public DefaultTokenizer talk(String req_message)
    {
        return this.talk(req_message, this.timeout);
    }

    public DefaultTokenizer talk(String req_message, long timeout)
    {
        return this.talk(req_message, timeout, "|");
    }

    public DefaultTokenizer talk(String req_message, long timeout, String delimiter)
    {
        return this.talk(req_message, timeout, delimiter, "None");
    }

    public DefaultTokenizer talk(String req_message, long timeout, String delimiter,
        String default_value)
    {
        return this.talk(req_message, timeout, delimiter, default_value, 0);
    }

    public DefaultTokenizer talk(String req_message, long timeout, String delimiter,
        String default_value, int start_index) // [microsecs]
    {
        String rep_message = this.communicate(req_message, timeout);
        if (rep_message != null)
        {
            return new DefaultTokenizer(
                rep_message.substring(start_index), delimiter, default_value
            );
        }
        else
        {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public String communicate(String req_message)
    {
        return this.communicate(req_message, this.timeout);
    }

    public String communicate(String req_message, long timeout) // [microsecs]
    {
        if (req_message != null)
        {
            if (this.request(req_message.getBytes()))
            {
                byte[] bytes = this.response(timeout);
                if (bytes != null)
                {
                    String rep_message = new String(bytes);
                    if (rep_message.startsWith("EXCEPTION"))
                    {
                        throw new AssertionError(
                            new SERVER_EXCEPTION(rep_message)
                        );
                    }
                    else
                    {
                        return rep_message;
                    }
                }
                else
                {
                    throw new AssertionError(
                        new RESPONSE_ISNULL_EXCEPTION(req_message)
                    );
                }
            }
            else
            {
                throw new AssertionError(
                    new REQUEST_EXCEPTION(req_message)
                );
            }
        }
        else
        {
            throw new AssertionError(
                new REQUEST_ISNULL_EXCEPTION(req_message)
            );
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public boolean request(byte[] bytes)
    {
        return this.getRequestor().send(bytes, 0);
    }

    public byte[] response(long timeout) // [microsecs]
    {
        long noo = MQ_MANAGER.poller.get().poll(timeout);
        if (noo > 0)
        {
            if (MQ_MANAGER.poller.get().pollin(0))
            {
                return this.getRequestor().recv(0);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ZMQ.Socket getRequestor()
    {
        if (MQ_MANAGER.reqSocket.get() == null)
        {
            MQ_MANAGER.reqSocket.set(context.socket(ZMQ.REQ));
            MQ_MANAGER.reqSocket.get().connect(this.reqSocketUri);
            MQ_MANAGER.poller.get().register(MQ_MANAGER.reqSocket.get(), ZMQ.Poller.POLLIN);
        }

        return MQ_MANAGER.reqSocket.get();
    }

    public ZMQ.Socket setRequestorUri(String uri)
    {
        if (this.reqSocketUri.compareTo(uri) != 0)
        {
            MQ_MANAGER.poller.get().unregister(MQ_MANAGER.reqSocket.get());
            MQ_MANAGER.reqSocket.get().close();
            MQ_MANAGER.reqSocket.set(null);
            
            this.reqSocketUri = uri;
        }

        return this.getRequestor();
    }

    public ZMQ.Socket setRequestorHostAndPort(String host, long port)
    {
        return this.setRequestorUri(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket setRequestorPort(long port)
    {
        return this.setRequestorHostAndPort(requestorHost, port);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ZMQ.Socket getSubscriber()
    {
        if (MQ_MANAGER.subSocket.get() == null)
        {
            MQ_MANAGER.subSocket.set(context.socket(ZMQ.SUB));
            MQ_MANAGER.subSocket.get().connect(this.subSocketUri);
        }
        
        return MQ_MANAGER.subSocket.get();
    }

    public ZMQ.Socket setSubscriberUri(String uri)
    {
        if (this.subSocketUri.compareTo(uri) != 0)
        {
            MQ_MANAGER.subSocket.get().close();
            MQ_MANAGER.subSocket.set(null);
            
            this.subSocketUri = uri;
        }

        return this.getSubscriber();
    }

    public ZMQ.Socket setSubscriberHostAndPort(String host, long port)
    {
        return this.setSubscriberUri(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket setSubscriberPort(long port) {

        return this.setSubscriberHostAndPort(subscriberHost, port);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public void finalize() throws Throwable
    {
        if (MQ_MANAGER.reqSocket.get() != null)
        {
            MQ_MANAGER.poller.get().unregister(MQ_MANAGER.reqSocket.get());
            MQ_MANAGER.reqSocket.get().close();
            MQ_MANAGER.reqSocket.set(null);
        }

        if (MQ_MANAGER.subSocket.get() != null)
        {
            MQ_MANAGER.subSocket.get().close();
            MQ_MANAGER.subSocket.set(null);
        }

        MQ_MANAGER.context.term();
        super.finalize();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
