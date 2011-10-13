package ch.blackhan.event;

/**
 * @author Hasan Karahan <hasan.karahan81@gmail.com>
 */

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Check every trade if a (traIling) S/L is necessary; the trailing is discrete to avoid too
 * much updates.
 */

public class TSEvent extends RATE_EVENT
{
    static final Logger logger =  Logger.getLogger(MOEvent.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    final long MODIFY_FREQUENCY = (long)(59.0 * 1E9); // nano sec
    final double SL_PERCENT = 0.8; // of actual (positive!) pnl
    final double SL_THRESHOLD = 0.01; // discretization threashold
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    ACCOUNT account;
    
    PAIR pair_st, pair_nd, pair_rd;
    long st_time, nd_time, rd_time;
        
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public TSEvent(ACCOUNT account, PAIR pair_st, PAIR pair_nd, PAIR pair_rd)
    {
        if (account == null) throw new IllegalArgumentException("account");
        if (pair_st == null) throw new IllegalArgumentException("pair_st");
        if (pair_nd == null) throw new IllegalArgumentException("pair_nd");
        if (pair_rd == null) throw new IllegalArgumentException("pair_rd");
        
        this.account = account;
                
        this.pair_st = pair_st; this.st_time = System.nanoTime();
        this.pair_nd = pair_nd; this.nd_time = System.nanoTime();
        this.pair_rd = pair_rd; this.rd_time = System.nanoTime();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    @Override public boolean match (EVENT_INFO ei)
    {
        RATE_EVENT_INFO rei = (RATE_EVENT_INFO) ei;

        if (rei.getPair().compareTo(this.pair_st) == 0)
        {
            if (System.nanoTime() - this.st_time < this.MODIFY_FREQUENCY)
            {
                return false;
            }

            this.st_time = System.nanoTime();
            return true;
        }
        
        if (rei.getPair().compareTo(this.pair_nd) == 0)
        {
            if (System.nanoTime() - this.nd_time < this.MODIFY_FREQUENCY)
            {
                return false;
            }

            this.nd_time = System.nanoTime();
            return true;
        }
        
        if (rei.getPair().compareTo(this.pair_rd) == 0)
        {
            if (System.nanoTime() - this.rd_time < this.MODIFY_FREQUENCY)
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
        
        try
        {
            this.trailSLs(this.account, (RATE_EVENT_INFO)ei);
        }
        catch (FX_EXCEPTION ex)
        {
            logger.log(Level.SEVERE, null, ex);            
            synchronized (this) { em.remove(this); }
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    private void trailSLs (ACCOUNT account, RATE_EVENT_INFO rei)
        throws ACCOUNT_EXCEPTION, FX_EXCEPTION
    {
        MARKET_ORDER[] mkts = null;
        
        synchronized (account)
        {
            Vector<MARKET_ORDER> mos = account.getTrades();
            mkts = new MARKET_ORDER[mos.size()];
            mos.copyInto(mkts);
        }

        for (MARKET_ORDER mo : mkts)
        {
            this.executeSL(account, mo, rei.getTick());
        }
    }

    private boolean executeSL(ACCOUNT account, MARKET_ORDER mo, TICK tick) throws FX_EXCEPTION
    {
        if (tick == null) return false;
        double pnl = mo.getUnrealizedPL(tick);
        if (pnl <= 0.0) return false;

        STOP_LOSS_ORDER sl = mo.getStopLoss();

        double p0 = (sl.getPrice() > 0.0) ? sl.getPrice() : mo.getPrice();
        double p1 = mo.getPrice() + pnl / (double) mo.getUnits() * this.SL_PERCENT;

        assert ((mo.getUnits() > 0) && (p1 < tick.getBid()));
        assert ((mo.getUnits() < 0) && (p1 > tick.getBid()));

        double q0 = p0 / mo.getPrice();
        double q1 = p1 / mo.getPrice();
        
        if (q1 - q0 < this.SL_THRESHOLD)
        {
            return false;
        }
        else
        {
            mo.setStopLoss(new STOP_LOSS_ORDER(p1));
            synchronized (account) { account.modify(mo); }

            return true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
