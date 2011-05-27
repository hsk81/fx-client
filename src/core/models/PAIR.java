package core.models;

import java.util.logging.*;
import core.exceptions.*;
import utils.*;

public class PAIR implements Cloneable {

    private String base = null;
    private String quote = null;

    public PAIR() { }

    public PAIR(String pair)
    {
        this.setPair(pair);
    }

    public PAIR(String base, String quote)
    {
        this.base = base;
        this.quote = quote;
    }

    @Override
    public Object clone()
    {
        return new PAIR(this.base, this.quote);
    }

    public int compareTo(PAIR pair)
    {
        return this.getPair().compareTo(pair.getPair());
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null) return false;
        if (object == this) return true;
        if (this.getClass() != object.getClass()) return false;

        return this.compareTo((PAIR)object) == 0;
    }

    public String getBase()
    {
        return this.base;
    }

    public PAIR getInverse()
    {
        return new PAIR(this.quote, this.base);
    }

    public String getPair()
    {
        return String.format("%s/%s", this.quote, this.base);
    }

    public String getQuote()
    {
        return this.quote;
    }

    @Override
    public int hashCode()
    {
        return this.getPair().hashCode();
    }

    private static final String get_halted = "PAIR|get_halted|%s|%s";
    private static final String[] get_halted_arr = get_halted.split("\\|");
    private static final int get_halted_sz = get_halted_arr.length;

    public boolean isHalted()
    {
        String message = String.format(get_halted, this.quote, this.base);

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
                Logger.getLogger(PAIR.class.getName()).log(
                    Level.SEVERE, null, new ServerException(reply)
                );
            }
            else
            {
                Logger.getLogger(PAIR.class.getName()).log(
                    Level.SEVERE, null, new MessageException(reply)
                );
            }

            /**
             * @TODO: Appareantly all pairs are prefetched somewhere and here
             *        only the cached pairs are returned. Implement!
             */
            
            return true;
        }
    }

    public void setBase(String base)
    {
        this.base = base;
    }

    public final void setPair(String pair)
    {
        String[] splits = pair.split("/");
        this.base = splits[0];
        this.quote = splits[1];
    }

    public void setQuote(String quote)
    {
        this.quote = quote;
    }

    @Override
    public String toString()
    {
        return this.getPair();
    }

    public static void main(String[] args) throws Exception
    {
        PAIR usd2eur = new PAIR("USD","EUR");
        PAIR eur2chf = new PAIR("EUR","CHF");
        PAIR chf2usd = new PAIR("CHF","USD");

        while (true)
        {
            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                usd2eur.getPair(), usd2eur.isHalted() ? "halted" : "active"
            ));

            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                eur2chf.getPair(), eur2chf.isHalted() ? "halted" : "active"
            ));

            System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
                chf2usd.getPair(), chf2usd.isHalted() ? "halted" : "active"
            ));
        }
    }
}
