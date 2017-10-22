package botrev3.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

public class HttpEx {

    private static HttpClient client = HttpClientBuilder.create().build();

    public static  InputStream execute(HttpUriRequest request){
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

    public static HttpResponse rawExecute(HttpUriRequest request){
        try {
            Thread.sleep(350l);
            return client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}