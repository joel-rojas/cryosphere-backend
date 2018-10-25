package com.cryoingdevs.services;

import com.cryoingdevs.POJO.*;
import com.cryoingdevs.common.ColorUtils;
import com.cryoingdevs.POJO.Point;
import com.cryoingdevs.common.GlobalConstants;
import com.cryoingdevs.common.ResourcesUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        Point nearestPoint = null;
        String country = restMapPosition.getImage().getImageName();//-33.71, -77.87     -22.08,-50.69    -29.61, -68.58
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
        File file = new File(imagesResourcesPathFolder+"/" + country + ".png");
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            double sourceYValue = (calculatedSourceY*height)/calculatedY;
            double sourceXValue = (calculatedSourceX*width)/calculatedX;
            int sourceY = (int) sourceYValue;
            int sourceX = (int) sourceXValue;

            RGBFormat[][] m = new RGBFormat[width][height];
            boolean[][] mb = new boolean[width][height];

            // Populate graph
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++) {
                    int clr = image.getRGB(i, j);
                    int red = (clr & 0x00ff0000) >> 16;
                    int green = (clr & 0x0000ff00) >> 8;
                    int blue = clr & 0x000000ff;

                    // Convert color from RGB to HSB
                    float[] hsv = Color.RGBtoHSB(red, green, blue, null);
                    float hue = hsv[0];
                    RGBFormat rgbFormat = new RGBFormat(red, green, blue);
                    m[i][j] = rgbFormat;
                    mb[i][j] = false;
                }

            // BFS
            Queue<Point> qv = new LinkedList<Point>();
            Point sourcePoint = new Point(sourceX, sourceY);
            qv.add(sourcePoint);

            int x, y;
            int nearestX = Integer.MIN_VALUE;
            int nearestY = Integer.MIN_VALUE;
            while (!qv.isEmpty()) {
                Point p = qv.poll();
                if (p != null) {
                    x = p.getX();
                    y = p.getY();
                    mb[x][y] = true; //visited
                    if (ColorUtils.isCryogenicArea(m[x][y].getRed(), m[x][y].getGreen(), m[x][y].getBlue())) {
                        nearestX = x;
                        nearestY = y;
                        break;
                    }
                    //Add neighbours to the queue
                    if (x > 0 && !mb[x - 10][y]) qv.add(new Point(x - 10, y));
                    if (x > 0 && y < height - 10 && !mb[x - 10][y + 10]) qv.add(new Point(x - 10, y + 10));
                    if (y < height - 10 && !mb[x][y + 10]) qv.add(new Point(x, y + 10));
                    if (x < width - 10 && y < height - 10 && !mb[x + 10][y + 10]) qv.add(new Point(x + 10, y + 10));
                    if (x < width - 10 && !mb[x + 10][y]) qv.add(new Point(x + 10, y));
                    if (x < width - 10 && y > 0 && !mb[x + 10][y - 10]) qv.add(new Point(x + 10, y - 10));
                    if (y > 0 && !mb[x][y - 10]) qv.add(new Point(x, y - 10));
                    if (x > 0 && y > 0 && !mb[x - 10][y - 10]) qv.add(new Point(x - 10, y - 10));
                }
            }
            qv.clear();

            double dnearX = (double) nearestX;
            double dnearY = (double) nearestY;

            double equivX = (dnearX * calculatedX) / width;
            double equivY = (dnearY * calculatedY) / height;

            equivY += swY;
            equivX += swX;

            resultList.add(equivY);
            resultList.add(equivX);
            System.out.println("Nearest point to the source is ("+equivY+","+equivX+")");

        } catch (IOException e) {
            e.printStackTrace();
        }
        message.put("data", resultList);
        return Response.ok(message).build();
    }
}
