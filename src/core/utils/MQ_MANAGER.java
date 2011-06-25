package core.utils;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQ_MANAGER {

    public static final MQ_MANAGER singleton = new MQ_MANAGER(
        "tcp://localhost:6666", "tcp://localhost:6667"
    );

    public Context context = null;
    public Socket req = null;
    public String reqTarget = null;
    public Socket sub = null;
    public String subTarget = null;

    private MQ_MANAGER(String reqTarget, String subTarget)
    {
        this.context = ZMQ.context(1);

        this.reqTarget = reqTarget;
        this.req = context.socket(ZMQ.REQ);
        this.req.connect(reqTarget);

        this.subTarget = subTarget;
        this.sub = context.socket(ZMQ.SUB);
        this.sub.connect(subTarget);
    }

    @Override
    public void finalize() throws Throwable
    {
        this.sub.close();
        this.req.close();
        this.context.term();
    }
}
