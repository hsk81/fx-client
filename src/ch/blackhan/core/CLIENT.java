package ch.blackhan.core;

import java.net.*;
import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.exceptions.*;

public class CLIENT extends Observable {

    protected MQ_MANAGER mqm = MQ_MANAGER.singleton;

    private static final String LOGIN = "CLIENT|login|%s|%s|%s";
    private static final String LOGOUT = "CLIENT|logout|%s|%s|%s";
    private static final String GET_SERVER_TIME = "CLIENT|get_server_time";

    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";
    public static final String FATAL_ERROR = "FATAL_ERROR";
    public static final String UPDATE = "UPDATE";
    public static final String VERSION_INFO = "1.0.0";
    
    public static long INTERVAL_1_DAY = 1 * 24 * 3600;
    public static long INTERVAL_1_HOUR = 1 * 3600;
    public static long INTERVAL_1_MIN = 1 * 60;
    public static long INTERVAL_10_SEC = 10;
    public static long INTERVAL_15_MIN = 15 * 60;
    public static long INTERVAL_3_HOUR = 3 * 3600;
    public static long INTERVAL_30_MIN = 30 * 60;
    public static long INTERVAL_30_SEC = 30;
    public static long INTERVAL_5_MIN = 5 * 60;
    public static long INTERVAL_5_SEC = 5;

    public void login(String username, String password) throws
        INVALID_USER_EXCEPTION,
        INVALID_PASSWORD_EXCEPTION,
        SESSION_EXCEPTION {

        String message = String.format(
            LOGIN, username, password, getHostAddress()
        );

        this.mqm.req().send(message.getBytes(), 0);
        byte[] bytes = this.mqm.req().recv(0);
        String reply = new String(bytes);

        if (reply.startsWith(message))
        {
            StringTokenizer st = new StringTokenizer(
                reply.substring(message.length()), "|"
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
                throw new SESSION_EXCEPTION(getHostAddress());
            }
            else
            {
                this.user = new USER(username, password);
            }
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new SERVER_EXCEPTION(reply)
                );
            }
            else
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new MESSAGE_EXCEPTION(reply)
                );
            }
        }
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }

    public void logout() {

        if (this.user == null)
        {
            return;
        }

        String message = String.format(LOGOUT,
            this.user.getUserName(), this.user.getPassword(), getHostAddress()
        );

        this.mqm.req().send(message.getBytes(), 0);
        byte[] bytes = this.mqm.req().recv(0);
        String reply = new String(bytes);

        if (reply.startsWith(message))
        {
            StringTokenizer st = new StringTokenizer(
                reply.substring(message.length()), "|"
            );

            String result = st.nextToken();
            if (result.compareTo("INVALID_USER_ERROR") == 0)
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new INVALID_USER_EXCEPTION(
                        this.user.getUserName()
                    )
                );
            }
            else if (result.compareTo("INVALID_PASSWORD_ERROR") == 0)
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new INVALID_PASSWORD_EXCEPTION(
                        this.user.getPassword()
                    )
                );
            }
            else if (result.compareTo("SESSION_ERROR") == 0)
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new SESSION_EXCEPTION(
                        getHostAddress()
                    )
                );
            }
            else
            {
                this.user = null;
                this.rateTable = null;
            }
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new SERVER_EXCEPTION(reply)
                );
            }
            else
            {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, new MESSAGE_EXCEPTION(reply)
                );
            }
        }
    }

    private int timeout = Integer.MAX_VALUE;
    public void setTimeout(int timeout) { this.timeout = timeout; } //@TODO!?
    public int getTimeout() { return this.timeout; }

    private boolean withRateThread = false;
    public void setWithRateThread(boolean flag) { this.withRateThread = flag; }
    public boolean getWithRateThread() { return this.withRateThread; }
    
    private boolean withKeepAliveThread = false;
    public void setWithKeepAliveThread(boolean flag) { this.withKeepAliveThread = flag; }
    public boolean getWithKeepAliveThread() { return this.withKeepAliveThread; }

    public void setProxy(boolean state) {

        this.mqm.req(state ? 80 : MQ_MANAGER.reqDefaultPort);
    }

    private USER user = null;
    public USER getUser() throws SESSION_EXCEPTION
    {
        if (this.user != null)
        {
            return this.user;
        }
        else
        {
            throw new SESSION_EXCEPTION(getHostAddress());
        }
    }

    private RATE_TABLE rateTable = null;
    public RATE_TABLE getRateTable() throws SESSION_DISCONNECTED_EXCEPTION {

        if (this.user != null)
        {
            if (this.rateTable != null)
            {
                return this.rateTable;
            }
            else
            {
                return this.rateTable = new RATE_TABLE();
            }
        }
        else
        {
            throw new SESSION_DISCONNECTED_EXCEPTION(getHostAddress());
        }
    }

    public long getServerTime()
    {
        String message = String.format(GET_SERVER_TIME);

        this.mqm.req().send(message.getBytes(), 0);
        byte[] bytes = this.mqm.req().recv(0);
        String reply = new String(bytes);

        if (reply.startsWith(message))
        {
            StringTokenizer st = new StringTokenizer(
                reply.substring(message.length()), "|"
            );

            return Long.parseLong(st.nextToken());
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new SERVER_EXCEPTION(reply)
                );
            }
            else
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new MESSAGE_EXCEPTION(reply)
                );
            }

            return Long.MIN_VALUE;
        }
    }

    private String hostAddress = null;
    private String getHostAddress() {

        if (this.hostAddress != null)
        {
            return this.hostAddress;
        }
        else
        {
            try {
                this.hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                Logger.getLogger(CLIENT.class.getName()).log(
                    Level.SEVERE, null, ex
                );

                this.hostAddress = "127.0.0.1";
            }

            return this.hostAddress;
        }
    }

}
