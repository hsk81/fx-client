package ch.blackhan.core.util;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQ_MANAGER {
    
    public static final MQ_MANAGER singleton = new MQ_MANAGER(
        "tcp://localhost:6666", "tcp://localhost:6667"
    );

    private Context context = null;
    
    private Socket req = null;
    private String reqTarget = null;
    private Socket sub = null;
    private String subTarget = null;
    
    private MQ_MANAGER(String reqTarget, String subTarget)
    {
        this.reqTarget = reqTarget;
        this.subTarget = subTarget;

        this.context = ZMQ.context(1);
    }

    public Socket req()
    {
        if (this.req == null)
        {
            this.req = context.socket(ZMQ.REQ);
            this.req.connect(reqTarget);
        }
        
        return this.req;
    }

    public Socket sub()
    {
        if (this.sub == null)
        {
            this.sub = context.socket(ZMQ.SUB);
            this.sub.connect(subTarget);
        }
        
        return this.sub;
    }

    @Override
    public void finalize() throws Throwable
    {
        if (this.req != null) { this.req.close(); }
        if (this.sub != null) { this.sub.close(); }

        this.context.term();
    }
}
