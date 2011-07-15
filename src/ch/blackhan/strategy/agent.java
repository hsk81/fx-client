package ch.blackhan.strategy;

/**
 * This agent trades in 3 different fx.markets: It keeps buying A/B, selling
 * A/C and buying B/C. Buy establishing this triange it effectively hedges its
 * actual position agains any markets moves.
 */

import ch.blackhan.core.*;
import ch.blackhan.core.models.*;
import ch.blackhan.core.exceptions.*;
import ch.blackhan.event.*;

import java.util.*;
import java.util.logging.*;
import java.util.Observer;
import java.util.Observable;

/**
 * @author hkarahan
 */

public class agent implements Observer {

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    static final Logger logger = Logger.getLogger(agent.class.getName());
    static final Object notifyer = new Object();

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    //
    // VARIUS TIMEOUTs USED IN THE SYSTEM
    //
    
    final int DEFAULT_TIMEOUT = -1; // [ms]
    
    //
    // USER's CONNECTION ATHENTICATION
    //
    
    String username = "user";
    String password = "****";

    //
    // LONG AND SHORT SUB-ACCOUNTs' IDs
    //
    
    final long DEFAULT_LG_ACCOUNT_ID = (long) 898646; // long
    Long LG_ACCOUNT_ID;
    final long DEFAULT_SH_ACCOUNT_ID = (long) 689281; // short
    Long SH_ACCOUNT_ID;
    
    //
    // FX CLIENT, RATETABLE
    //
    
    CLIENT fxclient;
    ACCOUNT accLG;
    ACCOUNT accSH;
    RATE_TABLE rateTable;

    //
    // CURRENCY PAIRs on WHICH THIS AGENT WORKs ON
    //
    
    PAIR pair_st; // e.g. EUR/USD (long  +/- or short -/+)
    PAIR pair_nd; // e.g. EUR/CHF (short +/- or long  -/+)
    PAIR pair_rd; // e.g. USD/CHF (long  +/- or short -/+)
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private boolean clientLogin (CLIENT fxclient) {
        
        try {
            
            if (this.fxclient != null) {
                this.fxclient.logout();
            }
            
            this.fxclient = fxclient;
            this.fxclient.setTimeout(this.DEFAULT_TIMEOUT);
            this.fxclient.setWithRateThread(true);
            this.fxclient.setWithKeepAliveThread(true);
            this.fxclient.login(username, password);

        } catch (INVALID_USER_EXCEPTION ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (INVALID_PASSWORD_EXCEPTION ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SESSION_EXCEPTION ex) {
            logger.log(Level.SEVERE, null, ex);
        }        
        
        return this.fxclient.isLoggedIn();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private ACCOUNT getAccount(long accountId) {
        
        ACCOUNT result = new ACCOUNT(); //null; @TODO!
        
        try {
            
            USER me = this.fxclient.getUser();
            Vector<ACCOUNT> accounts = me.getAccounts();

            for (ACCOUNT account : accounts) {
                if (account.getAccountId() == accountId) {
                    result = account;
                }
            }
            
        } catch (SESSION_EXCEPTION ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public void update(Observable source, Object status) {
        
        if (source == this.fxclient) {
            if (status == CLIENT.CONNECTED) {
                
                logger.log(Level.INFO, CLIENT.CONNECTED);
                
                //
                // GET LONG and SHORT SUB-ACCOUNTs
                //

                this.accLG = this.getAccount(this.LG_ACCOUNT_ID);
                this.accSH = this.getAccount(this.SH_ACCOUNT_ID);

                //
                // SETUP EVENT MANAGEMENT
                //

                try {
                    
                    this.rateTable = this.fxclient.getRateTable();
                    
                } catch (SESSION_DISCONNECTED_EXCEPTION ex) {
                    logger.log(Level.SEVERE, null, ex);
                }

                //
                // REGISTER SOME EVENTs TO BE HANDLED
                //

                H3Event h3e = new H3Event (this.accLG,
                      this.pair_st, this.pair_nd, this.pair_rd);

                this.rateTable.getEventManager().add(h3e);

                //
                // RE-ACTIVATE OBSERVER PATTERN
                //
                
                this.fxclient.addObserver(this);
                
            } else if (status == CLIENT.DISCONNECTED) {
                
                logger.log(Level.INFO, CLIENT.DISCONNECTED);
                
                //
                // DE-ACTIVATE OBSERVER PATTERN
                //
                
                this.fxclient.deleteObserver(this);
                
                //
                // TRY TO RECONNECT FXCLIENT
                //
                
                boolean login = this.clientLogin(new GAME());
                
                //
                // NOTIFY MAIN THREAD TO CONTINUE (TO EXIT APPLICATION)
                //

                if (!login) {
                    
                    System.exit(-1);
                    
                } else {
                    this.update(this.fxclient, CLIENT.CONNECTED);
                }
            }            
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    public static void main(String[] args) {

        agent self = new agent();

        //
        // PROCESS ARGUMENTs: FX.MARKET & ACCOUNTs
        //
        
        assert (args.length >= 3);
        
        self.pair_st = new PAIR(args[0]);
        self.pair_nd = new PAIR(args[1]);
        self.pair_rd = new PAIR(args[2]);

        self.LG_ACCOUNT_ID = (args.length >= 4) ? 
            new Long(args[3]) : self.DEFAULT_LG_ACCOUNT_ID;
        self.SH_ACCOUNT_ID = (args.length >= 5) ? 
            new Long(args[4]) : self.DEFAULT_SH_ACCOUNT_ID;

        //
        // CREATE FXCLIENT & LOGIN INTO SYSTEM
        //
        
        if (!self.clientLogin(new GAME()) || !self.clientLogin(new GAME())) {
            System.exit(-1);
        } else {            
            self.update(self.fxclient, CLIENT.CONNECTED);
        }

        //
        // START WAITING FOR TERMINAL SIGNAL
        //
        
        while (true) {
            try {

                //
                // WAIT FOR AN INDEFINITE PERIOD OF TIME
                //
                
                synchronized (notifyer) {
                    notifyer.wait(); //[ms]
                }
                
            } catch (InterruptedException ie) {
                logger.log(Level.SEVERE, null, ie);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
