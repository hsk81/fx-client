package core.models;

import core.exceptions.*;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

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

    /**
     * @TODO: Refactor ZQM context (and REQ socket?) away!
     */
    public boolean isHalted() throws ServerException, MessageException
    {
        Context context = ZMQ.context(1);

        Socket req = context.socket(ZMQ.REQ);
        req.connect("tcp://localhost:6666");

        String pattern = "PAIR|get_halted|%s";
        String message = String.format(pattern, this.getPair());

        req.send(message.getBytes(), 0);
        byte[] bytes = req.recv(0);
        String reply = new String(bytes);

        req.close();
        context.term();

        if (reply.compareTo(message + "|True") == 0)
        {
            return true;
        }
        else if (reply.compareTo(message + "|False") == 0)
        {
            return false;
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
        String path = System.getProperty("java.library.path");
        System.out.println(path);
        
        PAIR usd2eur = new PAIR("USD","EUR");
        PAIR eur2chf = new PAIR("EUR","CHF");
        PAIR chf2usd = new PAIR("CHF","USD");

        while (true)
        {
            System.out.println(String.format("[%s] %s: %s", System.currentTimeMillis(),
                usd2eur.getPair(), usd2eur.isHalted() ? "halted" : "active"));
            System.out.println(String.format("[%s] %s: %s", System.currentTimeMillis(),
                eur2chf.getPair(), eur2chf.isHalted() ? "halted" : "active"));
            System.out.println(String.format("[%s] %s: %s", System.currentTimeMillis(),
                chf2usd.getPair(), chf2usd.isHalted() ? "halted" : "active"));
        }
    }
}
