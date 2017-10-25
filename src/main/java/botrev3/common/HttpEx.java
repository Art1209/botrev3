package botrev3.common;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

public class HttpEx {

    private static CloseableHttpClient client = HttpClientBuilder.create()
            .disableCookieManagement()
            .disableAuthCaching()
            .disableAutomaticRetries()
            .disableConnectionState()
            .disableRedirectHandling()
            .build();

    public static  InputStream execute(HttpUriRequest request){
        CloseableHttpResponse resp = null;
        try {
            Thread.sleep(350l);
            resp = client.execute(request);
            return resp.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        finally {
//            if (resp!=null){
//                try {
//                    resp.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return null;
    }

    public static int returnStatus(HttpUriRequest request) {
        CloseableHttpResponse resp = null;
        try {
            Thread.sleep(350l);
            resp =  client.execute(request);
            return resp.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return HttpStatus.SC_EXPECTATION_FAILED;
    }

    public static Header returnHeaderOrNull(HttpUriRequest request, String headerName) {
        CloseableHttpResponse resp = null;
        try {
            Thread.sleep(350l);
            resp = client.execute(request);
            return resp.getFirstHeader(headerName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        finally {
//            if (resp!=null){
//                try {
//                    resp.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return null;
    }
}
