package com.agentsmith.marketfun;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.agentsmith.marketfun.Calc.calcAngleOfLineBetweenTwoPoints;
import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Utility methods.
 * <p/>
 * User: rmarquez
 * Date: 12/15/2013
 * Time: 00:38
 */
public class Util
{
    public static boolean betweenInclusive(double lowerBound, double value, double upperBound)
    {
        return value >= lowerBound && value <= upperBound;
    }

    public static boolean isStable(TechnicalsFinderOptions options, List<Bar> bars)
    {
        double maxHigh = Integer.MIN_VALUE;
        double minLow = Integer.MAX_VALUE;

        int maxHighIdx = -1;
        int minLowIdx = -1;

        for (int i = 0; i < bars.size(); i++)
        {
            Bar nextBar = bars.get(i);

            double high = closedHigherThanOpen(nextBar) ? nextBar.close : nextBar.open;
            double low = closedHigherThanOpen(nextBar) ? nextBar.open : nextBar.close;

            if (high >= maxHigh)
            {
                maxHighIdx = i;
                maxHigh = high;
            }

            if (low <= minLow)
            {
                minLowIdx = i;
                minLow = low;
            }
        }

        if (maxHighIdx > minLowIdx)
        {
            int tmp = maxHighIdx;
            maxHighIdx = minLowIdx;
            minLowIdx = tmp;
        }

        // If the current price is over 10.0, and the price hasn't been moving more than 1.0,
        // then consider it an opportunity.
        if (maxHigh - minLow < 1.0 && bars.get(0).close > 10.0)
        {
            return true;
        }

        double highLowAngle = abs(calcAngleOfLineBetweenTwoPoints(new Point(maxHighIdx, maxHigh),
                                                                  new Point(minLowIdx, minLow)));

        if (options.debug)
        {
            System.out.println("[" + bars.get(0).symbol + "] highLowAngle = " + highLowAngle);
        }

        return highLowAngle <= options.maxPriceFluxAngle;
    }

    private static boolean closedHigherThanOpen(Bar nextBar)
    {
        return nextBar.close >= nextBar.open;
    }

    public static void waitForAllThreadsToComplete(CountDownLatch latch, ExecutorService executors)
    {
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException("Problem while waiting for threads to finish.", e);
        }
        finally
        {
            executors.shutdown();
        }
    }

    public static void emailOpportunitiesIfNec(TechnicalsFinderOptions options, Set<String> opportunities)
    {
        if (isNotBlank(options.emailAddress))
        {
            String toEmail = options.emailAddress;

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(
                    props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication()
                        {
                            return new PasswordAuthentication("tom.a.anderson.neo@gmail.com", "poker123");
                        }
                    });

            try
            {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("randy31415@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Stock Picks");
                message.setText("\nPossible opportunities: " + opportunities);

                outToUser(options, "Trying to send email to: " + toEmail + "...");

                Transport.send(message);

                outToUser(options, "Done sending email to: " + toEmail);

            }
            catch (MessagingException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void outToUser(TechnicalsFinderOptions options, String str)
    {
        if (options.debug)
        {
            System.out.println(str);
        }
    }

    public static void errToUser(TechnicalsFinderOptions options, String str)
    {
        if (options.debug)
        {
            System.err.println(str);
        }
    }
}
