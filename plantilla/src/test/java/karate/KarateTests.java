package karate;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import org.junit.runner.RunWith;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

@RunWith(Karate.class)
@KarateOptions(tags = {"~@ignore"})
public class KarateTests
{
    public static String selectHtml(String html, String cssSelector) {
        final Document document = Jsoup.parse(html);
        String result = document.select(cssSelector).html();
        return result.isEmpty() ? "No Matches for '" + cssSelector + "' in '" + html + "'" : result;
    }

    public static String selectAttribute(String html, String cssSelector, String attributeKey) {
        final Document document = Jsoup.parse(html);
        return document.select(cssSelector).attr(attributeKey);
    }
}
