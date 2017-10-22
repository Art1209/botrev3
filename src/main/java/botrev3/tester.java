package botrev3;

import botrev3.AirTableApi;
import botrev3.domens.Action;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by aalbutov on 20.10.2017.
 */
public class tester {
    public static void main(String args[]) throws IOException, GeneralSecurityException {
        Action act = Action.getActionForId(null);
        act.setImage("dsfgsdg");
        act.setDescription("dsfgsdg");
        act.setLink("dsfgsdg");
        act.setTime("dsfgsdg");
        act.setPriceX100(12200);
        AirTableApi airTableApi = new AirTableApi();
        System.out.println(airTableApi.addAction(act));
    }
}
