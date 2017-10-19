package botrev3.tlgrm.auth;

import botrev3.tlgrm.JsonRecoursiveParser;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import sun.misc.BASE64Encoder;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by aalbutov on 19.10.2017.
 */
public class AdmitadApi {
    private String client_id = "cb281d918a37e346b45e9aea1c6eb7";
    private String client_secret = "cb281d918a37e346b45e9aea1c6eb7";
    private String vendor_id = "cb281d918a37e346b45e9aea1c6eb7";
    private String company_id = "cb281d918a37e346b45e9aea1c6eb7";

    private static final String TOKEN_REQUEST_ENTITY_STRING =
            "grant_type=client_credentials&client_id=%s&scope=deeplink_generator";
    private static final String TOKEN_LINK = "https://api.admitad.com/token/";
    private static final String DEEPLINK_LINK =
            "https://api.admitad.com/deeplink/{w_id}/advcampaign/{c_id}/";

    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private HttpClient client = HttpClientBuilder.create().build();
    HttpResponse response;
    HttpPost post;

    String token;
    String tokenType;
    long expires;
    String refreshToken;

    String getToken(){
        while (client_secret==null||client_id==null||vendor_id==null||company_id==null){
            retrieveCredentials();
        }
        while (token==null){
            retrieveToken();
        }
        if ((System.currentTimeMillis()-expires)>=0)retrieveToken();
        return token;
    }

    private void retrieveCredentials() {
        //todo excel parser
    }

    void retrieveToken() {
        String data = base64Encoder.encode((client_id + ":" + client_secret).getBytes());
        post = new HttpPost();
        post.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + data);
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        StringEntity body = null;
        try {
            body = new StringEntity(String.format(TOKEN_REQUEST_ENTITY_STRING,client_id));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(body);
        try {
            response = client.execute(post);
            InputStream in = response.getEntity().getContent();
            tokenType = parser.jsonFindByKey("token_type", in);
            if ("bearer".equals(tokenType))throw new UnsupportedOperationException();
            token = parser.jsonFindByKey("access_token", in);
            expires = System.currentTimeMillis()+((Long.parseLong(parser.jsonFindByKey("expires_in", in)))*1000);
            refreshToken = parser.jsonFindByKey("refresh_token", in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void changeLink(String dirtyLink){

    }

}

