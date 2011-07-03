package ch.blackhan.core.mqm;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

public class MQ_MANAGER {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final String reqSocketHostDefault = "tcp://localhost";
    public static final String subSocketHostDefault = "tcp://localhost";
    public static final long reqSocketPortDefault = 6666;
    public static final long subSocketPortDefault = 6667;

    public static final MQ_MANAGER singleton = new MQ_MANAGER(
        reqSocketHostDefault, reqSocketPortDefault,
        subSocketHostDefault, subSocketPortDefault
    );

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private ZMQ.Context context = null;
    private ZMQ.Poller poller = null;
    
    private ZMQ.Socket reqSocket = null;
    private String reqSocketUri = null;
    private ZMQ.Socket subSocket = null;
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
    
    public boolean send(byte[] message)
    {
        return this.reqSocket().send(message, 0);
    }

    public byte[] recv()
    {
        return this.recv(-1);
    }
    
    public byte[] recv(long timeout)
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

    public ZMQ.Socket reqSocket() {

        if (this.reqSocket == null)
        {
            this.reqSocket = context.socket(ZMQ.REQ);
            this.reqSocket.connect(reqSocketUri);
            this.poller.register(reqSocket, Poller.POLLIN);
        }

        return this.reqSocket;
    }

    public ZMQ.Socket reqSocket(String uri) {

        if (this.reqSocketUri.compareTo(uri) != 0)
        {
            this.poller.unregister(reqSocket);
            this.reqSocket.close();
            this.reqSocket = null;
            this.reqSocketUri = uri;
        }

        return this.reqSocket();
    }

    public ZMQ.Socket reqSocket(String host, long port) {

        return this.reqSocket(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket reqSocket(long port) {

        return this.reqSocket(String.format("%s:%d", reqSocketHostDefault, port));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ZMQ.Socket subSocket() {

        if (this.subSocket == null)
        {
            this.subSocket = context.socket(ZMQ.SUB);
            this.subSocket.connect(subSocketUri);
        }
        
        return this.subSocket;
    }

    public ZMQ.Socket subSocket(String uri) {

        if (this.subSocketUri.compareTo(uri) != 0)
        {
            this.subSocket.close();
            this.subSocket = null;
            this.subSocketUri = uri;
        }

        return this.subSocket();
    }

    public ZMQ.Socket subSocket(String host, long port) {

        return this.subSocket(String.format("%s:%d", host, port));
    }

    public ZMQ.Socket subSocket(long port) {

        return this.subSocket(String.format("%s:%d", subSocketHostDefault, port));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void finalize() throws Throwable {

        if (this.reqSocket != null) { this.reqSocket.close(); }
        if (this.subSocket != null) { this.subSocket.close(); }

        this.context.term();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
