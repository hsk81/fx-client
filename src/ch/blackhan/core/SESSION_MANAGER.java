package ch.blackhan.core;

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;

public final class SESSION_MANAGER extends Thread {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final Logger logger = Logger.getLogger(SESSION_MANAGER.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected final MQ_MANAGER mqm = MQ_MANAGER.singleton;
    
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
        while (true)
        {
            String req_message = String.format(
                MESSAGE.CLIENT.LOGIN, this.username, this.password, this.hostaddr
            );

            String rep_message = this.mqm.communicate(req_message); //@TODO: ThreadsVsSockets!

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
                        this.wait(25L);
                    }
                }
                catch (InterruptedException ex)
                {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
