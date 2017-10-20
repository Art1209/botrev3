package botrev3;

import botrev3.common.JsonRecoursiveParser;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aalbutov on 20.10.2017.
 */
public class AirTableApi {
    public static final String API_KEY = "Bearer keyhnzkreYNlvMRjR";
    public static final String API_BASE_LINK = "https://api.airtable.com/v0/%s/Table%201";
    public static final String ACTION_TABLE_ID = "keyhnzkreYNlvMRjR";
    public static final String CRED_TABLE_ID = "keyhnzkreYNlvMRjR";
    public static final String SHOP_TABLE_ID = "keyhnzkreYNlvMRjR";
    public static final String CATEGORY_TABLE_ID = "keyhnzkreYNlvMRjR";

            "\"https://api.airtable.com/v0/appuZ1lbjCDuFTboP/tab1?maxRecords=3&view=Grid%20view\" \\\n" +
            "-H \"Authorization: Bearer keyhnzkreYNlvMRjR\"";
    public static final String API_DOWNLOAD_IMG_LINK ="https://api.airtable.com/v0/appuZ1lbjCDuFTboP/tab1/rec9KIbF8BitH1y4S n";
    public static final String API_IMG_PATH ="https://api.airtable.com/v0/appuZ1lbjCDuFTboP/tab1/rec9KIbF8BitH1y4S \\\n" +
            "-H \"Authorization: Bearer YOUR_API_KEY\"";
    public static final String API_OCR_PATH ="POST https://api.airtable.com/v0/appuZ1lbjCDuFTboP/tab1 \\\n" +
            "-H \"Authorization: Bearer keyhnzkreYNlvMRjR\" \\\n" +
            "-H \"Content-type: application/json\" \\\n" +
            " -d '{\n" +
            "  \"fields\": {\n" +
            "    \"Link\": \"sdaf.rrt\",\n" +
            "    \"Price\": 1020,\n" +
            "    \"Descrition\": \"sdafdsf\",\n" +
            "    \"Image\": \"sdaf.rrt\",\n" +
            "    \"Time\": \"sdafads\"\n" +
            "  }\n" +
            "}'";
"https://api.airtable.com/v0/appQZmleBaXAK27A4/Table%201?maxRecords=3&view=Grid%20view" \
        -H "Authorization: Bearer YOUR_API_KEY"

    private JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private HttpClient client = HttpClientBuilder.create().build();

    public static List<String> getAllShops(){
        HttpResponse response;
        HttpPost post = auth(new HttpPost(String.format(API_BASE_LINK,SHOP_TABLE_ID)+"?filterByFormula=NOT({Name}=''"));

        List<String> res = new ArrayList<>();


        return res;
    }

    private synchronized InputStream executer(HttpUriRequest request){
        try {
            Thread.sleep(350l);
            return client.execute(request).getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static <T extends HttpUriRequest> T auth(T request){
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+API_KEY);
        return request;
    }

}
