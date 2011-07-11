package ch.blackhan.core.models.util;

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.mqm.exception.*;

public final class KEEP_ALIVE_THREAD extends Thread {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected static final Logger logger = Logger.getLogger(KEEP_ALIVE_THREAD.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final long timeout = -1L; // indefinite [microsecs]
    public static final long refresh_rate = 1 << 16; //[ms]

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
        String req_message = String.format(
            MESSAGE.CLIENT.REFRESH, this.username, this.password, this.hostaddr
        );

        try
        {
            while (true)
            {
                String rep_message = this.mqm.communicate(req_message, timeout);
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
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
