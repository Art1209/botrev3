package botrev3;

import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import sun.misc.BASE64Encoder;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by aalbutov on 19.10.2017.
 */
@Log4j
public class AdmitadApi {
    // todo get from database via AirTableApi
    private String client_id = "1b6b511925fcd9c41a86518d666eea";
    private String client_secret = "a5768067980f95d0a0b7f647c2a5a1";
    // base64 = MWI2YjUxMTkyNWZjZDljNDFhODY1MThkNjY2ZWVhOmE1NzY4MDY3OTgwZjk1ZDBhMGI3ZjY0N2MyYTVhMQ==
    private String token;
    private long expires;
    private String refreshToken;

    private static final String TOKEN_REQUEST_ENTITY_STRING =
            "grant_type=client_credentials&client_id=%s&scope=deeplink_generator";
    private static final String TOKEN_LINK = "https://api.admitad.com/token/";
    private static final String DEEPLINK_LINK_URL =
            "https://api.admitad.com/deeplink/%s/advcampaign/%s/?subid=%s&ulp=%s";
    //  api.admitad.com/deeplink/vendor/advcampaign/company/?subid=&ulp=

    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();



    private String getToken(){
        while (client_secret==null||
                client_id==null){
            retrieveCredentials();
            if (client_id==null)log.warn("AirTable API return NULL");
        }
        while (token==null||(System.currentTimeMillis()-expires)>=0){
            retrieveToken();
        }
        return token;
    }

    private void retrieveCredentials() {


    }

    private void retrieveToken() {
        HttpPost post;
        String data = base64Encoder.encode((client_id + ":" + client_secret).getBytes());
        post = new HttpPost(TOKEN_LINK);
        post.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + data);
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        StringEntity body = null;
        try {
            body = new StringEntity(String.format(TOKEN_REQUEST_ENTITY_STRING,client_id));
            post.setEntity(body);
            InputStream in = HttpEx.execute(post);
            token = parser.jsonFindByKey("access_token", in);
            expires = System.currentTimeMillis()+((Long.parseLong(parser.jsonFindByKey("expires_in", in)))*1000);
            refreshToken = parser.jsonFindByKey("refresh_token", in);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String admitize(String pureUrl, String vendor_id, String company_id, String sub_id) {
        HttpResponse response;
        HttpPost post = new HttpPost(String.format(DEEPLINK_LINK_URL,vendor_id, company_id, sub_id, pureUrl));
        post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+getToken());
        String link = (String)parser.jsonParseArray(null, HttpEx.execute(post)).get(0);
        return link;
    }



}

