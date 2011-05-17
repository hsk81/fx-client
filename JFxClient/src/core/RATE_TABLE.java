package core;

import java.util.*;

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

    public TICK getRate(PAIR pair) throws ServerException, MessageException
    {
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
            
            return new TICK(Long.parseLong(array[3]),
                Double.parseDouble(array[4]), Double.parseDouble(array[5])
            );
        }
        else if (reply.startsWith("EXCEPTION"))
        {
            throw new ServerException(reply);
        }
        else
        {
            throw new MessageException(reply);
        }
    }
    
    public EVENT_MANAGER getEventManager()
    {
        throw new UnsupportedOperationException();
    }

    public boolean loggedIn()
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws Exception
    {
        RATE_TABLE rateTable = new RATE_TABLE();

        PAIR usd2eur = new PAIR("USD","EUR");
        PAIR eur2chf = new PAIR("EUR","CHF");
        PAIR chf2usd = new PAIR("CHF","USD");

        while (true)
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
