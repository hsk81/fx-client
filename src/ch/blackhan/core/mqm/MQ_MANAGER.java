package ch.blackhan.core.mqm;

import ch.blackhan.core.mqm.exception.*;
import org.zeromq.ZMQ;

public class MQ_MANAGER {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final String reqSocketHost = "tcp://localhost";
    public static final long reqSocketPort = 6666;
    public static final String subSocketHost = "tcp://localhost";
    public static final long subSocketPort = 6667;

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
        reqSocketHost, reqSocketPort, subSocketHost, subSocketPort
    );

    private MQ_MANAGER(
        String reqHost, long reqPort, String subHost, long subPort)
    {
        this.reqSocketUri = String.format("%s:%d", reqHost, reqPort);
        this.subSocketUri = String.format("%s:%d", subHost, subPort);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public long timeout = -1L; // indefinite [microsecs]
    public long getTimeout() { return this.timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public String communicate(String req_message)
    {
        return this.communicate(req_message, this.timeout);
    }

    public String communicate(String req_message, long timeout) //[microsecs]
    {
        if (req_message != null)
        {
            if (this.request(req_message.getBytes()))
            {
                byte[] bytes = this.response(timeout);
                if (bytes != null)
                {
                    String rep_message = new String(bytes);
                    if (rep_message.startsWith(req_message))
                    {
                        return rep_message;
                    }
                    else
                    {
                        if (rep_message.startsWith("EXCEPTION"))
                        {
                            throw new UnsupportedOperationException(
                                new SERVER_EXCEPTION(rep_message)
                            );
                        }
                        else
                        {
                            throw new UnsupportedOperationException(
                                new RESPONSE_EXCEPTION(rep_message)
                            );
                        }
                    }
                }
                else
                {
                    throw new UnsupportedOperationException(
                        new RESPONSE_ISNULL_EXCEPTION(req_message)
                    );
                }
            }
            else
            {
                throw new UnsupportedOperationException(
                    new REQUEST_EXCEPTION(req_message)
                );
            }
        }
        else
        {
            throw new UnsupportedOperationException(
                new REQUEST_ISNULL_EXCEPTION(req_message)
            );
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public boolean request(byte[] bytes)
    {
        return this.reqSocket().send(bytes, 0);
    }

    public byte[] response(long timeout) //[microsecs]
    {
        long noo = MQ_MANAGER.poller.get().poll(timeout);
        if (noo > 0)
        {
            if (MQ_MANAGER.poller.get().pollin(0))
            {
                return this.reqSocket().recv(0);
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

    public ZMQ.Socket reqSocket()
    {
        if (MQ_MANAGER.reqSocket.get() == null)
        {
            MQ_MANAGER.reqSocket.set(context.socket(ZMQ.REQ));
            MQ_MANAGER.reqSocket.get().connect(this.reqSocketUri);
            MQ_MANAGER.poller.get().register(MQ_MANAGER.reqSocket.get(), ZMQ.Poller.POLLIN);
        }

        return MQ_MANAGER.reqSocket.get();
    }

    private ZMQ.Socket setReqSocketUri(String uri)
    {
        if (this.reqSocketUri.compareTo(uri) != 0)
        {
            MQ_MANAGER.poller.get().unregister(MQ_MANAGER.reqSocket.get());
            MQ_MANAGER.reqSocket.get().close();
            MQ_MANAGER.reqSocket.set(null);
            this.reqSocketUri = uri;
        }

        return this.reqSocket();
    }

    private ZMQ.Socket setReqSocketHostAndPort(String host, long port)
    {
        return this.setReqSocketUri(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket setReqSocketPort(long port)
    {
        return this.setReqSocketHostAndPort(reqSocketHost, port);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ZMQ.Socket subSocket()
    {
        if (MQ_MANAGER.subSocket.get() == null)
        {
            MQ_MANAGER.subSocket.set(context.socket(ZMQ.SUB));
            MQ_MANAGER.subSocket.get().connect(this.subSocketUri);
        }
        
        return MQ_MANAGER.subSocket.get();
    }

    private ZMQ.Socket setSubSocketUri(String uri)
    {
        if (this.subSocketUri.compareTo(uri) != 0)
        {
            MQ_MANAGER.subSocket.get().close();
            MQ_MANAGER.subSocket.set(null);
            this.subSocketUri = uri;
        }

        return this.subSocket();
    }

    private ZMQ.Socket setSubSockertHostAndPort(String host, long port)
    {
        return this.setSubSocketUri(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket setSubSocketPort(long port) {

        return this.setSubSockertHostAndPort(subSocketHost, port);
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
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
