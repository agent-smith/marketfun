package com.agentsmith.marketfun;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.agentsmith.marketfun.Util.outToUser;

/**
 * Adapted from http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
 */
public class SymbolFinder
{
    public static final String EODDATA_USERNAME = "agent.smith.more@gmail.com";
    public static final String EODDATA_PASSWORD = "killneo";
    public static final String EODDATA_URL = "http://www.eoddata.com/login.aspx";

    public static final String EODDATA_SYMBOL_LIST_URL_PREFIX = "http://www.eoddata.com/Data/symbollist.aspx?e=";

    private static final String USER_AGENT = "Mozilla/5.0";

    private static List<String> cookies;
    private static HttpURLConnection conn;

    public static Set<String> findSymbols(TechnicalsFinderOptions options)
    {
        Set<String> symbols;

        if (options.symbols.size() > 0)
        {
            symbols = new HashSet<>(options.symbols);
            if (symbols.size() > 0 && options.debug)
            {
                System.out.println("Analyzing '" + symbols + "'...");
            }
        }
        else
        {
            outToUser(options, "Downloading all symbols...");

            symbols = SymbolFinder.findAllSymbols(options.exchanges.toArray(new String[options.exchanges.size()]));

            outToUser(options, "Finished downloading all symbols.");
        }

        return symbols;
    }

    public static Set<String> findAllSymbols(String... exchanges)
    {
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        try
        {
            // 1. Send a "GET" request, so that you can extract the form's data.
            String page = GetPageContent1(EODDATA_URL);
            String postParams = getFormParams(page, EODDATA_USERNAME, EODDATA_PASSWORD);

            // 2. Construct above post's content and then send a POST request for
            // authentication
            sendPost(EODDATA_URL, postParams);

            // 3. success then go get the symbols for each exchange provided.
            Set<String> allSymbols = new TreeSet<>();
            for (String nextExchange : exchanges)
            {
                System.out.println("Getting all symbols for " + nextExchange + "...");
                allSymbols.addAll(getAllSymbols(EODDATA_SYMBOL_LIST_URL_PREFIX + nextExchange));
            }

            return allSymbols;
        }
        catch (Throwable t)
        {
            throw new RuntimeException("Could not get NASDAQ and NYSE symbols.", t);
        }
    }

    private static void sendPost(String url, String postParams) throws Exception
    {
        URL obj = new URL(url);
        conn = (HttpURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "www.eoddata.com");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null)
        {
            for (String cookie : cookies)
            {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "http://www.eoddata.com/login.aspx");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());

    }

    private static String GetPageContent1(String url) throws Exception
    {

        URL obj = new URL(url);

        conn = (HttpURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null)
        {
            for (String cookie : cookies)
            {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        cookies = conn.getHeaderFields().get("Set-Cookie");

        return response.toString();
    }

    private static Set<String> getAllSymbols(String url) throws Exception
    {
        URL obj = new URL(url);

        conn = (HttpURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null)
        {
            for (String cookie : cookies)
            {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        Set<String> allSymbols = new TreeSet<>();

        in.readLine(); // read header

        while ((inputLine = in.readLine()) != null)
        {
            String[] tokens = inputLine.split("\\t");
            String nextSymbol = tokens[0];
            allSymbols.add(nextSymbol);
        }
        in.close();

        // Get the response cookies
        cookies = conn.getHeaderFields().get("Set-Cookie");

        return allSymbols;
    }

    private static String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException
    {
        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementById("aspnetForm");
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<>();
        for (Element inputElement : inputElements)
        {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("ctl00$cph1$Login1$txtEmail"))
            {
                value = username;
            }
            else if (key.equals("ctl00$cph1$Login1$txtPassword"))
            {
                value = password;
            }
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList)
        {
            if (result.length() == 0)
            {
                result.append(param);
            }
            else
            {
                result.append("&").append(param);
            }
        }
        return result.toString();
    }
}