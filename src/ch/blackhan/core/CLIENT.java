package ch.blackhan.core;

import java.net.*;
import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.exceptions.*;
import ch.blackhan.core.models.util.*;

public class CLIENT extends Observable {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected static final Logger logger = Logger.getLogger(CLIENT.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private static KEEP_ALIVE_THREAD keep_alive_thread = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";
    public static final String FATAL_ERROR = "FATAL_ERROR";
    public static final String UPDATE = "UPDATE";
    public static final String VERSION_INFO = "1.0.0";
    
    public static final long INTERVAL_1_DAY = 1 * 24 * 3600;
    public static final long INTERVAL_1_HOUR = 1 * 3600;
    public static final long INTERVAL_1_MIN = 1 * 60;
    public static final long INTERVAL_10_SEC = 10;
    public static final long INTERVAL_15_MIN = 15 * 60;
    public static final long INTERVAL_3_HOUR = 3 * 3600;
    public static final long INTERVAL_30_MIN = 30 * 60;
    public static final long INTERVAL_30_SEC = 30;
    public static final long INTERVAL_5_MIN = 5 * 60;
    public static final long INTERVAL_5_SEC = 5;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public void login(String username, String password) throws
        INVALID_USER_EXCEPTION, INVALID_PASSWORD_EXCEPTION, SESSION_EXCEPTION
    {
        synchronized (this)
        {
            String req_message = String.format(
                MESSAGE.CLIENT.LOGIN, username, password, this.getHostAddress()
            );

            String rep_message = this.mqm.communicate(req_message);

            StringTokenizer st = new StringTokenizer(
                rep_message.substring(req_message.length()), "|"
            );

            String result = st.nextToken();
            if (result.compareTo("INVALID_USER_ERROR") == 0)
            {
                throw new INVALID_USER_EXCEPTION(username);
            }
            else if (result.compareTo("INVALID_PASSWORD_ERROR") == 0)
            {
                throw new INVALID_PASSWORD_EXCEPTION(password);
            }
            else if (result.compareTo("SESSION_ERROR") == 0)
            {
                throw new SESSION_EXCEPTION(this.getHostAddress());
            }
            else
            {
                if (this.user == null)
                {
                    this.user = new USER(username, password);
                }

                if (this.withKeepAliveThread && CLIENT.keep_alive_thread == null)
                {
                    CLIENT.keep_alive_thread = new KEEP_ALIVE_THREAD(
                        username, password, this.getHostAddress()
                    );

                    CLIENT.keep_alive_thread.start();
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public boolean isLoggedIn()
    {
        return this.user != null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void logout()
    {
        synchronized (this)
        {
            if (this.user != null)
            {
                while (CLIENT.keep_alive_thread.isAlive())
                {
                    CLIENT.keep_alive_thread.interrupt();
                }

                CLIENT.keep_alive_thread = null;

                String username = this.user.getUserName();
                String password = this.user.getPassword();

                this.mqm.communicate(String.format(
                    MESSAGE.CLIENT.LOGOUT, username, password, this.getHostAddress()
                ));

                this.user = null;
                this.rateTable = null;
            }
            else
            {
                this.rateTable = null;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public int getTimeout() { return (int)(1.0 * this.mqm.getTimeout() / 1000.0); }
    public void setTimeout(int timeout)
    {
        this.mqm.setTimeout((timeout >= 0) ? (long)timeout * 1000L : -1L);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private boolean withRateThread = false;
    public boolean getWithRateThread() { return this.withRateThread; }
    public void setWithRateThread(boolean flag) { this.withRateThread = flag; }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private boolean withKeepAliveThread = false;
    public boolean getWithKeepAliveThread() { return this.withKeepAliveThread; }
    public void setWithKeepAliveThread(boolean flag) { this.withKeepAliveThread = flag; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void setProxy(boolean state)
    {
        this.mqm.setRequestorPort(state ? 80 : MQ_MANAGER.requestorPort);
        this.mqm.setSubscriberPort(state ? 81 : MQ_MANAGER.subscriberPort);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private USER user = null;
    public USER getUser() throws SESSION_EXCEPTION
    {
        if (this.user != null)
        {
            return this.user;
        }
        else
        {
            throw new SESSION_EXCEPTION(this.getHostAddress());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private RATE_TABLE rateTable = null;
    public RATE_TABLE getRateTable() throws SESSION_DISCONNECTED_EXCEPTION
    {
        if (this.user != null)
        {
            if (this.rateTable != null)
            {
                return this.rateTable;
            }
            else
            {
                return this.rateTable = new RATE_TABLE(this.getWithRateThread());
            }
        }
        else
        {
            throw new SESSION_DISCONNECTED_EXCEPTION(this.getHostAddress());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public long getServerTime()
    {
        String req_message = String.format(MESSAGE.CLIENT.GET_SERVER_TIME);
        String rep_message = this.mqm.communicate(req_message);
 
        StringTokenizer st = new StringTokenizer(
            rep_message.substring(req_message.length()), "|"
        );

        return Long.parseLong(st.nextToken());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private String hostAddress = null;
    private String getHostAddress()
    {
        if (this.hostAddress != null)
        {
            return this.hostAddress;
        }
        else
        {
            try
            {
                this.hostAddress = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException ex)
            {
                logger.log(Level.SEVERE, null, ex); this.hostAddress = "127.0.0.1";
            }

            return this.hostAddress;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
