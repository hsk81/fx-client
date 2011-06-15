package core;

import java.net.*;
import java.util.*;
import java.util.logging.*;

import core.models.utils.*;
import core.exceptions.*;
import core.models.*;
import core.utils.*;

public class RATE_TABLE {

    private RATE_EVENT_MANAGER eventManager = null;
    public EVENT_MANAGER getEventManager()
    {
        if (this.eventManager != null)
        {
            return this.eventManager;
        }
        else
        {
            return this.eventManager = new RATE_EVENT_MANAGER();
        }
    }

    private static final String get_history = "RATE_TABLE|get_history|%s|%s|%s|%s";
    private static final String[] get_history_arr = get_history.split("\\|");
    private static final int get_history_sz = get_history_arr.length;
    
    public Vector<HISTORY_POINT> getHistory(
        PAIR pair, long interval, int numTicks
    )
        throws FxException
    {
        String message = String.format(
            get_history, pair.getQuote(), pair.getBase(), interval, numTicks
        );

        MQManager.singleton.req.send(message.getBytes(), 0);
        byte[] bytes = MQManager.singleton.req.recv(0);
        String reply = new String(bytes);
        
        if (reply.startsWith(message))
        {
            String[] array = reply.split("\\|");

            Vector<HISTORY_POINT> historyPoints = new Vector<HISTORY_POINT>();
            for(int idx = get_history_sz; idx < array.length; idx += 9)
            {
                historyPoints.add(new HISTORY_POINT(
                    Long.parseLong(array[idx]),
                    Double.parseDouble(array[idx + 1]),
                    Double.parseDouble(array[idx + 2]),
                    Double.parseDouble(array[idx + 3]),
                    Double.parseDouble(array[idx + 4]),
                    Double.parseDouble(array[idx + 5]),
                    Double.parseDouble(array[idx + 6]),
                    Double.parseDouble(array[idx + 7]),
                    Double.parseDouble(array[idx + 8])
                ));
            }

            return historyPoints;
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new ServerException(reply)
                );
            }
            else
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new MessageException(reply)
                );
            }

            throw new FxException(reply);
        }
    }

    public Vector<CANDLE_POINT> getCandles(
        PAIR pair, long interval, int numTicks
    )
        throws FxException
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

    public Vector<MIN_MAX_POINT> getMinMaxs(
        PAIR pair, long interval, int numTicks
    )
        throws FxException
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

    private static final String get_rate = "RATE_TABLE|get_rate|%s|%s";
    private static final String[] get_rate_arr = get_rate.split("\\|");
    private static final int get_rate_sz = get_rate_arr.length;

    public TICK getRate(PAIR pair) throws RateTableException
    {
        String message = String.format(
            get_rate, pair.getQuote(), pair.getBase()
        );

        MQManager.singleton.req.send(message.getBytes(), 0);
        byte[] bytes = MQManager.singleton.req.recv(0);
        String reply = new String(bytes);

        if (reply.startsWith(message))
        {
            String[] array = reply.split("\\|");
            
            return new TICK(Long.parseLong(array[array.length - 3]),
                Double.parseDouble(array[array.length - 2]),
                Double.parseDouble(array[array.length - 1])
            );
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new ServerException(reply)
                );
            }
            else
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new MessageException(reply)
                );
            }

            throw new RateTableException(reply);
        }
    }
    
    private static final String logged_in = "RATE_TABLE|logged_in|%s";
    private static final String[] logged_in_arr = logged_in.split("\\|");
    private static final int logged_in_sz = logged_in_arr.length;

    public boolean loggedIn()
    {
        InetAddress ip = null;

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(RATE_TABLE.class.getName()).log(
                Level.SEVERE, null, ex
            );
        }

        String message = String.format(logged_in, ip.getHostAddress());
        
        MQManager.singleton.req.send(message.getBytes(), 0);
        byte[] bytes = MQManager.singleton.req.recv(0);
        String reply = new String(bytes);

        if (reply.startsWith(message))
        {
            String[] array = reply.split("\\|");
            return array[array.length - 1].compareTo("True") == 0;
        }
        else
        {
            if (reply.startsWith("EXCEPTION"))
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new ServerException(reply)
                );
            }
            else
            {
                Logger.getLogger(RATE_TABLE.class.getName()).log(
                    Level.SEVERE, null, new MessageException(reply)
                );
            }
        }

        return false;
    }

    public static void main(String[] args) throws Exception
    {
        RATE_TABLE rateTable = new RATE_TABLE();

        PAIR eur2usd = new PAIR("USD","EUR");
        PAIR usd2chf = new PAIR("CHF","USD");
        PAIR chf2eur = new PAIR("EUR","CHF");

        while (rateTable.loggedIn())
        {
            dump(rateTable, eur2usd);
            dump(rateTable, usd2chf);
            dump(rateTable, chf2eur);
        }
    }

    private static void dump(RATE_TABLE rateTable, PAIR pair)
        throws RateTableException, FxException
    {
        TICK tick = rateTable.getRate(pair);

        System.out.println(String.format("[%s] %s: %s",
            System.nanoTime(), pair.getPair(), tick
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
}
