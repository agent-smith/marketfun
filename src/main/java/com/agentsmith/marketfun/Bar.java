package com.agentsmith.marketfun;

import java.util.Date;

/**
 * // TODO: impl me!
 * <p/>
 * User: rmarquez
 * Date: 11/24/13
 * Time: 5:35 PM
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
        return result;
    }

    @Override
    public String toString()
    {
        return "Bar{" +
                "symbol='" + symbol + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", upperBollingerBand=" + upperBollingerBand +
                ", middleBollingerBand=" + middleBollingerBand +
                ", lowerBollingerBand=" + lowerBollingerBand +
                '}';
    }
}
