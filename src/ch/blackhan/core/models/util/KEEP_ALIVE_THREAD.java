package ch.blackhan.core.models.util;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.util.logging.*;

import ch.blackhan.*;
import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.mqm.exception.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public final class KEEP_ALIVE_THREAD extends Thread {

    protected static final Logger logger = Logger.getLogger(KEEP_ALIVE_THREAD.class.getName());
    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final long timeout = -1L; // indefinite [microsecs]
    public static final long refresh_rate = 1 << 16; // [ms]

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private UUID sessionToken = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public KEEP_ALIVE_THREAD(UUID sessionToken)
    {
        this.sessionToken = sessionToken;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public void run()
    {
        String req = String.format(MESSAGE.CLIENT.REFRESH, this.sessionToken);
        try
        {
            while (true)
            {
                DefaultTokenizer tokenizer = this.mqm.talk(req, timeout);
                if (tokenizer != null)
                {
                    String result = tokenizer.nextStringOrDefault();
                    if (result == null || result.compareTo("SESSION_ERROR") == 0)
                    {
                        logger.log(Level.SEVERE, result);
                        break;
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
                    logger.log(Level.SEVERE, null, new RESPONSE_ISNULL_EXCEPTION(req));
                    break;
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
