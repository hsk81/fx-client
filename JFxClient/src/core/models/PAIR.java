package core.models;

public class PAIR implements Cloneable {

    private String base = null;
    private String quote = null;

    public PAIR()
    {
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
        PAIR pair = (PAIR)object;

        return this.getBase().compareTo(pair.getBase()) == 0 &&
               this.getQuote().compareTo(pair.getQuote()) == 0;
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

    public boolean isHalted()
    {
        //
        // TODO: Implement access to server using ZMQ!
        //

        throw new UnsupportedOperationException();
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

    public static void main(String[] args)
    {
        PAIR usd2eur = new PAIR("USD","EUR");
        PAIR eur2chf = new PAIR("EUR","CHF");
        PAIR chf2usd = new PAIR("CHF","USD");

        System.out.println(String.format("%s: %s", usd2eur.getPair(),
            usd2eur.isHalted() ? "halted" : "active"));
        System.out.println(String.format("%s: %s", eur2chf.getPair(),
            eur2chf.isHalted() ? "halted" : "active"));
        System.out.println(String.format("%s: %s", chf2usd.getPair(),
            chf2usd.isHalted() ? "halted" : "active"));
    }
}
