package ch.blackhan.core.models;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.lang.reflect.*;

import ch.blackhan.representation.*;
import ch.blackhan.core.exceptions.*;

///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////

public abstract class ORDER implements Representable {
    
    protected PAIR pair;
    protected long units;
    protected double price;
    protected long timestamp;
    protected double lowPriceLimit;
    protected double highPriceLimit;
    protected int transactionNumber;
    protected TAKE_PROFIT_ORDER takeProfitOrder;
    protected STOP_LOSS_ORDER stopLossOrder;

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @INFO: Return JSON; cannot represent circular dependencies!
     */
    
    public String toRepresentation() throws FX_EXCEPTION
    {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = ORDER.getNonPrivateFields(this.getClass());
        if (fields.length == 0) return sb.append("}").toString();

        for (Field field : fields)
        {
            try {
                Object value = field.get(this);
                sb.append(field.getName());
                sb.append(":");
                
                if (value != null)
                {
                    sb.append(value instanceof Representable
                        ? ((Representable)value).toRepresentation() : value.toString()
                    );
                }
                else
                {
                    sb.append("null");
                }
            } catch (IllegalArgumentException ex) {
                throw new FX_EXCEPTION(ex);
            } catch (IllegalAccessException _) {
                continue; // private field
            } catch (Exception ex) {
                throw new FX_EXCEPTION(ex);
            }

            sb.append(",");
        }

        return sb.delete(sb.length() - 1, sb.length()).append("}").toString();
    }

    private static Field[] getNonPrivateFields(Class clazz)
    {
        Stack<Class> classes = new Stack<Class>();
        while (clazz != null)
        {
            classes.push(clazz); clazz = clazz.getSuperclass();
        }

        List<Field> fields = new ArrayList<Field>();
        while (!classes.empty())
        {
            fields.addAll(Arrays.asList(classes.pop().getDeclaredFields()));
        }

        return fields.toArray(new Field[] {});
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ORDER()
    {
        // pass
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public PAIR getPair() { return this.pair; }
    public void setPair(PAIR value) { this.pair = value; }

    public long getUnits() { return this.units; }
    public void setUnits(long value) { this.units = value; }

    public double getPrice() { return this.price; }
    public long getTimestamp() { return this.timestamp; }
    public int getTransactionNumber() { return this.transactionNumber; }

    public double getHighPriceLimit() { return this.highPriceLimit; }
    public void setHighPriceLimit(double value) { this.highPriceLimit = value; }

    public double getLowPriceLimit() { return this.lowPriceLimit; }
    public void setLowPriceLimit(double value) { this.lowPriceLimit = value; }

    public STOP_LOSS_ORDER getStopLoss() { return this.stopLossOrder; }
    public void setStopLoss(STOP_LOSS_ORDER value) { this.stopLossOrder = value; }

    public TAKE_PROFIT_ORDER getTakeProfit() { return this.takeProfitOrder; }
    public void setTakeProfit(TAKE_PROFIT_ORDER value) { this.takeProfitOrder = value; }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override public String toString()
    {
        return String.format("%s %s @ %s", this.pair, this.units, this.price);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
}
