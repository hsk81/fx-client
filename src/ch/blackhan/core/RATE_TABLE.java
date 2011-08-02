package ch.blackhan.core;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.net.*;
import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.mqm.util.*;
import ch.blackhan.core.exceptions.*;
import ch.blackhan.core.models.util.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class RATE_TABLE {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final Logger logger = Logger.getLogger(RATE_TABLE.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected final MQ_MANAGER mqm = MQ_MANAGER.unique;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private boolean withRateTableThread = true;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public RATE_TABLE()
    {
        // pass
    }

    public RATE_TABLE(boolean withRateTableThread)
    {
        this.withRateTableThread = withRateTableThread;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private RATE_EVENT_MANAGER eventManager = null;
    public EVENT_MANAGER getEventManager()
    {
        if (this.eventManager != null)
        {
            return this.eventManager;
        }
        else
        {
            this.eventManager = new RATE_EVENT_MANAGER();

            if (this.withRateTableThread)
            {
                this.eventManager.start();
            }
            
            return this.eventManager;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<HISTORY_POINT> getHistory(
        PAIR pair, long interval, int numTicks
    )
        throws FX_EXCEPTION
    {
        Vector<HISTORY_POINT> historyPoints = new Vector<HISTORY_POINT>();

        String req_message = String.format(
            MESSAGE.RATE_TABLE.GET_HISTORY, pair.getQuote(), pair.getBase(), interval, numTicks
        );

        String res_message = this.mqm.communicate(req_message);

        StringTokenizer st = new StringTokenizer(
            res_message.substring(req_message.length()), "|"
        );

        while (st.hasMoreTokens())
        {
            historyPoints.add(new HISTORY_POINT(
                Long.parseLong(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken()),
                Double.parseDouble(st.nextToken())
            ));
        }

        return historyPoints;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<CANDLE_POINT> getCandles(
        PAIR pair, long interval, int numTicks
    )
        throws FX_EXCEPTION
    {
        Vector<HISTORY_POINT> historyPoints = this.getHistory(
            pair, interval, numTicks
        );

        Vector<CANDLE_POINT> candlePoints = new Vector<CANDLE_POINT>();
        for(HISTORY_POINT historyPoint : historyPoints)
        {
            candlePoints.add(historyPoint.getCandlePoint());
        }

        return candlePoints;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Vector<MIN_MAX_POINT> getMinMaxs(
        PAIR pair, long interval, int numTicks
    )
        throws FX_EXCEPTION
    {
        Vector<HISTORY_POINT> historyPoints = this.getHistory(
            pair, interval, numTicks
        );

        Vector<MIN_MAX_POINT> minMaxPoints = new Vector<MIN_MAX_POINT>();
        for(HISTORY_POINT historyPoint : historyPoints)
        {
            minMaxPoints.add(historyPoint.getMinMaxPoint());
        }

        return minMaxPoints;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public TICK getRate(PAIR pair) throws RATE_TABLE_EXCEPTION
    {
        String req_message = String.format(
            MESSAGE.RATE_TABLE.GET_RATE, pair.getQuote(), pair.getBase()
        );

        String rep_message = this.mqm.communicate(req_message);

        StringTokenizer st = new StringTokenizer(
            rep_message.substring(req_message.length()), "|"
        );

        return new TICK(Long.parseLong(st.nextToken()),
            Double.parseDouble(st.nextToken()),
            Double.parseDouble(st.nextToken())
        );
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public boolean loggedIn()
    {
        String hostAddress = "127.0.0.1";

        try
        {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } 
        catch (UnknownHostException ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }

        String req_message = String.format(MESSAGE.RATE_TABLE.LOGGED_IN, hostAddress);
        String rep_message = this.mqm.communicate(req_message);

        StringTokenizer st = new StringTokenizer(
            rep_message.substring(req_message.length()), "|"
        );

        return Boolean.parseBoolean(st.nextToken());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception
    {
        RATE_TABLE rateTable = new RATE_TABLE();

        PAIR eur2usd = new PAIR("EUR/USD");
        PAIR usd2chf = new PAIR("USD/CHF");
        PAIR eur2chf = new PAIR("EUR/CHF");

        //if (rateTable.loggedIn())
        {
            dump(rateTable, eur2usd);
            dump(rateTable, usd2chf);
            dump(rateTable, eur2chf);
        }
    }

    private static void dump(RATE_TABLE rateTable, PAIR pair)
        throws RATE_TABLE_EXCEPTION, FX_EXCEPTION
    {
        TICK tick = rateTable.getRate(pair);

        System.out.println(String.format("[%s] %s: %s",
            System.nanoTime(), pair.toString(), tick
        ));

        //history
        Vector<HISTORY_POINT> historyPoints = rateTable.getHistory(
            pair, 1000, 15 // 1sec interval for 15 ticks
        );

        for (HISTORY_POINT historyPoint : historyPoints) {
            System.out.println(String.format("[%s] %s",
                System.nanoTime(), historyPoint
            ));
        }

        //candles
        Vector<CANDLE_POINT> candlePoints = rateTable.getCandles(
            pair, 1000, 15 // 1sec interval for 15 ticks
        );

        for (CANDLE_POINT candlePoint : candlePoints) {
            System.out.println(String.format("[%s] %s",
                System.nanoTime(), candlePoint
            ));
        }

        //min-max points
        Vector<MIN_MAX_POINT> maxMaxPoints = rateTable.getMinMaxs(
            pair, 1000, 15 // 1sec interval for 15 ticks
        );

        for (MIN_MAX_POINT maxMaxPoint : maxMaxPoints) {
            System.out.println(String.format("[%s] %s",
                System.nanoTime(), maxMaxPoint
            ));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
