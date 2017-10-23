package botrev3;

import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;
import java.util.Base64;

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

    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    AirTableApi airTableApi = AirTableApi.getApi();



    private String getToken(){
        int errorcounter1 = 0;
        int errorcounter2 = 0;
        while (client_secret==null||client_id==null){
            retrieveCredentials();
            if (client_id==null){
                errorcounter1++;
            }
            if (errorcounter1>3){
                log.warn("AirTable API return NULL");
                return null;
            }
        }
        while (token==null||(System.currentTimeMillis()-expires)>=0){
            retrieveToken();
            if (token==null){
                errorcounter2++;
            }
            if (errorcounter2>3){
                log.warn("Admitad API return NULL TOKEN");
                return null;
            }
        }
        return token;
    }

    private void retrieveCredentials() {
        String[] creds = airTableApi.getCredentials();
        client_id = creds[0];
        client_secret = creds[1];
    }

    private void retrieveToken() {
        HttpPost post;
        String data = Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes());
        post = new HttpPost(TOKEN_LINK);
        post.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + data);
//                "MWI2YjUxMTkyNWZjZDljNDFhODY1MThkNjY2ZWVhOmE1NzY4MDY3OTgwZjk1ZDBhMGI3ZjY0N2MyYTVhMQ==");
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        StringEntity body = null;
        try {
            body = new StringEntity(String.format(TOKEN_REQUEST_ENTITY_STRING,client_id));
            post.setEntity(body);
            InputStream in = HttpEx.execute(post);
            JSONObject obj = parser.jsonGetRoot(in);
            if (obj.containsKey("access_token")){
                token = (String) obj.get("access_token");
                expires = System.currentTimeMillis()+(((Long) obj.get("expires_in"))*1000);
                refreshToken = (String) obj.get("refresh_token");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String admitize(String pureUrl, String vendor_id, String company_id, String sub_id) {
        HttpGet get = new HttpGet(String.format(DEEPLINK_LINK_URL,vendor_id, company_id, sub_id, pureUrl));
        get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+getToken());
        String link = (String)parser.jsonParseArray(null, HttpEx.execute(get)).get(0);
        return link;
    }



}

