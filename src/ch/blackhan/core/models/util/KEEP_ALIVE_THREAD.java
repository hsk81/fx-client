package ch.blackhan.core.models.util;

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.mqm.exception.*;

import org.zeromq.*;

public final class KEEP_ALIVE_THREAD extends Thread {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected static final Logger logger = Logger.getLogger(KEEP_ALIVE_THREAD.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final long timeout = -1L; //[microsecs]
    public static final long refresh_rate = 4096; //[ms]

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    String username = null;
    String password = null;
    String hostaddr = null;

    public KEEP_ALIVE_THREAD(String username, String password, String hostaddr)
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
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Poller poller = context.poller(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);

        socket.connect(String.format("%s:%d",
            MQ_MANAGER.reqSocketHost, MQ_MANAGER.reqSocketPort
        ));
        
        poller.register(socket, ZMQ.Poller.POLLIN);
        
        String req_message = String.format(
            MESSAGE.CLIENT.REFRESH, this.username, this.password, this.hostaddr
        );

        try
        {
            while (true)
            {
                if (socket.send(req_message.getBytes(), 0))
                {
                    long noo = poller.poll(KEEP_ALIVE_THREAD.timeout);
                    if (noo > 0)
                    {
                        if (poller.pollin(0))
                        {
                            String rep_message = new String(socket.recv(0));
                            if (rep_message != null)
                            {
                                String result = new StringTokenizer(
                                    rep_message.substring(req_message.length()), "|"
                                ).nextToken();
                                
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
                                            this.wait(KEEP_ALIVE_THREAD.refresh_rate);
                                        }
                                    }
                                    catch (InterruptedException ex)
                                    {
                                        break;
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
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
        finally
        {
            poller.unregister(socket);
            socket.close();
            context.term();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
