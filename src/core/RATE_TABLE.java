package core;

import java.net.*;
import java.util.*;
import java.util.logging.*;

import core.models.*;
import core.exceptions.*;
import core.models.util.*;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

public class RATE_TABLE {
    
    public Vector<CANDLE_POINT> getCandles(PAIR pair, long interval, int numTicks)
    {
        throw new UnsupportedOperationException();
    }

    public Vector<HISTORY_POINT> getHistory(PAIR pair, long interval, int numTicks)
    {
        throw new UnsupportedOperationException();
    }
    
    public Vector<MIN_MAX_POINT> getMinMaxs(PAIR pair, long interval, int numTicks)
    {
        throw new UnsupportedOperationException();
    }

    public TICK getRate(PAIR pair) throws RateTableException
    {
        /**
         * @TODO: Refactor ZQM context (and REQ socket?) away!
         */

        Context context = ZMQ.context(1);
        Socket req = context.socket(ZMQ.REQ);
        req.connect("tcp://localhost:6666");

        String pattern = "RATE_TABLE|get_rate|%s";
        String message = String.format(pattern, pair.getPair());

        req.send(message.getBytes(), 0);
        byte[] bytes = req.recv(0);
        String reply = new String(bytes);

        req.close();
        context.term();

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
    
    public EVENT_MANAGER getEventManager()
    {
        throw new UnsupportedOperationException();
    }

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

        /**
         * @TODO: Refactor ZQM context (and REQ socket?) away!
         */
        
        Context context = ZMQ.context(1);
        Socket req = context.socket(ZMQ.REQ);
        req.connect("tcp://localhost:6666");

        String pattern = "RATE_TABLE|logged_in|%s";
        String message = String.format(pattern, ip.getHostAddress());

        req.send(message.getBytes(), 0);
        byte[] bytes = req.recv(0);
        String reply = new String(bytes);

        req.close();
        context.term();

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

        PAIR usd2eur = new PAIR("USD","EUR");
        PAIR eur2chf = new PAIR("EUR","CHF");
        PAIR chf2usd = new PAIR("CHF","USD");

        while (rateTable.loggedIn())
        {
            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                usd2eur.getPair(), rateTable.getRate (usd2eur)
            ));

            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                eur2chf.getPair(), rateTable.getRate (eur2chf)
            ));

            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                chf2usd.getPair(), rateTable.getRate (eur2chf)
            ));
        }
    }
}
