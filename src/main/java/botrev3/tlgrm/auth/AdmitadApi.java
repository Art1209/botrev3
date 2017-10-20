package botrev3.tlgrm.auth;

import botrev3.tlgrm.JsonRecoursiveParser;
import botrev3.tlgrm.common.ApiException;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.misc.BASE64Encoder;

import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by aalbutov on 19.10.2017.
 */
@Log4j
public class AdmitadApi {
    private String client_id = "cb281d918a37e346b45e9aea1c6eb7";
    private String client_secret = "cb281d918a37e346b45e9aea1c6eb7";
    private String vendor_id = "cb281d918a37e346b45e9aea1c6eb7";
    private String company_id = "cb281d918a37e346b45e9aea1c6eb7";
    private String sub_id = "cb281d918a37e346b45e9aea1c6eb7";

    private static final String GOOGLE_CLIENT_ID = "JI4NS58gf5xsKGXyWvx0GmPJ";
    private static final String GOOGLE_CLIENT_SECRET = "127773255865-m3eni29k2vp0bjdiv17n24ftg031ahnh.apps.googleusercontent.com";
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    private static final String GOOGLE_CERT_URL = "https://www.googleapis.com/oauth2/v1/certs";
    private static final String GOOGLE_PROJECT_ID = "my-project-1508485980423";
    private static final String GOOGLE_SHRINKER_URL =
            "https://www.googleapis.com/urlshortener/v1/url?key=AIzaSyDlD4Mjt4h4dir-RKQrIFMr2huDcTYgHnI";
    private static final String GOOGLE_SHRINKER_POST_BODY = "{\"longUrl\": \"%s\"}";
    private static final String TOKEN_REQUEST_ENTITY_STRING =
            "grant_type=client_credentials&client_id=%s&scope=deeplink_generator";
    private static final String TOKEN_LINK = "https://api.admitad.com/token/";
    private static final String DEEPLINK_LINK_URL =
            "https://api.admitad.com/deeplink/%s/advcampaign/%s/?subid=%s&ulp=%s";

    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private HttpClient client = HttpClientBuilder.create().build();


    String token;
    String tokenType;
    long expires;
    String refreshToken;

    String getToken(){
        while (client_secret==null||
                client_id==null||
                vendor_id==null||
                company_id==null||
                sub_id==null){
            retrieveCredentials();
            if (client_id==null)log.warn("Google sheets API return NULL");
        }
        while (token==null){
            retrieveToken();
        }
        if ((System.currentTimeMillis()-expires)>=0)retrieveToken();
        return token;
    }

    private void retrieveCredentials() {


    }

    void retrieveToken() {
        HttpResponse response;
        HttpPost post;
        String data = base64Encoder.encode((client_id + ":" + client_secret).getBytes());
        post = new HttpPost(TOKEN_LINK);
        post.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + data);
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        StringEntity body = null;
        try {
            body = new StringEntity(String.format(TOKEN_REQUEST_ENTITY_STRING,client_id));
            post.setEntity(body);
            InputStream in = executer(post);
            tokenType = parser.jsonFindByKey("token_type", in);
            if ("bearer".equals(tokenType))throw new UnsupportedOperationException();
            token = parser.jsonFindByKey("access_token", in);
            expires = System.currentTimeMillis()+((Long.parseLong(parser.jsonFindByKey("expires_in", in)))*1000);
            refreshToken = parser.jsonFindByKey("refresh_token", in);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    String changeLink(String dirtyLink){
        int errorCounter =0;
        String link=null;
        String preurl;
        String url=null;
        String admitedUrl=null;

        while ((link==null||url==null||admitedUrl==null)&&errorCounter<6){
            link = clearLink(dirtyLink);
            preurl = String.format(DEEPLINK_LINK_URL,vendor_id, company_id, sub_id,link);
            url = admitize(preurl);
            admitedUrl  = shrink(url);
            errorCounter++;
        }
        if (errorCounter==5)throw new ApiException("changeLink failed");
        return admitedUrl;
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
        InputStream in = executer(post);
        String res  = parser.jsonFindByKey("id", in);
        if (res==null)log.warn("Google shrinker API return NULL");
        return parser.jsonFindByKey("id", in);
    }

    private String admitize(String preurl) {
        HttpResponse response;
        HttpPost post;
    }

    private String clearLink(String dirtyLink) {
        HttpResponse response;
        HttpPost post;
    }

    private InputStream executer(HttpUriRequest request){
        try {
            return client.execute(request).getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

