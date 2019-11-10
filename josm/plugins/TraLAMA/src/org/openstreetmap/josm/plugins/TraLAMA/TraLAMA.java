package org.openstreetmap.josm.plugins.TraLAMA;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.TraLAMA.view.SettingsView;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file TraLAMA.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class TraLAMA extends Plugin {

    //private TraLAMAAction traLamaAction;
    private SettingsView traLamaView;

    /**
     * Will be invoked by JOSM to bootstrap the plugin
     *
     * @param info  information about the plugin and its local installation
     */
    public TraLAMA (PluginInformation info) {
        super(info);
        System.out.println("This is TraLAMA!");

        //add new List item
        //traLamaAction = new TraLAMAAction();
        //registerToolMenu();
    }

//    private void registerToolMenu() {
//        MainApplication.getMenu().toolsMenu.add(traLamaAction);
//    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame,newFrame);
        if (newFrame != null) {
            registerToggleDialog();
        }
    }

    private void registerToggleDialog() {
        this.traLamaView = new SettingsView();
        MainApplication.getMap().addToggleDialog(traLamaView);
    }
}
