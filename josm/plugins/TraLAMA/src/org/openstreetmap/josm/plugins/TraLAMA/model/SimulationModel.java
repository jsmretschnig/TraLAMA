package org.openstreetmap.josm.plugins.TraLAMA.model;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file SimulationModel.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public final class SimulationModel {
    private SimulationModel() {}

    //Docker pathBase: "/ACPS/"
    //Docker host: "sumo"

    //Local pathBase: System.getProperty("user.home") + "/sourcetree/jakobs/ACPS/"
    //Local host: "localhost"

    //DIRECTORIES
    //ABSOLUTE
    public static final String pathBase = "/ACPS/";//System.getProperty("user.home") + "/sourcetree/jakobs/ACPS/"; //"/ACPS/";
    //RELATIVE
    private static final String pathData = "data/";
    public static final String pathOut = pathData + "output-simulation/"; //--> data/output-simulation

    //NAMES
    private static final String name = "selectedLayer";
    //TMP Files --> /data/out
    public static final String osmLayer = pathOut + name + ".osm";
    public static final String gpxFile = pathOut + name + ".gpx";
    public static final String csvOutFile = pathOut + name + ".csv";

    //SETTINGS
    public static final String sumoExe = "sumogui";
    public static final boolean skipPoly = false;
    public static final boolean autoStart = false;
    public static final boolean statistics = false;
    public static final boolean staticDemand = true;
    public static final String demand = "morning"; //morning, noon, afternoon
    public static final int simLength = 600;
    public static final double stepLength = 1.0;
    public static final boolean sendToWeb = false;
    public static final boolean saveSettings = true;

    public static final boolean runningSimulation = false;

    //NETWORK
    public static final String host = "http://" + "sumo" + ":4040"; //"http://" + "localhost" + ":4040"; or sumo instead of localhost

    //WEBSERVICE
    public static final String webServicePost = "http://" + "www.tralama.de" + "/php/json2csv.php"; //"http://" + "www.tralama.de" + "/php/json2csv.php"; //"http://" + "localhost" + ":8888" + "/tralama/php/json2csv.php"; //"http://" + "www.tralama.de" + "/php/json2csv.php";
    public static final String webServiceGet = "http://" + "www.tralama.de" + "/heatmap.html";
    public static final int webServicePostSize = 10000;

    //MULTILANGUAGE
    private static final Locale tmpLocale = Locale.getDefault();
    private static final Locale currentLocale = new Locale(tmpLocale.toString(), "DE");
    //TO-DO ant build does not include resources..
    //public static final ResourceBundle messages = ResourceBundle.getBundle("TraLAMA", currentLocale);
}
