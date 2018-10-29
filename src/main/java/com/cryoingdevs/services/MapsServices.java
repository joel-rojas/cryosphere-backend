package com.cryoingdevs.services;

import com.cryoingdevs.POJO.*;
import com.cryoingdevs.common.ColorUtils;
import com.cryoingdevs.POJO.Point;
import com.cryoingdevs.common.GlobalConstants;
import com.cryoingdevs.common.ResourcesUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Created by IvànAlejandro on 20/10/2018.
 */
@Path("/maps")
public class MapsServices {

    private ServletContext servletContext;

    public MapsServices(ServletContext servletContext){
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws IOException {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("Cryosphere backend", "The REST services are ready");
        String fullPath = servletContext.getRealPath("/WEB-INF/test/foo.txt");
        return Response.ok(message).build();
    }

    @POST
    @Path("/sendImage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void getImages(RestImage restImage) {
        String imageName = restImage.getImageName();
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("This", "is a test");
        message.put("This", "run");
    }

    @GET
    @Path("/calculatePercentages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImagePercentage() {

        Map<String, Object> message = new HashMap<String, Object>();
        Map<String, RestImagePercentages> mapResults = new HashMap<String, RestImagePercentages>();
        String imagesResourcesPathFolder = servletContext.getRealPath(ResourcesUtils.getImagesFolderPath());
        try {
            String[] years = {"2015", "2016", "2017", "2018"};
            for (int iter = 0; iter < years.length; iter++) {
                String year = years[iter];
                File file = new File(imagesResourcesPathFolder+"/" + year + ".png");
                BufferedImage image = null;
                image = ImageIO.read(file);
                int width = image.getWidth();
                int height = image.getHeight();
                long frozenWater = 0;
                long liquidWater = 0;
                long earth = 0;
                long others = 0;
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++) {

                        // Getting pixel color by position x and y
                        int clr = image.getRGB(i, j);
                        int red = (clr & 0x00ff0000) >> 16;
                        int green = (clr & 0x0000ff00) >> 8;
                        int blue = clr & 0x000000ff;

                        // Convert color from RGB to HSB
                        float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                        float hue = hsv[0];

                        if (ColorUtils.isCryogenicArea(red, green, blue)) {
                            frozenWater++;
                        } else if (ColorUtils.isLiquidWater(hue)) {
                            liquidWater++;
                        } else if (ColorUtils.isEarth(hue)) {
                            earth++;
                        } else {
                            others++;
                        }
                    }

                long total = frozenWater + liquidWater + earth;
                double frozenWaterPercent = (frozenWater / (double) total) * 100;
                BigDecimal icePercent = new BigDecimal(frozenWaterPercent);
                icePercent = icePercent.setScale(2, RoundingMode.HALF_UP);

                double liquidWaterPercent = (liquidWater / (double) total) * 100;
                BigDecimal waterPercent = new BigDecimal(liquidWaterPercent);
                waterPercent = waterPercent.setScale(2, RoundingMode.HALF_UP);

                double earthPercent = (earth / (double) total) * 100;
                BigDecimal earthPercentValue = new BigDecimal(earthPercent);
                earthPercentValue = earthPercentValue.setScale(2, RoundingMode.HALF_UP);

                // Create objects if not existent
                if (!mapResults.containsKey(GlobalConstants.CRYOSPHERE)) {
                    mapResults.put(GlobalConstants.CRYOSPHERE, new RestImagePercentages(GlobalConstants.CRYOSPHERE));
                }
                if (!mapResults.containsKey(GlobalConstants.EARTH)) {
                    mapResults.put(GlobalConstants.EARTH, new RestImagePercentages(GlobalConstants.EARTH));
                }
                if (!mapResults.containsKey(GlobalConstants.WATER)) {
                    mapResults.put(GlobalConstants.WATER, new RestImagePercentages(GlobalConstants.WATER));
                }

                // Add processed information
                List<RestYearInformation> icePercentages = mapResults.get(GlobalConstants.CRYOSPHERE).getSeries();
                icePercentages.add(new RestYearInformation(year, Math.round(icePercent.doubleValue())));

                List<RestYearInformation> earthPercentages = mapResults.get(GlobalConstants.EARTH).getSeries();
                earthPercentages.add(new RestYearInformation(year, Math.round(earthPercentValue.doubleValue())));

                List<RestYearInformation> waterPercentages = mapResults.get(GlobalConstants.WATER).getSeries();
                waterPercentages.add(new RestYearInformation(year, Math.round(waterPercent.doubleValue())));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<RestImagePercentages> listRestImagePercentages = new ArrayList<RestImagePercentages>();
        for (Map.Entry<String, RestImagePercentages> entry : mapResults.entrySet()) {
            listRestImagePercentages.add(entry.getValue());
        }
        message.put("data", listRestImagePercentages);

        return Response.ok(message).build();
    }

    @GET
    @Path("/saveImageToDisk")
    public void saveImageToDisk() {
        try {

            URL url = new URL("https://gibs.earthdata.nasa.gov/twms/epsg4326/best/twms.cgi?request=GetMap&layers=MODIS_Terra_CorrectedReflectance_TrueColor&srs=EPSG:4326&format=image/jpeg&styles=&time=2012-07-09&width=512&height=512&bbox=-18,27,-13.5,31.5");
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    @POST
    @Path("/getNearestCryosphere")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNearestCryosphere(RestMapPosition restMapPosition) {
        Map<String, Object> message = new HashMap<String, Object>();
        String encodedImage = restMapPosition.getImage().getEncodedImage();
        String nameSavedImage  = saveEncodedPngAsImage(encodedImage);
        double swY = restMapPosition.getBoundingBox()[0][0];
        double swX = restMapPosition.getBoundingBox()[0][1];
        double neY = restMapPosition.getBoundingBox()[1][0];
        double neX = restMapPosition.getBoundingBox()[1][1];
        double userLocY = restMapPosition.getUserLocation()[0];
        double userLocX = restMapPosition.getUserLocation()[1];

        System.out.println("Received Parameters");
        System.out.println("swY: "+swY+", swX: "+swX);
        System.out.println("neY: "+neY+", neX: "+neX);
        System.out.println("userLocY: "+userLocY+", userLockX: "+userLocX);

        double calculatedY = Math.abs(swY - neY);
        double calculatedX = Math.abs(swX - neX);

        double calculatedSourceY = Math.abs(swY - userLocY);
        double calculatedSourceX = Math.abs(swX - userLocX);

        Collection<Double> resultList = new ArrayList<Double>();

        String imagesResourcesPathFolder = servletContext.getRealPath(ResourcesUtils.getImagesFolderPath());
        File file = new File(imagesResourcesPathFolder+"/" + nameSavedImage + ".png");
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            System.out.println("Map dimensions: width: "+width+", height: "+height);
            double sourceYValue = (calculatedSourceY*height)/calculatedY;
            double sourceXValue = (calculatedSourceX*width)/calculatedX;
            int sourceY = (int) sourceYValue;
            int sourceX = (int) sourceXValue;

            RGBFormat[][] m = new RGBFormat[width][height];
            boolean[][] mb = new boolean[width][height];

            // Populate graph
            Point nearestPoint = new Point(sourceX, sourceY);
            nearestPoint.setDistanceToSource(Double.MAX_VALUE);
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++) {
                    int clr = image.getRGB(i, j);
                    int red = (clr & 0x00ff0000) >> 16;
                    int green = (clr & 0x0000ff00) >> 8;
                    int blue = clr & 0x000000ff;

                    // Convert color from RGB to HSB
                    float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                    float hue = hsv[0];
                    if (ColorUtils.isCryogenicArea(red, green,blue)) {
                        //Calculate minimum distance
                        double calcDistance = Math.sqrt(Math.pow(i-sourceX,2) + Math.pow(j-sourceY,2));
                        if(calcDistance < nearestPoint.getDistanceToSource()){
                            nearestPoint.setDistanceToSource(calcDistance);
                            nearestPoint.setX(i);
                            nearestPoint.setY(j);
                        }

                    }
                    RGBFormat rgbFormat = new RGBFormat(red, green, blue);
                    m[i][j] = rgbFormat;
                    mb[i][j] = false;
                }
            System.out.println("Cryo: "+nearestPoint.getX()+", "+nearestPoint.getY()+"; dist: "+nearestPoint.getDistanceToSource());

            if( nearestPoint.getX() != Double.MAX_VALUE && nearestPoint.getY() != Double.MAX_VALUE){

                double nearestX = nearestPoint.getX();
                double nearestY = nearestPoint.getY();

                //Convert to sent equivalent values
                double equivX = (nearestX * calculatedX) / width;
                double equivY = (nearestY * calculatedY) / height;

                equivY += swY;
                equivX += swX;

                resultList.add(equivY);
                resultList.add(equivX);
                System.out.println("Nearest point to the source is ["+equivY+","+equivX+"]");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        message.put("data", resultList);
        return Response.ok(message).build();
    }

    private String saveEncodedPngAsImage( String base64String){

        // tokenize the data
        String base64Image = base64String.split(",")[1];
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
        String nameSavedImage = "tempImage";

        try {

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            // write the image to a file
            String imagesResourcesPathFolder = servletContext.getRealPath(ResourcesUtils.getImagesFolderPath());
            File outputfile = new File(imagesResourcesPathFolder+"/"+nameSavedImage+".png");
            ImageIO.write(img, "png", outputfile);
            System.out.println("The image was successfully saved");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return nameSavedImage;
    }
}
