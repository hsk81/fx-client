package ch.blackhan;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public class DefaultTokenizer extends StringTokenizer {

    private String lastToken = null;
    private String defaultToken = null;
    private boolean keepToken = false;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public DefaultTokenizer(String str, String delim, String defaultToken)
    {
        super (str, delim);
        this.defaultToken = defaultToken;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public String nextTokenOrDefault()
    {
        return this.nextTokenOrDefault(false, this.defaultToken);
    }
    
    public String nextTokenOrDefault(boolean keepToken)
    {
        return this.nextTokenOrDefault(keepToken, this.defaultToken);
    }

    public String nextTokenOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(this.keepToken, defaultToken);
    }

    public String nextTokenOrDefault(boolean keepToken, String defaultToken)
    {
        if (this.keepToken)
        {
            this.keepToken = keepToken;
            return this.lastToken;
        }
        else
        {
            this.keepToken = keepToken;
            this.lastToken = this.nextToken();
            
            if (this.lastToken != null)
            {
                if (!this.lastToken.equals(defaultToken))
                {
                    return this.lastToken;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Boolean nextBooleanOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? Boolean.parseBoolean(this.lastToken) : null;
    }

    public Short nextShortOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? Short.parseShort(this.lastToken) : null;
    }

    public Integer nextIntegerOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? Integer.parseInt(this.lastToken) : null;
    }

    public Long nextLongOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? Long.parseLong(this.lastToken) : null;
    }

    public Double nextDoubleOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? Double.parseDouble(this.lastToken) : null;
    }

    public String nextStringOrDefault()
    {
        return this.nextTokenOrDefault() != null
            ? this.lastToken : null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Boolean nextBooleanOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null
            ? Boolean.parseBoolean(this.lastToken) : null;
    }

    public Short nextShortOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null
            ? Short.parseShort(this.lastToken) : null;
    }

    public Integer nextIntegerOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null
            ? Integer.parseInt(this.lastToken) : null;
    }

    public Long nextLongOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null 
            ? Long.parseLong(this.lastToken) : null;
    }

    public Double nextDoubleOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null
            ? Double.parseDouble(this.lastToken) : null;
    }

    public String nextStringOrDefault(String defaultToken)
    {
        return this.nextTokenOrDefault(defaultToken) != null
            ? this.lastToken : null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public boolean hasMoreTokens()
    {
        return this.keepToken || super.hasMoreTokens();
    }

    @Override public String toString()
    {
        return this.lastToken;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
