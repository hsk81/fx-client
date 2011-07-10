package ch.blackhan.core.mqm;

import org.zeromq.ZMQ;
import ch.blackhan.core.mqm.exception.*;

public class MQ_MANAGER {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final String reqSocketHost = "tcp://localhost";
    public static final String subSocketHost = "tcp://localhost";
    public static final long reqSocketPort = 6666;
    public static final long subSocketPort = 6667;

    public static final MQ_MANAGER singleton = new MQ_MANAGER(
        reqSocketHost, reqSocketPort, subSocketHost, subSocketPort
    );

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private ZMQ.Context context = null;
    private ZMQ.Poller poller = null;
    
    private ZMQ.Socket reqSocket = null; //TODO: Per thread!?
    private String reqSocketUri = null;
    private ZMQ.Socket subSocket = null; //TODO: Per thread!?
    private String subSocketUri = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    private MQ_MANAGER(
        String reqHost, long reqPort, String subHost, long subPort)
    {
        this.reqSocketUri = String.format("%s:%d", reqHost, reqPort);
        this.subSocketUri = String.format("%s:%d", subHost, subPort);

        this.context = ZMQ.context(1);
        this.poller = this.context.poller(1);
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

    private String communicate(String req_message, long timeout) // [microseconds]
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

    public byte[] response(long timeout) // [microseconds]
    {
        long noo = this.poller.poll(timeout);
        if (noo > 0)
        {
            if (this.poller.pollin(0))
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
        if (this.reqSocket == null)
        {
            this.reqSocket = context.socket(ZMQ.REQ);
            this.reqSocket.connect(reqSocketUri);
            this.poller.register(reqSocket, ZMQ.Poller.POLLIN);
        }

        return this.reqSocket;
    }

    private ZMQ.Socket setReqSocketUri(String uri)
    {
        if (this.reqSocketUri.compareTo(uri) != 0)
        {
            this.poller.unregister(reqSocket);
            this.reqSocket.close();
            this.reqSocket = null;
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
        if (this.subSocket == null)
        {
            this.subSocket = context.socket(ZMQ.SUB);
            this.subSocket.connect(subSocketUri);
        }
        
        return this.subSocket;
    }

    private ZMQ.Socket setSubSocketUri(String uri)
    {
        if (this.subSocketUri.compareTo(uri) != 0)
        {
            this.subSocket.close();
            this.subSocket = null;
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

    @Override
    public void finalize() throws Throwable
    {
        if (this.reqSocket != null)
        {
            this.poller.unregister(this.reqSocket);
            this.reqSocket.close();
            this.reqSocket = null;
        }

        if (this.subSocket != null)
        {
            this.subSocket.close();
            this.subSocket = null;
        }

        this.context.term();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
