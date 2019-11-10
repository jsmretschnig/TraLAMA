package org.openstreetmap.josm.plugins.TraLAMA.controller;

import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.TraLAMA.model.Coordinate;
import org.openstreetmap.josm.plugins.TraLAMA.model.SimulationModel;
import org.openstreetmap.josm.plugins.TraLAMA.model.FileIO;
import org.openstreetmap.josm.plugins.TraLAMA.model.SimulationSettings;
import org.openstreetmap.josm.plugins.TraLAMA.network.SimulationInterface;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @file SimulationThread.java
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @date 2019-08-01
 * @version 2.0
 */
public class SimulationThread extends PleaseWaitRunnable {

    private int work;
    private boolean res;
    private boolean canceled;

    public SimulationThread() {
        super("SUMO Traffic Simulation");
        work = 1120;
        res = true;
        canceled = false;
        preProcessing();
    }

    private void preProcessing() {
        FileIO.removeTmpFiles();
        if (!FileIO.writeOsmLayer()) {
            return;
        }
    }

    @Override
    protected void cancel() {
        canceled = true;
    }

    private void execute(String cmd) {
        this.progressMonitor.setCustomText(cmd); //below
        //this.progressMonitor.setExtraText(cmd); //right
        res = SimulationInterface.get(SimulationModel.host + "/" + cmd);
        if(!res)
            return;
        System.out.println("\n" + cmd + " done");
        this.progressMonitor.worked(work);
    }

