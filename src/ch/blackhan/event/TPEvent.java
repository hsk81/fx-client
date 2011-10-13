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
 * Sets up a triple of currency pairs; every triple in itself is hedged. It uses take profit
 * orders (which can cause the overall position to become un-hedged).
 */

public class TPEvent extends RATE_EVENT
{
    static final Logger logger = Logger.getLogger(TPEvent.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    final long TRADE_FREQUENCY = (long)(5.250 * 1E9); // nano sec
    final long TRADE_UNITS = 10;
    
    final double TP_LIMIT = 0.00250;
    final double SL_LIMIT = 0.00100;
    final double TS_LIMIT = 0.00100;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    ACCOUNT account;

    PAIR pair_st, pair_nd, pair_rd;
    double price_st, price_nd, price_rd;
    long st_time, nd_time, rd_time;
    
    int direction;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public TPEvent(ACCOUNT account, int direction, PAIR pair_st, PAIR pair_nd, PAIR pair_rd)
    {
        this.account = account;
        
        this.pair_st = pair_st; this.st_time = System.nanoTime();
        this.pair_nd = pair_nd; this.nd_time = System.nanoTime();
        this.pair_rd = pair_rd; this.rd_time = System.nanoTime();
        
        this.direction = direction;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    @Override public boolean match (EVENT_INFO ei)
    {
        RATE_EVENT_INFO rei = (RATE_EVENT_INFO) ei;

        if (rei.getPair().compareTo(this.pair_st) == 0)
        {
            if (System.nanoTime() - this.st_time < this.TRADE_FREQUENCY)
            {
                return false;
            }

            this.st_time = System.nanoTime();
            return true;
        }
        
        if (rei.getPair().compareTo(this.pair_nd) == 0)
        {
            if (System.nanoTime() - this.nd_time < this.TRADE_FREQUENCY)
            {
                return false;
            }

            this.nd_time = System.nanoTime();
            return true;
        }
        
        if (rei.getPair().compareTo(this.pair_rd) == 0)
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

        if (this.direction > 0)
        {
            this.handle_pos(ei,em);
        } 
        else if (this.direction < 0)
        {
            this.handle_neg(ei,em);
        }
        else
        {
            return;
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    private void handle_pos(EVENT_INFO ei, EVENT_MANAGER em)
    {
        RATE_EVENT_INFO rei = (RATE_EVENT_INFO) ei;
        MARKET_ORDER mkt = new MARKET_ORDER();
        
        if (rei.getPair().compareTo(pair_st) == 0) // header of triangle
        {
            double units = (+1.0) * this.TRADE_UNITS;
            mkt.setUnits(java.lang.Math.round((units > 0) ? units : 0));
            mkt.setPair(this.pair_st);

            this.price_st = rei.getTick().getAsk();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_st + this.TP_LIMIT / units)
            );
        }

        if (rei.getPair().compareTo(pair_nd) == 0) // trailer of pair_st
        {
            double units = (-1.0) * this.TRADE_UNITS;
            mkt.setUnits(java.lang.Math.round((units < 0) ? units : 0));
            mkt.setPair(this.pair_nd);
            
            this.price_nd = rei.getTick().getBid();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_nd + this.TP_LIMIT / units)
            );
        }

        if (rei.getPair().compareTo(pair_rd) == 0) // trailer of pair_st
        {
            double units = (+1.0) * this.TRADE_UNITS * this.price_st;
            mkt.setUnits(java.lang.Math.round((units > 0) ? units : 0));
            mkt.setPair(this.pair_rd);
            
            this.price_rd = rei.getTick().getAsk();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_rd + this.TP_LIMIT / units)
            );
        }

        this.execute(this.account, mkt, em);
    }

    private void handle_neg(EVENT_INFO ei, EVENT_MANAGER em)
    {
        RATE_EVENT_INFO rei = (RATE_EVENT_INFO) ei;
        MARKET_ORDER mkt = new MARKET_ORDER();
        
        if (rei.getPair().compareTo(pair_st) == 0) // header of triangle
        {
            double units = (-1.0) * this.TRADE_UNITS;
            mkt.setUnits(java.lang.Math.round((units < 0) ? units : 0));
            mkt.setPair(this.pair_st);

            this.price_st = rei.getTick().getBid();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_st + this.TP_LIMIT / units)
            );
        }

        if (rei.getPair().compareTo(pair_nd) == 0) // trailer of pair_st
        {
            double units = (+1.0) * this.TRADE_UNITS;
            mkt.setUnits(java.lang.Math.round((units > 0) ? units : 0));
            mkt.setPair(this.pair_nd);
            
            this.price_nd = rei.getTick().getAsk();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_nd + this.TP_LIMIT / units)
            );
        }

        if (rei.getPair().compareTo(pair_rd) == 0) // trailer of pair_st
        {
            double units = (-1.0) * this.TRADE_UNITS * this.price_st;
            mkt.setUnits(java.lang.Math.round((units < 0) ? units : 0));
            mkt.setPair(this.pair_rd);
            
            this.price_rd = rei.getTick().getBid();
            mkt.setTakeProfit(
                new TAKE_PROFIT_ORDER(this.price_rd + this.TP_LIMIT / units)
            );
        }
        
        this.execute(this.account, mkt, em);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    private void execute(ACCOUNT account, MARKET_ORDER mkt, EVENT_MANAGER em)
    {
        try
        {
            if (mkt.getUnits() != 0)
            {
                synchronized (account)
                {
                    this.account.execute(mkt);
                }
            }
        }
        catch (FX_EXCEPTION ex)
        {
            logger.log(Level.SEVERE, null, ex);
            synchronized (this) { em.remove(this); }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
