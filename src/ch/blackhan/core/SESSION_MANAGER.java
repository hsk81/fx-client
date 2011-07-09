package ch.blackhan.core;

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.mqm.exception.*;

import org.zeromq.*;

public final class SESSION_MANAGER extends Thread { //@TODO: Verify class name!

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final Logger logger = Logger.getLogger(SESSION_MANAGER.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final String socketHost = "tcp://localhost"; //@TODO: Move to MQ_MANAGER!
    static final int socketPort = 6666; //@TODO: Move to MQ_MANAGER!

    static final long timeout = 2048L * 1000L; //[microsecs]
    static final long frequency = 4096; //[ms]

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    String username = null;
    String password = null;
    String hostaddr = null;

    public SESSION_MANAGER(String username, String password, String hostaddr)
    {
        this.username = username;
        this.password = password;
        this.hostaddr = hostaddr;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run()
    {
        ZMQ.Context context = ZMQ.context(1); //@TODO: Move to MQ_MANAGER!
        ZMQ.Poller poller = context.poller(1); //@TODO: Move to MQ_MANAGER!
        ZMQ.Socket socket = context.socket(ZMQ.REQ); //@TODO: Move to MQ_MANAGER!

        socket.connect(String.format("%s:%d",
            SESSION_MANAGER.socketHost, SESSION_MANAGER.socketPort
        ));
        
        poller.register(socket, ZMQ.Poller.POLLIN);
        
        String req_message = String.format(
            MESSAGE.CLIENT.REFRESH, this.username, this.password, this.hostaddr
        );

        while (true) //@TODO: Check ctrl-c interrupt!
        {
            if (socket.send(req_message.getBytes(), 0))
            {
                long noo = poller.poll(SESSION_MANAGER.timeout);
                if (noo > 0)
                {
                    if (poller.pollin(0))
                    {
                        String rep_message = new String(socket.recv(0));
                        if (rep_message != null)
                        {
                            StringTokenizer st = new StringTokenizer(
                                rep_message.substring(req_message.length()), "|"
                            );

                            String result = st.nextToken();
                            if (result.compareTo("INVALID_USER_ERROR") == 0 ||
                                result.compareTo("INVALID_PASSWORD_ERROR") == 0 ||
                                result.compareTo("SESSION_ERROR") == 0)
                            {
                                logger.log(Level.SEVERE, result); break;
                            }
                            else
                            {
                                try
                                {
                                    synchronized (this)
                                    {
                                        this.wait(SESSION_MANAGER.frequency);
                                    }
                                }
                                catch (InterruptedException ex)
                                {
                                    logger.log(Level.SEVERE, null, ex); break;
                                }
                            }
                        }
                        else
                        {
                            logger.log(Level.SEVERE, null,
                                new RESPONSE_ISNULL_EXCEPTION(req_message)
                            ); break;
                        }
                    }
                    else
                    {
                        logger.log(Level.SEVERE, null,
                            new RESPONSE_ISNULL_EXCEPTION(req_message)
                        ); break;
                    }
                }
                else
                {
                    logger.log(Level.SEVERE, null,
                        new RESPONSE_ISNULL_EXCEPTION(req_message)
                    ); break;
                }
            }
            else
            {
                logger.log(Level.SEVERE, null,
                    new REQUEST_EXCEPTION(req_message)
                ); break;
            }
        }

        poller.unregister(socket); //@TODO: Move to MQ_MANAGER!
        socket.close(); //@TODO: Move to MQ_MANAGER!
        context.term(); //@TODO: Move to MQ_MANAGER!
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
