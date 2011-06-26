package ch.blackhan.core.models;

public class MARKET_ORDER {

    public PAIR getPair() {
        return new PAIR(); //throw new UnsupportedOperationException();
    }

    public double getUnrealizedPL(TICK tick) {
        return 0.0; //throw new UnsupportedOperationException();
    }

    public double getPrice() {
        return 0.0; //throw new UnsupportedOperationException();
    }

    public long getUnits() {
        return 0L; //throw new UnsupportedOperationException();
    }

}
