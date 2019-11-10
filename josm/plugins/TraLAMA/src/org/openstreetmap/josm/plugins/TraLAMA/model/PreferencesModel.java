package org.openstreetmap.josm.plugins.TraLAMA.model;

import java.util.prefs.*;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file PreferencesModel.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class PreferencesModel {
    private Preferences preferences;

    public PreferencesModel() {
        this.preferences = Preferences.userNodeForPackage(this.getClass());
    }

    /**
     * put Preferences based on SimulationSettings
     *
     *
     */
    public void setPreferences() {
        preferences.put("sumoExe", SimulationSettings.sumoExe);
        preferences.putBoolean("skipPoly", SimulationSettings.skipPoly);
        preferences.putBoolean("autoStart", SimulationSettings.autoStart);
        preferences.putBoolean("statistics", SimulationSettings.statistics);
        preferences.putBoolean("staticDemand", SimulationSettings.staticDemand);
        preferences.put("demand", SimulationSettings.demand);
        preferences.putInt("simLength", SimulationSettings.simLength);
        preferences.putDouble("stepLength", SimulationSettings.stepLength);
        preferences.putBoolean("sendToWeb", SimulationSettings.sendToWeb);
        preferences.putBoolean("saveSettings", SimulationSettings.saveSettings);
    }

    /**
     * get Preferences or load SimulationModel if empty
     *
     *
     */
    public void loadPreferences() {
        SimulationSettings.sumoExe = preferences.get("sumoExe", SimulationModel.sumoExe);
        SimulationSettings.skipPoly = preferences.getBoolean("skipPoly", SimulationModel.skipPoly);
        SimulationSettings.autoStart = preferences.getBoolean("autoStart", SimulationModel.autoStart);
        SimulationSettings.statistics = preferences.getBoolean("statistics", SimulationModel.statistics);
        SimulationSettings.staticDemand = preferences.getBoolean("staticDemand", SimulationModel.staticDemand);
        SimulationSettings.demand = preferences.get("demand", SimulationModel.demand);
        SimulationSettings.simLength = preferences.getInt("simLength", SimulationModel.simLength);
        SimulationSettings.stepLength = preferences.getDouble("stepLength", SimulationModel.stepLength);
        SimulationSettings.sendToWeb = preferences.getBoolean("sendToWeb", SimulationModel.sendToWeb);
        SimulationSettings.saveSettings = preferences.getBoolean("saveSettings", SimulationModel.saveSettings);
    }
}
