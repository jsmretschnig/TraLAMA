package org.openstreetmap.josm.plugins.TraLAMA.model;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file SimulationSettings.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class SimulationSettings {
    private SimulationSettings() {}

    //COUNTER
    public static int simCounter = 1;

    //SETTINGS
    public static String sumoExe = SimulationModel.sumoExe;
    public static boolean skipPoly = SimulationModel.skipPoly;
    public static boolean autoStart = SimulationModel.autoStart;
    public static boolean statistics = SimulationModel.statistics;
    public static boolean staticDemand = true;
    public static String demand = SimulationModel.demand;
    public static int simLength = SimulationModel.simLength;
    public static double stepLength = SimulationModel.stepLength;
    public static boolean sendToWeb = SimulationModel.sendToWeb;
    public static boolean saveSettings = SimulationModel.saveSettings;

    public static boolean runningSimulation = SimulationModel.runningSimulation;

    public static void setDefaultSettings() {
        sumoExe = SimulationModel.sumoExe;
        skipPoly = SimulationModel.skipPoly;
        autoStart = SimulationModel.autoStart;
        statistics = SimulationModel.statistics;
        staticDemand = SimulationModel.staticDemand;
        demand = SimulationModel.demand;
        simLength = SimulationModel.simLength;
        stepLength = SimulationModel.stepLength;
        sendToWeb = SimulationModel.sendToWeb;
        saveSettings = SimulationModel.saveSettings;

        runningSimulation = SimulationModel.runningSimulation;
    }
}