    @Override
    protected void realRun() throws SAXException, IOException, OsmTransferException {

        //NETCONVERT
        execute("netconvert");
        if(canceled)
            return;

        //POLYCONVERT
        if (SimulationSettings.skipPoly) {
            this.progressMonitor.worked(work);
        } else {
            execute("polyconvert");
            if(canceled)
                return;
        }


        //TRAFFIC DEMAND
        if (SimulationSettings.staticDemand) {
            String json = "{" + "\"demand\"" + ":" + "\"" + SimulationSettings.demand + "\"" + "," +
                    "\"simLength\"" + ":" + SimulationSettings.simLength + "}";

            this.progressMonitor.setCustomText("statictrips");
            res = SimulationInterface.post(SimulationModel.host + "/statictrips",json);
            if(!res)
                return;
            if(canceled)
                return;
            System.out.println("\nstatictrips done");
            this.progressMonitor.worked(work);

            execute("duarouter");
            if(canceled)
                return;
        } else {
            //execute("randomroutes");
            String json = "{" + "\"simLength\"" + ":" + "\"" + SimulationSettings.simLength + "\"" + "," +
                    "\"stepLength\"" + ":" + SimulationSettings.stepLength + "}";

            this.progressMonitor.setCustomText("randomroutes");
            res = SimulationInterface.post(SimulationModel.host + "/randomroutes",json);
            if(!res)
                return;
            if(canceled)
                return;
            System.out.println("\nrandomroutes done");
            this.progressMonitor.worked(work);

            this.progressMonitor.worked(work); //additional for duarouter
        }

        //VEHICLE COLOR
        if (SimulationSettings.skipPoly) {
            this.progressMonitor.worked(work);
        } else {
            execute("vehiclecolor");
            if(canceled)
                return;
        }

        //SUMO SIMULATION
        String start = "manual";
        if(SimulationSettings.autoStart) {
            start = "auto";
        }
        String json = "{" + "\"exe\"" + ":" + "\"" + SimulationSettings.sumoExe + "\"" + "," +
                "\"simLength\"" + ":" + SimulationSettings.simLength + "," +
                "\"stepLength\"" + ":" + SimulationSettings.stepLength + "," +
                "\"start\"" + ":" + "\"" + start + "\"" + "," +
                "\"statistics\"" + ":" + SimulationSettings.statistics +
                "}";

        this.progressMonitor.setCustomText("sumo");
        res = SimulationInterface.post(SimulationModel.host + "/sumo",json);
        if(!res)
            return;
        if(canceled)
            return;
        System.out.println("\nsumo done");
        this.progressMonitor.worked(work);

//        if (!SimulationSettings.skipPoly) {
//            final int WAIT_TIME = 500; // milliseconds
//            JOptionPane.showMessageDialog(
//                    MainApplication.getMainFrame(),
//                    "To proceed, make sure that you have closed SUMO. Then click OK.",
//                    "Close SUMO",
//                    JOptionPane.WARNING_MESSAGE);
//            try {
//                Thread.sleep(WAIT_TIME);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


        //CREATE GPX OUTPUT
        execute("geoconverter");
        if(canceled)
            return;

        //IMPORT GPX OUTPUT
        this.progressMonitor.setCustomText("importGpxOutput");
        FileIO.importGpxOutput();
        if(canceled)
            return;
        this.progressMonitor.worked(work);

        if(SimulationSettings.sendToWeb) {

            //CREATE CSV OUTPUT
            //TO-DO perform in background
            execute("gpxconverter");
            if(canceled)
                return;

            //CREATE JSON OUTPUT
            this.progressMonitor.setCustomText("webservice");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            json = "{" +
                    "\"user\"" + ":" + "\"" + "TraLAMA" + "\""+ "," +
                    "\"date\"" + ":" + "\"" + dateFormat.format(date) + "\"" + "," +
                    "\"demand\"" + ":" + "\"" + SimulationSettings.demand + "\"" + "," +
                    "\"simLength\"" + ":" + SimulationSettings.simLength + "," +
                    "\"stepLength\"" + ":" + SimulationSettings.stepLength + "," +
                    "\"simulationOutput\"" + ":";

            List<Coordinate> coordinates = FileIO.importCsvOutput();
            int size = SimulationModel.webServicePostSize;
            for (int i = 0; i <= coordinates.size() / size; i++) {
                System.out.print("Coordinate chunk from " + i*size + " to " + Math.min((i+1)*size, coordinates.size()));
                String sendJsonPart = json + coordinates.subList(i*size, Math.min((i+1)*size, coordinates.size())).toString() + "}";
                //SEND TO WEB SERVICE
                res = SimulationInterface.post(SimulationModel.webServicePost, sendJsonPart);
                if(!res)
                    return;
                if(canceled)
                    return;
            }

            System.out.println("\nwebservice done");
        }
        this.progressMonitor.worked(work);

        this.progressMonitor.worked(work);
        this.progressMonitor.finishTask();
    }

    @Override
    protected void finish() {
        SimulationSettings.runningSimulation = false;
        if(canceled) {
//            new Notification(
//                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                            SimulationModel.messages.getString("userCanceled") + "<br />")
//                    .setIcon(JOptionPane.WARNING_MESSAGE)
//                    .setDuration(2500)
//                    .show();
            new Notification(
                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
                            tr("User stopped the SUMO simulation process") + "<br />")
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(2500)
                    .show();
            return;
        }
        if(res) {
            SimulationSettings.simCounter++;
//            new Notification(
//                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                            SimulationModel.messages.getString("simulationSuccess") + "<br />")
//                    .setIcon(JOptionPane.WARNING_MESSAGE)
//                    .setDuration(2500)
//                    .show();
            new Notification(
                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
                            tr("Successfully imported the SUMO simulation output") + "<br />")
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(2500)
                    .show();
        } else {
//            new Notification(
//                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                            SimulationModel.messages.getString("simulationError") + "<br />")
//                    .setIcon(JOptionPane.WARNING_MESSAGE)
//                    .setDuration(2500)
//                    .show();
            new Notification(
                    "<strong>" + tr("TraLAMA") + "</strong><br />" +
                            tr("Something went wrong ...") + "<br />")
                    .setIcon(JOptionPane.WARNING_MESSAGE)
                    .setDuration(2500)
                    .show();
        }
    }
}
