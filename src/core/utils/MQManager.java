package core.utils;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQManager {

    public static final MQManager singleton = new MQManager(
        "tcp://localhost:6666", "tcp://localhost:6667"
    );

    public Context context = null;
    public Socket req = null;
    public Socket sub = null;

    private MQManager(String reqTarget, String subTarget)
    {
        this.context = ZMQ.context(1);
        this.req = context.socket(ZMQ.REQ);
        this.req.connect(reqTarget);
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
