package utils;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class MQManager {

    public static final MQManager singleton

        = new MQManager("tcp://localhost:6666");

    public Context context = null;
    public Socket req = null;

    private MQManager(String target)
    {
        this.context = ZMQ.context(1);
        this.req = context.socket(ZMQ.REQ);
        this.req.connect(target);
    }

    @Override
    public void finalize() throws Throwable
    {
        this.req.close();
        this.context.term();
    }

}
