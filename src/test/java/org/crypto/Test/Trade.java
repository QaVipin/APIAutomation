package org.crypto.Test;


import java.time.Instant;

public class Trade {

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    private long quantity;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return quantity == trade.quantity &&
                time == trade.time;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public boolean equals(Trade trade){

        if(Instant.ofEpochMilli(this.getTime()).equals(Instant.ofEpochMilli(trade.getTime()))
          && (Long.compare(this.getQuantity(),trade.getQuantity()))==0){
            return true;
        }

        return false;
    }
}
