package org.openstreetmap.josm.plugins.TraLAMA.view;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.TraLAMA.controller.SimulationThread;
import org.openstreetmap.josm.tools.Shortcut;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * @author Ignacio Palermo - Julio Rivera
 * modified by Jakob Smretschnig
 *
 */
public class TraLAMAAction extends JosmAction {

    /**
     * This class is currently not in use. It was replaced by the ToggleDialog.
     */

    public TraLAMAAction() {
        //to-do show Shortcut
        //Shortcut s = new Shortcut("sumo");
        //s.setAssignedKey();
        super(tr("Run SUMO Traffic Simulation"), "TraLAMA.png",
                tr("Export OSM data to SUMO network file, polygon file and run a simulation."),
                Shortcut.registerShortcut("menu:sumoexport", tr("Menu: {0}", tr("SUMO Export")),
                        KeyEvent.VK_ENTER, Shortcut.CTRL_SHIFT), false, "sumoID", true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //to-do get settings from ToggleView
        MainApplication.worker.execute(new SimulationThread());
    }

    @Override
    protected void updateEnabledState() {
        //outgray the menu-item so that it only works if an OSM layer is selected
        if (getLayerManager().getActiveLayer() == null) {
            setEnabled(false);
        } else {
            setEnabled(getLayerManager().getActiveDataLayer() != null);
        }
    }
}
