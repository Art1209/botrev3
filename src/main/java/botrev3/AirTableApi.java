package botrev3;

import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import botrev3.domens.Action;
import botrev3.domens.Category;
import botrev3.domens.Shop;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aalbutov on 20.10.2017.
 */
@Log4j
public class AirTableApi {
    public static final String API_KEY = "Bearer keyhnzkreYNlvMRjR";
    public static final String API_BASE_LINK = "https://api.airtable.com/v0/%s/Table1";
    public static final String ACTION_TABLE_ID = "appuZ1lbjCDuFTboP";
    public static final String CRED_TABLE_ID = "appR7A0JBsMp6UBwT";
    public static final String SHOP_TABLE_ID = "appQZmleBaXAK27A4";
    public static final String CATEGORY_TABLE_ID = "appJ3HPlhCQeDDQkN";
    private static String NOT_NULL_FORMULA = "?filterByFormula=";

    private static AirTableApi api = new AirTableApi();

    public static AirTableApi getApi(){
        return api;
    }
    private AirTableApi() {
    }

    private static JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private static HttpClient client = HttpClientBuilder.create().build();

    public void init() {
        Action.actions = api.getAllActions();
        Category.categories = api.getAllCategories();
        Shop.shops = api.getAllShops();
    }

    public String addAction(Action act) {
        HttpPost post = auth(new HttpPost(String.format(API_BASE_LINK, ACTION_TABLE_ID)));
        post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        JSONObject obj = new JSONObject();
        JSONObject fields = new JSONObject();
        fields.put("Link", act.getLink());
        fields.put("Price", act.getPriceAsString());
        fields.put("Description", act.getDescription());
        fields.put("Image", act.getImage());
        fields.put("Time", act.getTime());
        obj.put("fields", fields);
        post.setEntity(new StringEntity(obj.toJSONString(), ContentType.APPLICATION_JSON ));
        InputStream in = HttpEx.execute(post);
        String id = parser.jsonFindByKey("id",in);
        log.info("Added new action "+id);
        return id;
    }

    public List<Action> getAllActions(){
        List<Action> res = new ArrayList<>();
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,ACTION_TABLE_ID)+NOT_NULL_FORMULA));
        InputStream in = HttpEx.execute(get);

        List<Object> objs = parser.jsonParseArray("records",in);
        for (Object obj:objs){
            JSONObject jsonObj = (JSONObject)obj;
            String id = (String) jsonObj.get("id");
            Action action = Action.getActionForId(id);
            JSONObject fields = (JSONObject)jsonObj.get("fields");
            String image = null;
            String price = null;
            String link = null;
            String time = null;
            String description = null;
            action.setTime(time = (String)fields.get("Time"));
            Date parsedDate = action.getTimeAsDate();
            if (parsedDate != null && ((new Date()).getTime() - parsedDate.getTime()) > 60000) {
                deleteAction(action);
                continue; // check for valid time at every update
            }
            action.setImage(image = (String)fields.get("Image"));
            action.setPriceAsString((String)fields.get("Price"));
            action.setLink(link = (String)fields.get("Link"));
            action.setDescription(description = (String)fields.get("Description"));
            res.add(action);
        }
        log.info("Got actions "+res.size());
        return res;
    }

    public List<Category> getAllCategories(){
        List<Category> res = new ArrayList<>();
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,CATEGORY_TABLE_ID)+NOT_NULL_FORMULA));
        InputStream in = HttpEx.execute(get);


        for (Object obj:parser.jsonParseArray("records",in)){
            JSONObject jsonObj = (JSONObject)obj;
            String id = (String) jsonObj.get("id");
            Category category = Category.getCategoryForId(id);
            JSONObject fields = (JSONObject)jsonObj.get("fields");
            String sub_id = null;
            String priceRanges = null;
            String name = null;
            String description = null;
            category.setSub_id(sub_id = (String)fields.get("Sub_id"));
            category.setName(name = (String)fields.get("Name"));
            category.setDescription(description = (String)fields.get("Description"));
            category.setRange(priceRanges = (String)fields.get("Price ranges"));
            if (priceRanges!=null)category.renewRange();
            res.add(category);
        }
        log.info("Got categories "+res.size());
        return res;
    }

    public List<Shop> getAllShops(){
        List<Shop> res = new ArrayList<>();
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,SHOP_TABLE_ID)+NOT_NULL_FORMULA));
        InputStream in = HttpEx.execute(get);

        for (Object obj:parser.jsonParseArray("records",in)){
            JSONObject jsonObj = (JSONObject)obj;
            String id = (String) jsonObj.get("id");
            Shop shop = Shop.getShopForId(id);
            JSONObject fields = (JSONObject)jsonObj.get("fields");
            String name = null;
            String vendor = null;
            String company = null;
            String description = null;
            shop.setName(name = (String)fields.get("Name"));
            shop.setVendor_id(vendor = (String)fields.get("Vendor id"));
            shop.setCompany_id(company = (String)fields.get("Company id"));
            shop.setDescription(description = (String)fields.get("Description"));
            res.add(shop);
        }
        log.info("Got shops "+res.size());
        return res;
    }
    public String[] getCredentials(){
        String [] res = new String[2];
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,CRED_TABLE_ID)+NOT_NULL_FORMULA));
        InputStream in = HttpEx.execute(get);

        for (Object obj:parser.jsonParseArray("records",in)){
            JSONObject jsonObj = (JSONObject)obj;
            JSONObject fields = (JSONObject)jsonObj.get("fields");
            String id = null;
            String secret = null;
            if (fields.containsKey("Client id")){
                id = (String)fields.get("Client id");
                secret = (String)fields.get("Client secret");
                res[0]=id;
                res[1]=secret;
                return res;
            }

        }
        log.warn("Empty credentials returned");
        return res;
    }

    public void deleteAction(Action action){
        HttpDelete delete = auth(new HttpDelete(String.format(API_BASE_LINK, ACTION_TABLE_ID)+"/"+action.getId()));
        int status = HttpEx.returnStatus(delete);
        if (status!=200){
            log.warn("Delete action return "+status);
        }

    }

    private <T extends HttpUriRequest> T auth(T request){
        request.addHeader(HttpHeaders.AUTHORIZATION, API_KEY);
        return request;
    }

}
