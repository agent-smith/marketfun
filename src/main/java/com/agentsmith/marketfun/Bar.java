package com.agentsmith.marketfun;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A bar represents the interesting information relative for one instrument for an entire day.
 * <p/>
 * User: rmarquez
 * Date: 11/24/2013
 * Time: 17:35
 */
public class Bar
{
    public String symbol;
    public Date date;
    public double open;
    public double high;
    public double low;
    public double close;
    public int volume;

    public double upperBollingerBand = -1.0;
    public double middleBollingerBand = -1.0;
    public double lowerBollingerBand = -1.0;

    // Key = nDays, Value = SMA (e.g. get the 10-day SMA from SMAs.get(10))
    public Map<Integer, Double> SMAs = new HashMap<>();

    // Key = nDays, Value = EMA (e.g. get the 10-day EMA from EMAs.get(10))
    public Map<Integer, Double> EMAs = new HashMap<>();

    public Bar(String symbol,
               Date date,
               double open,
               double high,
               double low,
               double close,
               int volume)
    {
        this(symbol, date, open, high, low, close, volume, -1.0, -1.0, -1.0);
    }

    public Bar(String symbol,
               Date date,
               double open,
               double high,
               double low,
               double close,
               int volume,
               double upperBollingerBand,
               double middleBollingerBand,
               double lowerBollingerBand)
    {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.upperBollingerBand = upperBollingerBand;
        this.middleBollingerBand = middleBollingerBand;
        this.lowerBollingerBand = lowerBollingerBand;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Bar bar = (Bar) o;

        if (Double.compare(bar.close, close) != 0)
        {
            return false;
        }
        if (Double.compare(bar.high, high) != 0)
        {
            return false;
        }
        if (Double.compare(bar.low, low) != 0)
        {
            return false;
        }
        if (Double.compare(bar.lowerBollingerBand, lowerBollingerBand) != 0)
        {
            return false;
        }
        if (Double.compare(bar.middleBollingerBand, middleBollingerBand) != 0)
        {
            return false;
        }
        if (Double.compare(bar.open, open) != 0)
        {
            return false;
        }
        if (Double.compare(bar.upperBollingerBand, upperBollingerBand) != 0)
        {
            return false;
        }
        if (volume != bar.volume)
        {
            return false;
        }
        if (EMAs != null ? !EMAs.equals(bar.EMAs) : bar.EMAs != null)
        {
            return false;
        }
        if (SMAs != null ? !SMAs.equals(bar.SMAs) : bar.SMAs != null)
        {
            return false;
        }
        if (date != null ? !date.equals(bar.date) : bar.date != null)
        {
            return false;
        }
        if (symbol != null ? !symbol.equals(bar.symbol) : bar.symbol != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        temp = Double.doubleToLongBits(open);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(high);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(low);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(close);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + volume;
        temp = Double.doubleToLongBits(upperBollingerBand);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(middleBollingerBand);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lowerBollingerBand);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (SMAs != null ? SMAs.hashCode() : 0);
        result = 31 * result + (EMAs != null ? EMAs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "\n\nBar{" +
                "\nsymbol='" + symbol + '\'' +
                ", \n\tdate=" + date +
                ", \n\topen=" + open +
                ", \n\thigh=" + high +
                ", \n\tlow=" + low +
                ", \n\tclose=" + close +
                ", \n\tvolume=" + volume +
                ", \n\tupperBollingerBand=" + upperBollingerBand +
                ", \n\tmiddleBollingerBand=" + middleBollingerBand +
                ", \n\tlowerBollingerBand=" + lowerBollingerBand +
                ", \n\tSMAs=" + SMAs +
                ", \n\tEMAs=" + EMAs +
                "\n}";
    }
}
