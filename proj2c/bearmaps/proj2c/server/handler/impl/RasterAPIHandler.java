package bearmaps.proj2c.server.handler.impl;

import bearmaps.proj2c.AugmentedStreetMapGraph;
import bearmaps.proj2c.server.handler.APIRouteHandler;
import spark.Request;
import spark.Response;
import bearmaps.proj2c.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bearmaps.proj2c.utils.Constants.*;

/**
 * Handles requests from the web browser for map images. These images
 * will be rastered into one large image to be displayed to the user.
 * @author rahul, Josh Hug, Sean Li
 */
public class RasterAPIHandler extends APIRouteHandler<Map<String, Double>, Map<String, Object>> {

    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside RasterAPIHandler.processRequest(). <br>
     * ullat : upper left corner latitude, <br> ullon : upper left corner longitude, <br>
     * lrlat : lower right corner latitude,<br> lrlon : lower right corner longitude <br>
     * w : user viewport window width in pixels,<br> h : user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
            "lrlon", "w", "h"};


    /**
     * The result of rastering must be a map containing all of the
     * fields listed in the comments for RasterAPIHandler.processRequest.
     **/
    private static final String[] REQUIRED_RASTER_RESULT_PARAMS = {"render_grid", "raster_ul_lon",
            "raster_ul_lat", "raster_lr_lon", "raster_lr_lat", "depth", "query_success"};


    @Override
    protected Map<String, Double> parseRequestParams(Request request) {
        return getRequestParams(request, REQUIRED_RASTER_REQUEST_PARAMS);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param requestParams Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @param response : Not used by this function. You may ignore.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image;
     *                    can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    @Override
    public Map<String, Object> processRequest(Map<String, Double> requestParams, Response response) {
        //System.out.println("requestParams: " + requestParams);
        boolean query_success = true;
        Map<String, Object> results = new HashMap<>();
        double lrlon = requestParams.get("lrlon");
        double ullon = requestParams.get("ullon");
        double w = requestParams.get("w");
        double h = requestParams.get("h");
        double ullat = requestParams.get("ullat");
        double lrlat = requestParams.get("lrlat");
        double LongDPP = (lrlon - ullon) / w;
        double level0dpp = 0.00034332275390625;
        double level1dpp = 0.000171661376953125;
        double level2dpp = 0.0000858306884765625;
        double level3dpp = 0.00004291534423828125;
        double level4dpp = 0.000021457672119140625;
        double level5dpp = 0.000010728836059570312;
        double level6dpp = 0.000005364418029785156;
        double level7dpp = 0.000002682209014892578;
        //System.out.println(LongDPP);
        int level = 7;
        if(level0dpp <= LongDPP) {
            level = 0;
        } else if (level1dpp <= LongDPP) {
            level = 1;
        } else if (level2dpp <= LongDPP) {
            level = 2;
        } else if (level3dpp <= LongDPP) {
            level = 3;
        } else if (level4dpp <= LongDPP) {
            level = 4;
        } else if (level5dpp <= LongDPP) {
            level = 5;
        } else if (level6dpp <= LongDPP) {
            level = 6;
        } else if (level7dpp <= LongDPP) {
            level = 7;
        }
        //System.out.println(level);

        double mapWidth = ROOT_LRLON - ROOT_ULLON;
        double mapHeight = ROOT_ULLAT - ROOT_LRLAT;
        double gridWidth = mapWidth / Math.pow(2, level);
        double gridHeight = mapHeight / Math.pow(2, level);

        if (ullon > lrlon || lrlat > ullat || lrlon <= ROOT_ULLON || ullon >= ROOT_LRLON ||
                lrlat >= ROOT_ULLAT || ullat <= ROOT_LRLAT) {
            query_success = false;
        }
        if (ullat > ROOT_ULLAT) {
            ullat = ROOT_ULLAT;
        }
        if (ullon < ROOT_ULLON) {
            ullon = ROOT_ULLON;
        }
        if (lrlat < ROOT_LRLAT) {
            lrlat = ROOT_LRLAT;
        }
        if (lrlon > ROOT_LRLON) {
            lrlon = ROOT_LRLON;
        }

        int upperleftX = (int) ((ullon - ROOT_ULLON) / gridWidth);
        int upperleftY = (int) ((ROOT_ULLAT - ullat) / gridHeight);
        int lowerrightX = (int) ((lrlon - ROOT_ULLON) / gridWidth);
        int lowerrightY = (int) ((ROOT_ULLAT - lrlat) / gridHeight);

        String[][] filenames = new String[lowerrightY - upperleftY + 1][lowerrightX - upperleftX + 1];
        //System.out.println((lowerrightX - upperleftX + 1) + " grids " +  (lowerrightY - upperleftY + 1));
        for (int i = upperleftY; i <= lowerrightY; i++) {
            for (int j = upperleftX; j <= lowerrightX; j++) {
                filenames[i - upperleftY][j - upperleftX] = "d" + level + "_x" + j + "_y" + i + ".png";
                //System.out.println((j - upperleftX) + " " +  (i - upperleftY));
            }
        }

        results.put("depth", level);
        //System.out.println("depth: " +  level);
        results.put("query_success", query_success);
        //System.out.println("query_success: " + query_success);
        results.put("render_grid", filenames);
        //System.out.println("render_grid: " +  filenames);
        results.put("raster_ul_lon", ROOT_ULLON + upperleftX * gridWidth);
        //System.out.println("raster_ul_lon: "+ (ROOT_ULLON + upperleftX * gridWidth));
        results.put("raster_ul_lat", ROOT_ULLAT - upperleftY * gridHeight);
        //System.out.println("raster_ul_lat: "+ (ROOT_ULLAT - upperleftY * gridHeight));
        results.put("raster_lr_lon", ROOT_ULLON + (lowerrightX + 1) * gridWidth);
        //System.out.println("raster_lr_lon: "+ (ROOT_ULLON + (lowerrightX + 1) * gridWidth));
        results.put("raster_lr_lat", (ROOT_ULLAT - (lowerrightY + 1) * gridHeight));
        //System.out.println("raster_lr_lat: " + (ROOT_ULLAT - (lowerrightY + 1) * gridHeight));

        /*
        parameters in requestParams: lrlon ullon w(width) h(height) ullat lrlat
        String[][] grids = new String[3][3];
        grids[0][0] = "d7_x84_y28.png";
        grids[0][1] = "d7_x85_y28.png";
        grids[0][2] = "d7_x86_y28.png";
        grids[1][0] = "d7_x84_y29.png";
        grids[1][1] = "d7_x85_y29.png";
        grids[1][2] = "d7_x86_y29.png";
        grids[2][0] = "d7_x84_y30.png";
        grids[2][1] = "d7_x85_y30.png";
        grids[2][2] = "d7_x86_y30.png";
        results.put("render_grid", grids);
        results.put("raster_ul_lon", -122.24212646484375);
        results.put("raster_ul_lat", 37.87701580361881);
        results.put("raster_lr_lon", -122.24006652832031);
        results.put("raster_lr_lat", 37.87538940251607);
        results.put("depth", 7);
        results.put("query_success", true);
        */
        return results;
    }

    @Override
    protected Object buildJsonResponse(Map<String, Object> result) {
        boolean rasterSuccess = validateRasteredImgParams(result);

        if (rasterSuccess) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writeImagesToOutputStream(result, os);
            String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
            result.put("b64_encoded_image_data", encodedImage);
        }
        return super.buildJsonResponse(result);
    }

    private Map<String, Object> queryFail() {
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", null);
        results.put("raster_ul_lon", 0);
        results.put("raster_ul_lat", 0);
        results.put("raster_lr_lon", 0);
        results.put("raster_lr_lat", 0);
        results.put("depth", 0);
        results.put("query_success", false);
        return results;
    }

    /**
     * Validates that Rasterer has returned a result that can be rendered.
     * @param rip : Parameters provided by the rasterer
     */
    private boolean validateRasteredImgParams(Map<String, Object> rip) {
        for (String p : REQUIRED_RASTER_RESULT_PARAMS) {
            if (!rip.containsKey(p)) {
                System.out.println("Your rastering result is missing the " + p + " field.");
                return false;
            }
        }
        if (rip.containsKey("query_success")) {
            boolean success = (boolean) rip.get("query_success");
            if (!success) {
                System.out.println("query_success was reported as a failure");
                return false;
            }
        }
        return true;
    }

    /**
     * Writes the images corresponding to rasteredImgParams to the output stream.
     * In Spring 2016, students had to do this on their own, but in 2017,
     * we made this into provided code since it was just a bit too low level.
     */
    private  void writeImagesToOutputStream(Map<String, Object> rasteredImageParams,
                                                  ByteArrayOutputStream os) {
        String[][] renderGrid = (String[][]) rasteredImageParams.get("render_grid");
        int numVertTiles = renderGrid.length;
        int numHorizTiles = renderGrid[0].length;

        BufferedImage img = new BufferedImage(numHorizTiles * Constants.TILE_SIZE,
                numVertTiles * Constants.TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphic = img.getGraphics();
        int x = 0, y = 0;

        for (int r = 0; r < numVertTiles; r += 1) {
            for (int c = 0; c < numHorizTiles; c += 1) {
                graphic.drawImage(getImage(Constants.IMG_ROOT + renderGrid[r][c]), x, y, null);
                x += Constants.TILE_SIZE;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += Constants.TILE_SIZE;
                }
            }
        }

        /* If there is a route, draw it. */
        double ullon = (double) rasteredImageParams.get("raster_ul_lon"); //tiles.get(0).ulp;
        double ullat = (double) rasteredImageParams.get("raster_ul_lat"); //tiles.get(0).ulp;
        double lrlon = (double) rasteredImageParams.get("raster_lr_lon"); //tiles.get(0).ulp;
        double lrlat = (double) rasteredImageParams.get("raster_lr_lat"); //tiles.get(0).ulp;

        final double wdpp = (lrlon - ullon) / img.getWidth();
        final double hdpp = (ullat - lrlat) / img.getHeight();
        AugmentedStreetMapGraph graph = SEMANTIC_STREET_GRAPH;
        List<Long> route = ROUTE_LIST;

        if (route != null && !route.isEmpty()) {
            Graphics2D g2d = (Graphics2D) graphic;
            g2d.setColor(Constants.ROUTE_STROKE_COLOR);
            g2d.setStroke(new BasicStroke(Constants.ROUTE_STROKE_WIDTH_PX,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            route.stream().reduce((v, w) -> {
                g2d.drawLine((int) ((graph.lon(v) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(v)) * (1 / hdpp)),
                        (int) ((graph.lon(w) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(w)) * (1 / hdpp)));
                return w;
            });
        }

        rasteredImageParams.put("raster_width", img.getWidth());
        rasteredImageParams.put("raster_height", img.getHeight());

        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BufferedImage getImage(String imgPath) {
        BufferedImage tileImg = null;
        if (tileImg == null) {
            try {
                File in = new File(imgPath);
                tileImg = ImageIO.read(in);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return tileImg;
    }
}
