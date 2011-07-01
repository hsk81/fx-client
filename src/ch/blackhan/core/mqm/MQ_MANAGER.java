package ch.blackhan.core.mqm;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQ_MANAGER {

    public static final String reqDefaultHost = "tcp://localhost";
    public static final String subDefaultHost = "tcp://localhost";
    public static final long reqDefaultPort = 6666;
    public static final long subDefaultPort = 6667;

    public static final MQ_MANAGER singleton = new MQ_MANAGER(
        reqDefaultHost, reqDefaultPort, subDefaultHost, subDefaultPort
    );

    private Context context = null;
    
    private Socket req = null;
    private String reqUri = null;
    private Socket sub = null;
    private String subUri = null;
    
    private MQ_MANAGER(
        String reqHost, long reqPort, String subHost, long subPort)
    {
        this.reqUri = String.format("%s:%d", reqHost, reqPort);
        this.subUri = String.format("%s:%d", subHost, subPort);

        this.context = ZMQ.context(1);
    }

    public Socket req() {

        if (this.req == null)
        {
            this.req = context.socket(ZMQ.REQ);
            this.req.connect(reqUri);
        }
        
        return this.req;
    }

    public Socket req(String uri) {

        if (this.reqUri.compareTo(uri) != 0)
        {
            this.req.close();
            this.req = null;
            this.reqUri = uri;
        }

        return this.req();
    }

    public Socket req(String host, long port) {

        return this.req(String.format("%s:%d", host, port));
    }

    public Socket req(long port) {

        return this.req(String.format("%s:%d", reqDefaultHost, port));
    }

    public Socket sub() {

        if (this.sub == null)
        {
            this.sub = context.socket(ZMQ.SUB);
            this.sub.connect(subUri);
        }
        
        return this.sub;
    }

    public Socket sub(String uri) {

        if (this.subUri.compareTo(uri) != 0)
        {
            this.sub.close();
            this.sub = null;
            this.subUri = uri;
        }

        return this.sub();
    }

    public Socket sub(String host, long port) {

        return this.sub(String.format("%s:%d", host, port));
    }

    public Socket sub(long port) {

        return this.sub(String.format("%s:%d", subDefaultHost, port));
    }

    @Override
    public void finalize() throws Throwable {

        if (this.req != null) { this.req.close(); }
        if (this.sub != null) { this.sub.close(); }

        this.context.term();
    }
}
