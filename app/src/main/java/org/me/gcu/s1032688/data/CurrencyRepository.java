package org.me.gcu.s1032688.data;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import org.me.gcu.s1032688.model.CurrencyItem;

/**
 * Repository responsible for downloading the RSS feed and parsing it into a model.
 */
public class CurrencyRepository {

    public static final String FEED_URL = "https://www.fx-exchange.com/gbp/rss.xml";

    public static class ParseResult {
        public final ArrayList<CurrencyItem> items;
        public final String lastBuildDate;
        public ParseResult(ArrayList<CurrencyItem> items, String lastBuildDate) {
            this.items = items;
            this.lastBuildDate = lastBuildDate;
        }
    }

    /** Network esuring and parsing download the RSS into a UTF-8 string. */
    public String fetchFeedString() throws Exception {
        HttpURLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(FEED_URL);
            conn = (HttpURLConnection) url.openConnection();
            // Pretend to be a real browser and ask specifically for XML/RSS
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36");
            conn.setRequestProperty("Accept", "application/rss+xml, application/xml;q=0.9, */*;q=0.8");
            conn.setRequestProperty("Referer", "https://www.fx-exchange.com/");
            conn.setRequestProperty("Accept-Encoding", "identity"); // avoid gzip surprises

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IllegalStateException("HTTP " + code);
            }

            in = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32768);
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            String body = sb.toString().trim();

            // Quick sanity check: ensure we actually got RSS/XML, not an HTML/JS challenge page
            if (!isLikelyRss(body)) {
                String snippet = body.length() > 300 ? body.substring(0, 300) + "..." : body;
                throw new IllegalStateException("Server returned non-RSS content. First bytes:\n" + snippet);
            }
            return body;
        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    /** Quick sanity check: ensure that i actually got RSS/XML, not an HTML/JS challenge page. */
    private boolean isLikelyRss(String s) {
        String t = s.trim().toLowerCase();
        // allow optional XML prolog, then <rss ...> or <feed ...>
        return t.startsWith("<?xml") || t.contains("<rss") || t.contains("<feed");
    }

    /** Parse an RSS XML string using an XmlPullParser into model items. */
    public ParseResult parseRssString(String xml) throws Exception {
        xml = sanitizeXml(xml);
        ArrayList<CurrencyItem> items = new ArrayList<>();
        String lastBuildDate = null;

        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        f.setNamespaceAware(true);
        XmlPullParser xpp = f.newPullParser();
        xpp.setInput(new java.io.StringReader(xml));

        int eventType = xpp.getEventType();
        String tTitle = null, tLink = null, tPubDate = null, tDesc = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = xpp.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if ("lastBuildDate".equalsIgnoreCase(tag)) {
                    lastBuildDate = nextTextTrim(xpp);
                } else if ("item".equalsIgnoreCase(tag)) {
                    tTitle = tLink = tPubDate = tDesc = null;
                } else if ("title".equalsIgnoreCase(tag)) {
                    tTitle = nextTextTrim(xpp);
                } else if ("link".equalsIgnoreCase(tag)) {
                    tLink = nextTextTrim(xpp);
                } else if ("pubDate".equalsIgnoreCase(tag)) {
                    tPubDate = nextTextTrim(xpp);
                } else if ("description".equalsIgnoreCase(tag)) {
                    tDesc = nextTextTrim(xpp);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("item".equalsIgnoreCase(tag)) {
                    if (tTitle != null && tDesc != null) {
                        CurrencyItem ci = mapFieldsToCurrencyItem(tTitle, tDesc, tLink, tPubDate);
                        if (ci != null) items.add(ci);
                    }
                    tTitle = tLink = tPubDate = tDesc = null;
                }
            }
            eventType = xpp.next();
        }
        return new ParseResult(items, lastBuildDate);
    }

    private String nextTextTrim(XmlPullParser xpp) throws Exception {
        String t = xpp.nextText();
        return t == null ? "" : t.trim();
    }

    /** Map one <item> set of fields from the RSS into a CurrencyItem. */
    private CurrencyItem mapFieldsToCurrencyItem(String title, String description, String link, String pubDate) {
        try {
            // e.g. title: "British Pound Sterling(GBP)/United States Dollar(USD)"
            String[] parts = title.split("/");
            if (parts.length != 2) return null;
            String rhs = parts[1].trim();
            String code = null;
            String name = rhs;
            int open = rhs.lastIndexOf('(');
            int close = rhs.lastIndexOf(')');
            if (open >= 0 && close > open) {
                code = rhs.substring(open + 1, close).trim();
                name = rhs.substring(0, open).trim();
            }
            if (code == null || code.length() != 3) return null;

            // desc: "1 British Pound Sterling = 1.2745 United States Dollar"
            double rate = extractRate(description);

            CurrencyItem ci = new CurrencyItem();
            ci.code = code.toUpperCase(Locale.ROOT);
            ci.displayName = name;
            ci.rate = rate;
            ci.link = link;
            ci.pubDate = pubDate;
            return ci;
        } catch (Exception e) {
            return null;
        }
    }

    private double extractRate(String desc) {
        int eq = desc.indexOf('=');
        if (eq >= 0) {
            String tail = desc.substring(eq + 1).trim();
            String[] toks = tail.split("\\s+");
            for (String t : toks) {
                try { return Double.parseDouble(t.replace(",", "")); }
                catch (NumberFormatException ignored) {}
            }
        }
        return Double.NaN;
    }

    /** Replace malformed entities so PullParser can read the feed safely. */
    private String sanitizeXml(String s) {
        if (s == null) return "";
        // 1) Normalise common HTML entity to XML-safe form
        s = s.replaceAll("(?i)&nbsp;?", " "); // &nbsp or &nbsp; -> space

        // 2) Escape ANY bare '&' that is not a valid XML entity:
        //    allowed: &amp; &lt; &gt; &quot; &apos; &#123; &#x1F4A9;
        s = s.replaceAll("&(?!amp;|lt;|gt;|quot;|apos;|#\\d+;|#x[0-9a-fA-F]+;)", "&amp;");

        // 3) Remove illegal control characters (keep \n\r\t)
        s = s.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");

        return s;
    }

    /** Optional: quick peek to help debugging if needed later. */
    private String head(String s) {
        return s == null ? "" : (s.length() > 300 ? s.substring(0, 300) + "..." : s);
    }
}
