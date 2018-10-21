package test.java;

import com.cryoingdevs.common.ImageUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class ImageTest {
    @Test
    public void testDecodingBase64() {
        ImageUtils.decodeImage64base();
    }

    @Test
    public void connectServer() {
//        String url = "https://gibs.earthdata.nasa.gov/wmts/epsg4326/best/MODIS_Terra_CorrectedReflectance_TrueColor/default/2012-07-09/250m/6/13/36.jpg";
        String url = "http://localhost:8080/cryosphere/rest/maps/test";

        try {

            ClientRequest request = new ClientRequest(url);
            request.accept("application/json");

            ClientResponse<String> response = request.get(String.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(response.getEntity().getBytes())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {

                System.out.println(output);
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}
