package ch.blackhan.event;

/**
 * Harvests currency triple if possible; if so, reduce triple size using the
 * realized profit.
 */

import java.util.*;
import java.util.logging.*;

import ch.blackhan.core.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.exceptions.*;

/**
 * @author hkarahan
 */

public class H3Event extends RATE_EVENT {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final Logger logger = Logger.getLogger(H3Event.class.getName());

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    final long MODIFY_FREQUENCY = (long) (59.0 * 1E9); // nano sec
    final double SL_THRESHOLD = 0.005;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    ACCOUNT account;
    
    PAIR st_pair, nd_pair, rd_pair;
    TICK st_tick, nd_tick, rd_tick;
    long st_time, nd_time, rd_time;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public H3Event(ACCOUNT account, PAIR pair_st, PAIR pair_nd, PAIR pair_rd) {

        this.account = account;

        this.st_pair = pair_st;
        this.st_tick = null;
        this.st_time = System.nanoTime();
        
        this.nd_pair = pair_nd;
        this.nd_tick = null;
        this.nd_time = System.nanoTime();
        
        this.rd_pair = pair_rd;
        this.rd_tick = null;
        this.rd_time = System.nanoTime();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean match(EVENT_INFO ei) {

        RATE_EVENT_INFO rei = (RATE_EVENT_INFO)ei;

        //
        // CHECK IF TICK FROM 1 OF 3 PAIRs (and APPLY TIME FILTER)
        //

        if (rei.getPair().compareTo(this.st_pair) == 0) {

            if (System.nanoTime() - this.st_time < this.MODIFY_FREQUENCY) {
                return false;
            }

            this.st_time = System.nanoTime();
            this.st_tick = rei.getTick();

            return true;
        }

        if (rei.getPair().compareTo(this.nd_pair) == 0) {

            if (System.nanoTime() - this.nd_time < this.MODIFY_FREQUENCY) {
                return false;
            }

            this.nd_time = System.nanoTime();
            this.nd_tick = rei.getTick();

            return true;
        }

        if (rei.getPair().compareTo(this.rd_pair) == 0) {

            if (System.nanoTime() - this.rd_time < this.MODIFY_FREQUENCY) {
                return false;
            }

            this.rd_time = System.nanoTime();
            this.rd_tick = rei.getTick();

            return true;
        }

        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void handle(EVENT_INFO ei, EVENT_MANAGER em) {

        if (!em.getEvents().contains(this)) {
            return;
        }

        //
        // CAST FROM GENERAL TO SPECIFIC TYPE
        //

        RATE_EVENT_INFO rei = (RATE_EVENT_INFO)ei;

        //
        // UPDATE CURRENCY TRIANGLE and EXECUTE EVTL. TRADEs
        //

        try {

            this.trade(this.account, rei);

        } catch (FX_EXCEPTION ex) {
            logger.log(Level.SEVERE, null, ex);

            synchronized (this) {
                em.remove(this);
            } return;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    private void trade(ACCOUNT account, RATE_EVENT_INFO rei)
        throws ACCOUNT_EXCEPTION, FX_EXCEPTION {

        MARKET_ORDER[] mos = null;

        synchronized (account) {

            Vector<MARKET_ORDER> ts = account.getTrades();
            mos = new MARKET_ORDER[ts.size()];
            ts.copyInto(mos);

        }

        for (MARKET_ORDER mo : mos) {

            if (mo.getPair().compareTo(rei.getPair()) != 0) {
                continue;
            }

            if (this.check(mo, rei.getTick())) {
                
                double old = this.account.getBalance();

                synchronized (account) {
                    account.close(mo);
                }
                
                this.reduce(account, mo, this.account.getBalance() - old);
            }

        }
    }

    private boolean check(MARKET_ORDER mo, TICK tick) {

        if (tick == null) {
            return false;
        }

        double pnl = mo.getUnrealizedPL(tick);

        if (pnl < 0.0) {
            return false;
        }

        double p0 = mo.getPrice();
        double p1 = pnl / (double)mo.getUnits();

        if (p1 / p0 < this.SL_THRESHOLD) {
            return false;
        }

        return true;
    }

    private void reduce(ACCOUNT account, MARKET_ORDER mo, double pnl) {

        assert (account != null); //@TODO!?
        
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
