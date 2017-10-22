package botrev3;

import botrev3.common.HttpEx;
import botrev3.common.JsonRecoursiveParser;
import botrev3.domens.Action;
import botrev3.domens.Category;
import botrev3.domens.Shop;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aalbutov on 20.10.2017.
 */
public class AirTableApi {
    public static final String API_KEY = "Bearer keyhnzkreYNlvMRjR";
    public static final String API_BASE_LINK = "https://api.airtable.com/v0/%s/Table1";
    public static final String ACTION_TABLE_ID = "appuZ1lbjCDuFTboP";
    public static final String CRED_TABLE_ID = "keyhnzkreYNlvMRjR";
    public static final String SHOP_TABLE_ID = "keyhnzkreYNlvMRjR";
    public static final String CATEGORY_TABLE_ID = "keyhnzkreYNlvMRjR";

    private static JsonRecoursiveParser parser = JsonRecoursiveParser.getParser();
    private static HttpClient client = HttpClientBuilder.create().build();


    public static String addAction(Action act) {
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
        try {
            System.out.println(obj.toJSONString());
            post.setEntity(new StringEntity(obj.toJSONString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        InputStream in = HttpEx.execute(post);
        String id = parser.jsonFindByKey("id",in);
        return id;
    }

    public static List<Action> getAllActions(){
        List<Action> res = new ArrayList<>();
        HttpResponse response;
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,ACTION_TABLE_ID)+"?filterByFormula=NOT({Link}=''"));
        InputStream in = HttpEx.execute(get);

        for (Object obj:parser.jsonParseArray("records",in)){
            JSONObject jsonObj = (JSONObject)obj;
            String id = (String) jsonObj.get("id");
            Action action = Action.getActionForId(id);
            JSONObject fields = (JSONObject)jsonObj.get("fields");
            String image = null;
            int price = 0;
            String link = null;
            String time = null;
            String description = null;
            action.setImage(image = (String)jsonObj.get("Image"));
            action.setPriceX100(price = 100*(int)(Double.parseDouble((String)jsonObj.get("Price"))));
            action.setLink(link = (String)jsonObj.get("Link"));
            action.setTime(time = (String)jsonObj.get("Time"));
            action.setDescription(description = (String)jsonObj.get("Description"));
            res.add(action);
        }
        return res;
    }

    public static List<Category> getAllCategories(){
        List<Category> res = new ArrayList<>();
        HttpResponse response;
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,CATEGORY_TABLE_ID)+"?filterByFormula=NOT({Name}=''"));
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
            category.setSub_id(sub_id = (String)jsonObj.get("Sub_id"));
            category.setName(name = (String)jsonObj.get("Name"));
            category.setDescription(description = (String)jsonObj.get("Description"));
            priceRanges = (String)jsonObj.get("Price ranges");
            if (priceRanges!=null)category.renewRange();
            res.add(category);
        }
        return res;
    }

    public static List<Shop> getAllShops(){
        List<Shop> res = new ArrayList<>();
        HttpResponse response;
        HttpGet get = auth(new HttpGet(String.format(API_BASE_LINK,SHOP_TABLE_ID)+"?filterByFormula=NOT({Name}=''"));
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
            shop.setName(name = (String)jsonObj.get("Name"));
            shop.setVendor_id(vendor = (String)jsonObj.get("Vendor id"));
            shop.setCompany_id(company = (String)jsonObj.get("Company id"));
            shop.setDescription(description = (String)jsonObj.get("Description"));
            res.add(shop);
        }
        return res;
    }

    private static <T extends HttpUriRequest> T auth(T request){
        request.addHeader(HttpHeaders.AUTHORIZATION, API_KEY);
        return request;
    }

}
