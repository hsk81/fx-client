package ch.blackhan.event;

/**
 * @author Hasan Karahan <hasan.karahan81@gmail.com>
 */

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.logging.*;

import ch.blackhan.core.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Sets up a triple of currency pairs; additionally it minimizes the total exposure.
 */

public class MOEvent extends RATE_EVENT {
    
    static final Logger logger = Logger.getLogger(MOEvent.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    final long TRADE_UNITS = 1;
    final long TRADE_FREQUENCY = (long)(59.0 * 1E9); // nano sec

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    final ACCOUNT account;
    
    PAIR pair_st, pair_nd, pair_rd;
    POSITION pos_st, pos_nd, pos_rd;
    long units_st, units_nd, units_rd;
    double price_st, price_nd, price_rd;
    long st_time, nd_time, rd_time;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public MOEvent(ACCOUNT account, PAIR pair_st, PAIR pair_nd, PAIR pair_rd) {
        
        if (account == null) throw new IllegalArgumentException("account");
        if (pair_st == null) throw new IllegalArgumentException("pair_st");
        if (pair_nd == null) throw new IllegalArgumentException("pair_nd");
        if (pair_rd == null) throw new IllegalArgumentException("pair_rd");
        
        this.account = account;
        
        this.pair_st = pair_st; this.st_time = System.nanoTime();
        this.pair_nd = pair_nd; this.nd_time = System.nanoTime();
        this.pair_rd = pair_rd; this.rd_time = System.nanoTime();

        try
        {
            this.updatePositions();
        } 
        catch (FX_EXCEPTION ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
        
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    @Override public boolean match (EVENT_INFO EI)
    {
        RATE_EVENT_INFO REI = (RATE_EVENT_INFO) EI;

        if (REI.getPair().compareTo(this.pair_st) == 0)
        {
            if (System.nanoTime() - this.st_time < this.TRADE_FREQUENCY)
            {
                return false;
            }

            this.st_time = System.nanoTime();
            return true;
        }
        
        if (REI.getPair().compareTo(this.pair_nd) == 0)
        {
            if (System.nanoTime() - this.nd_time < this.TRADE_FREQUENCY)
            {
                return false;
            }

            this.nd_time = System.nanoTime();
            return true;
        }
        
        if (REI.getPair().compareTo(this.pair_rd) == 0)
        {
            if (System.nanoTime() - this.rd_time < this.TRADE_FREQUENCY)
            {
                return false;
            }

            this.rd_time = System.nanoTime();
            return true;
        }
        
        return false;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void handle(EVENT_INFO ei, EVENT_MANAGER em)
    {
        if (!em.getEvents().contains(this))
        {
            return;
        }
        
        RATE_EVENT_INFO rei = (RATE_EVENT_INFO) ei;
        MARKET_ORDER mkt = new MARKET_ORDER();
        
        if (rei.getPair().compareTo(pair_st) == 0) // header of triangle
        {
            double units = (+1.0) * this.TRADE_UNITS;
            mkt.setUnits(java.lang.Math.round((units > 0) ? units : 0));
            mkt.setPair(this.pair_st);
        }

        if (rei.getPair().compareTo(pair_nd) == 0) // trailer of pair_st
        {
            double coeff = (+1.0);
            double units = (-1.0) * (coeff * this.units_st + this.units_nd);
            mkt.setUnits(java.lang.Math.round((units < 0) ? units : 0));
            mkt.setPair(this.pair_nd);
        }

        if (rei.getPair().compareTo(pair_rd) == 0) // trailer of pair_st
        {
            double coeff = this.price_st;
            double units = (+1.0) * (coeff * this.units_st - this.units_rd);
            mkt.setUnits(java.lang.Math.round((units > 0) ? units : 0));
            mkt.setPair(this.pair_rd);
        }
        
        try
        {
            if (mkt.getUnits() != 0)
            {
                synchronized (this.account) 
                {
                    this.account.execute(mkt);
                }
            }
        }
        catch (FX_EXCEPTION ex)
        {
            logger.log(Level.SEVERE, null, ex);
            synchronized (this) { em.remove(this); }
            return;
        }
        
        try
        {
            this.updatePositions();
        }
        catch (FX_EXCEPTION ex)
        {
            logger.log(Level.SEVERE, null, ex);            
            synchronized (this) { em.remove(this); }
            return;
        }
    }

    /**
     * Update positions size for each currency pair.
     */
    
    private void updatePositions () throws FX_EXCEPTION
    {
        synchronized (this.account)
        {
            this.pos_st = this.account.getPosition(this.pair_st);
        }

        if (this.pos_st != null)
        {
            this.units_st = this.pos_st.getUnits();
            this.price_st = this.pos_st.getPrice();
        }
        else
        {
            this.units_st = 0;
            this.price_st = 0.0;
        }

        this.pos_nd = this.account.getPosition(this.pair_nd);

        if (this.pos_nd != null)
        {
            this.units_nd = this.pos_nd.getUnits();
            this.price_nd = this.pos_nd.getPrice();
        }
        else
        {
            this.units_nd = 0;
            this.price_nd = 0.0;
        }

        this.pos_rd = this.account.getPosition(this.pair_rd);

        if (this.pos_rd != null)
        {
            this.units_rd = this.pos_rd.getUnits();
            this.price_rd = this.pos_rd.getPrice();
        }
        else
        {
            this.units_rd = 0;
            this.price_rd = 0.0;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
