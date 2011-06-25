package core;

import java.util.*;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public final class RATE_EVENT_MANAGER extends EVENT_MANAGER {

    public RATE_EVENT_MANAGER()
    {
        super();
    }

    @Override
    public void run()
    {
        Socket sub = mqm.context.socket(ZMQ.SUB);
        sub.connect(mqm.subTarget);

        while (true)
        {
            String message = new String(sub.recv(0));
            String[] array = message.split("\\|");
            String pair = array[0];

            Vector<EVENT> es = this.eventMap.get(pair);
            for (EVENT e : es)
            {
                //
                // TODO!
                //
            }
        }
    }
}
