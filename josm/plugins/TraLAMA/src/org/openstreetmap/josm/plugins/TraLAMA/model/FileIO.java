package org.openstreetmap.josm.plugins.TraLAMA.model;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.TagMap;
import org.openstreetmap.josm.data.osm.UploadPolicy;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.GpxLayer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.GpxReader;
import org.openstreetmap.josm.io.OsmWriter;
import org.openstreetmap.josm.io.OsmWriterFactory;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file FileIO.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class FileIO {

    private static List<Way> filterOSM(List<Way> ways) {
        Iterator<Way> iterator = ways.iterator();
        while(iterator.hasNext()) {
            Way way = iterator.next();
            TagMap tm = way.getKeys();
            if(tm.containsKey("highway")) {
                if (tm.get("highway").equals("footway") || tm.get("highway").equals("cycleway") || tm.get("highway").equals("pedestrian")) {
                    System.out.println(tm.getTags());
                    iterator.remove();
                }
            }
            if(tm.containsKey("vehicle")) {
                if(tm.get("vehicle").equals("no")) {
                    System.out.println(tm.getTags());
                    iterator.remove();
                }
            }
        }
        return ways;
    }

    public static boolean writeOsmLayer() {
        OsmDataLayer osmDataLayer = MainApplication.getLayerManager().getActiveDataLayer();
        if(osmDataLayer != null) {
            DataSet dataSet = osmDataLayer.getDataSet();
            //List<Way> ways = new ArrayList<Way>(dataSet.getWays());
            //ways = FileIO.filterOSM(ways);

            //save as osm file first
            try {
                PrintWriter pw = new PrintWriter(SimulationModel.pathBase + SimulationModel.osmLayer);
                OsmWriter osmWriter = OsmWriterFactory.createOsmWriter(pw, true, "0.6");

                osmWriter.setWithBody(true);
                osmWriter.setWithVisible(true);

                osmWriter.write(dataSet);

//                osmWriter.header(dataSet.getDownloadPolicy(), dataSet.getUploadPolicy());
//                osmWriter.writeDataSources(dataSet);
//                osmWriter.setWithVisible(UploadPolicy.NORMAL == dataSet.getUploadPolicy());
//                osmWriter.writeNodes(dataSet.getNodes());
//                osmWriter.writeWays(ways);
//                osmWriter.writeRelations(dataSet.getRelations());
//                osmWriter.footer();

                //osmWriter.writeWays(ways);
                pw.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void importGpxOutput() {
        InputStream inputstreamGPX = null;

        try {
            inputstreamGPX = new FileInputStream(SimulationModel.pathBase + SimulationModel.gpxFile);
            GpxReader gpxReader = new GpxReader(inputstreamGPX);
            boolean successfullyParsed = gpxReader.parse(true);

            if(successfullyParsed) {
                GpxData gpxData = gpxReader.getGpxData();
                gpxData.storageFile = new File(SimulationModel.pathBase + SimulationModel.gpxFile);
                gpxData.creator = "SUMO - Simulation of Urban MObility";

                GpxLayer gpxLayer = new GpxLayer(gpxData, "SUMO Simulation Output " + SimulationSettings.simCounter);
                MainApplication.getLayerManager().addLayer(gpxLayer);
                MainApplication.getLayerManager().setActiveLayer(gpxLayer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static List<Coordinate> importCsvOutput() {
        BufferedReader br = null;
        final String SPLIT_BY = ",";

        List<Coordinate> coordinates = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(SimulationModel.pathBase + SimulationModel.csvOutFile));

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(SPLIT_BY); // 0 --> date, 1 --> time, 2 --> lat, 3 --> long
                if(values[2].equals("latitude")) {
                    continue;
                }
                Coordinate c = new Coordinate(Double.parseDouble(values[2]), Double.parseDouble(values[3]));
                coordinates.add(c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return coordinates;
    }

    public static void removeTmpFiles() {
        File dir = new File(SimulationModel.pathBase + SimulationModel.pathOut);
        if (!dir.exists()) {
            dir.mkdir();
        }
        for(File file: dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}
