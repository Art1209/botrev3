package botrev3;

import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import botrev3.domens.Category;
import botrev3.domens.Shop;
import botrev3.tlgrm.BlogBot;
import lombok.extern.log4j.Log4j;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import botrev3.common.ApiException;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by aalbutov on 19.10.2017.
 */
@Log4j
public class TextProcessor {

    private static final String UNSHRINK_API_URL = "https://unshorten.me/json/";
    private static final String GOOGLE_SHRINKER_URL =
            "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDlD4Mjt4h4dir-RKQrIFMr2huDcTYgHnI";
    private static final String GOOGLE_UNSHRINKER_URL =
            "https://www.googleapis.com/urlshortener/v1/url?shortUrl=%s&key=AIzaSyDlD4Mjt4h4dir-RKQrIFMr2huDcTYgHnI";
    private static final String GOOGLE_SHRINKER_POST_BODY = "{\"longUrl\": \"%s\"}";

    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private AdmitadApi admitadApi = new AdmitadApi();
//    private BlogBot bot;

    public String changeLink(String dirtyLink, int price ){
        if (dirtyLink == null) {
            log.warn("Empty dirty link, returned unchanged");
        }
        int attempts = 0;
        Shop shop;
        String longUrl=null;
        String cleanUrl=null;
        String admitedUrl=null;
        String res = null;

        while ((longUrl == null || cleanUrl == null || admitedUrl == null) && attempts < 6) {
            attempts++;

            longUrl = unShrink(dirtyLink);
            if ((shop = Shop.getShopForLink(longUrl))==null){
                log.warn("No shop found for link "+longUrl);
            }

            cleanUrl = clearLink(longUrl, shop);
            String vendor = shop.getVendor_id();
            String company = shop.getCompany_id();
            String subid = Category.getCategoryForPrice(price).getSub_id();
            if (cleanUrl == null || vendor == null || company == null || subid == null) {
                log.warn("NPE before admitize - null parameters");
            }

            admitedUrl = admitadApi.admitize(cleanUrl, vendor, company, subid);

            res  = shrink(admitedUrl);
        }
        if (attempts == 5) throw new ApiException("changeLink failed");
        log.info(res);
        return res;
    }

    private String unShrink(String url) {
        HttpResponse response;
        HttpGet get;
        get = new HttpGet(String.format(GOOGLE_UNSHRINKER_URL,url));
        InputStream in = HttpEx.execute(get);
        String res  = parser.jsonFindByKey("longUrl", in);
        if (res==null){
            HttpGet get2 = new HttpGet(UNSHRINK_API_URL+url);
            InputStream in2 = HttpEx.execute(get2);
            res = parser.jsonFindByKey("resolved_url", in2);
        }
        if (res==null){
            if (!url.contains("http"))url = "http://"+url;
            HttpHead head = new HttpHead(url);
            Header location = HttpEx.returnHeaderOrNull(head, "Location");
            if (location != null) res = location.getValue();
        }
        if (res==null){
            log.warn("Unshrinker return NULL");
            return url;}
        return res;
    }

    private String shrink(String url) {
        HttpResponse response;
        HttpPost post;
        post = new HttpPost(GOOGLE_SHRINKER_URL);
        post.addHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON);
        StringEntity body = null;
        try {
            body = new StringEntity(String.format(GOOGLE_SHRINKER_POST_BODY,url));
            post.setEntity(body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();}
        InputStream in = HttpEx.execute(post);
        String res  = parser.jsonFindByKey("id", in);
        if (res==null){
            log.warn("Google shrinker API return NULL");
            return url;
        }
        return res;
    }


    private String clearLink(String dirtyLink, Shop shop) {
        if (dirtyLink == null || shop == null) {
            log.warn("NPE from clealink - null parameters");
            return dirtyLink;
        }
        try {
            dirtyLink = URLDecoder.decode(dirtyLink, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (dirtyLink.contains("http")){
            dirtyLink = dirtyLink.substring(dirtyLink.lastIndexOf("http"));
        }
        if (!dirtyLink.contains(shop.getName())){
            log.warn("ClearLink failed with "+dirtyLink + " and shop " +shop.getName());
            return null;
        }
        if (dirtyLink.contains("html")){
            dirtyLink = dirtyLink.substring(0, dirtyLink.lastIndexOf("html")+4);
            log.info(dirtyLink);
        }
        return dirtyLink;
    }
}

