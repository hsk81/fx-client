package ch.blackhan.core.models;

import java.util.logging.*;

import ch.blackhan.core.mqm.*;
import ch.blackhan.core.exceptions.*;

public class PAIR implements Cloneable {

    protected MQ_MANAGER mqm = MQ_MANAGER.singleton;
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
        return this.toString().compareTo(pair.toString());
    }

    @Override
    public boolean equals(Object object)
    {
        if (this != object)
        {
            if (object instanceof PAIR)
            {
                return this.compareTo((PAIR)object) == 0;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public String getBase() { return this.base; }
    public void setBase(String base) { this.base = base; }

    public String getQuote() { return this.quote; }
    public void setQuote(String quote) { this.quote = quote; }

    public String getPair()
    {
        return this.toString();
    }
    
    public final void setPair(String pair)
    {
        String[] splits = pair.split("/");
        this.quote = splits[0];
        this.base = splits[1];
    }

    public PAIR getInverse()
    {
        return new PAIR(this.quote, this.base);
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    private static final String get_halted = "PAIR|get_halted|%s|%s";
    private static final String[] get_halted_arr = get_halted.split("\\|");
    private static final int get_halted_sz = get_halted_arr.length;

    public boolean isHalted()
    {
        String message = String.format(get_halted, this.quote, this.base);

        this.mqm.req().send(message.getBytes(), 0);
        byte[] bytes = this.mqm.req().recv(0);
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
                    Level.SEVERE, null, new SERVER_EXCEPTION(reply)
                );
            }
            else
            {
                Logger.getLogger(PAIR.class.getName()).log(
                    Level.SEVERE, null, new MESSAGE_EXCEPTION(reply)
                );
            }

            return true;
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s/%s", this.quote, this.base);
    }

    public static void main(String[] args) throws Exception
    {
        PAIR eur2usd = new PAIR("EUR/USD");
        PAIR usd2chf = new PAIR("USD/CHF");
        PAIR eur2chf = new PAIR("EUR/CHF");

        System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
            eur2usd.toString(), eur2usd.isHalted() ? "halted" : "active"
        ));

        System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
            eur2chf.toString(), eur2chf.isHalted() ? "halted" : "active"
        ));

        System.out.println(String.format("[%s] %s: %s", System.nanoTime(),
            usd2chf.toString(), usd2chf.isHalted() ? "halted" : "active"
        ));
    }
}
