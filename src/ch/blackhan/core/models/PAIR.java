package ch.blackhan.core.models;

import java.util.*;
import ch.blackhan.core.mqm.*;
import ch.blackhan.core.mqm.util.*;

public class PAIR implements Cloneable {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected MQ_MANAGER mqm = MQ_MANAGER.singleton;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private String base = null;
    private String quote = null;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public PAIR()
    {
        // pass
    }

    public PAIR(String pair)
    {
        this.setPair(pair);
    }

    public PAIR(String base, String quote)
    {
        this.base = base;
        this.quote = quote;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object clone()
    {
        return new PAIR(this.base, this.quote);
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

    @Override
    public int hashCode()
    {
        return this.toString().hashCode(); //@TODO!
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public int compareTo(PAIR pair)
    {
        return this.toString().compareTo(pair.toString());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public String getBase() { return this.base; }
    public void setBase(String base) { this.base = base; }

    public String getQuote() { return this.quote; }
    public void setQuote(String quote) { this.quote = quote; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public PAIR getInverse()
    {
        return new PAIR(this.quote, this.base);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public boolean isHalted()
    {
        String req_message = String.format(MESSAGE.PAIR.GET_HALTED, this.quote, this.base);
        String rep_message = this.mqm.communicate(req_message);

        StringTokenizer st = new StringTokenizer(
            rep_message.substring(req_message.length()), "|"
        );

        return Boolean.parseBoolean(st.nextToken());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("%s/%s", this.quote, this.base);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
