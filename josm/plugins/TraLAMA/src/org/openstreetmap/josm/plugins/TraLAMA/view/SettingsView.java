package org.openstreetmap.josm.plugins.TraLAMA.view;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.plugins.TraLAMA.controller.SimulationThread;
import org.openstreetmap.josm.plugins.TraLAMA.model.PreferencesModel;
import org.openstreetmap.josm.plugins.TraLAMA.model.SimulationModel;
import org.openstreetmap.josm.plugins.TraLAMA.model.SimulationSettings;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * Copyright (C) 2019 Jakob Smretschnig
 * This program is made available under the terms of the GNU General Public License v3.0
 * which accompanies this distribution
 *
 * @author Jakob Smretschnig <jakob.smretschnig@tum.de>
 * @version 2.0
 * @file SettingsView.java
 * @date 2019-08-01
 */
public class SettingsView extends ToggleDialog {

    private PreferencesModel preferencesModel;

    private JRadioButton graphicalSimulationRadioButton;
    private JCheckBox saveSettingsForFutureCheckBox;
    private JTextField stepLengthTextField;
    private JCheckBox startSUMOSimulationAutomaticallyCheckBox;
    private JRadioButton backgroundSimulationRadioButton;
    private JPanel rootPanel;
    private JButton startButton;
    private JButton defaultButton;
    private JRadioButton noonRadioButton;
    private JTextField simLengthTextField;
    private JCheckBox createSUMOSimulationStatisticsCheckBox;
    private JPanel panelLeft;
    private JPanel panelRight;
    private JPanel panelSubmit;
    private JButton wwwTralamaDeButton;
    private JCheckBox webserviceCheckBox;
    private JRadioButton morningRadioButton;
    private JRadioButton afternoonRadioButton;
    private JCheckBox staticOnlyForKirchheimCheckBox;
    private JButton saveButton;
    private JLabel randomLabel;
    private ButtonGroup buttonGroupGuiBackground;

    public SettingsView() {
        super(
                tr("TraLAMA - Simulation Settings"),
                "TraLAMA.png",
                tr("Run SUMO Traffic Simulation"),
                null,
                280,
                true
        );

        this.preferencesModel = new PreferencesModel();

        this.createLayout(rootPanel, true, null);

        preferencesModel.loadPreferences();
        setForm();
        addAllListener();
    }

    private void setDefaultSettings() {
        SimulationSettings.setDefaultSettings(); //initialize SimulationSettings with SimulationModel
        preferencesModel.setPreferences(); //put preferences based on variables
        setForm(); //set gui elements based on variables
    }

    /**
     * set GUI Elements based on SimulationSettings
     */
    private void setForm() {
        graphicalSimulationRadioButton.setSelected(SimulationSettings.sumoExe.equals("sumogui"));
        backgroundSimulationRadioButton.setSelected(SimulationSettings.sumoExe.equals("sumo"));
        staticOnlyForKirchheimCheckBox.setSelected(SimulationSettings.staticDemand);
        morningRadioButton.setSelected(SimulationSettings.demand.equals("morning"));
        noonRadioButton.setSelected(SimulationSettings.demand.equals("noon"));
        afternoonRadioButton.setSelected(SimulationSettings.demand.equals("afternoon"));
        startSUMOSimulationAutomaticallyCheckBox.setSelected(SimulationSettings.autoStart);
        createSUMOSimulationStatisticsCheckBox.setSelected(SimulationSettings.statistics);
        webserviceCheckBox.setSelected(SimulationSettings.sendToWeb);
        simLengthTextField.setText(SimulationSettings.simLength + "");
        stepLengthTextField.setText(SimulationSettings.stepLength + "");
        saveSettingsForFutureCheckBox.setSelected(SimulationSettings.saveSettings);

        if (SimulationSettings.staticDemand) {
            updateDemandGroup(true);
        } else {
            updateDemandGroup(false);
        }

        if (backgroundSimulationRadioButton.isSelected()) {
            startSUMOSimulationAutomaticallyCheckBox.setEnabled(false);
        } else if (graphicalSimulationRadioButton.isSelected()) {
            startSUMOSimulationAutomaticallyCheckBox.setEnabled(true);
        }
    }

    private void addAllListener() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SimulationSettings.runningSimulation) {
//                    new Notification(
//                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                                SimulationModel.messages.getString("simulationRunning") + "<br />")
//                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
//                        .setDuration(5000)
//                        .show();

                    new Notification(
                            "<strong>" + tr("TraLAMA") + "</strong><br />" +
                                    tr("Simulation is already running. Please wait ..") + "<br />")
                            .setIcon(JOptionPane.INFORMATION_MESSAGE)
                            .setDuration(5000)
                            .show();
                    return;
                }
                SimulationSettings.runningSimulation = true;
                saveSettings();

//                new Notification(
//                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                                SimulationModel.messages.getString("savedStart") + "<br />")
//                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
//                        .setDuration(5000)
//                        .show();

                new Notification(
                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
                                tr("Settings Saved. Starting Simulation ...") + "<br />")
                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
                        .setDuration(5000)
                        .show();

                MainApplication.worker.execute(new SimulationThread());
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();

//                new Notification(
//                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                                SimulationModel.messages.getString("saveSettings") + "<br />")
//                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
//                        .setDuration(2500)
//                        .show();

                new Notification(
                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
                                tr("Settings Saved.") + "<br />")
                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
                        .setDuration(2500)
                        .show();
            }
        });
        defaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultSettings();

//                new Notification(
//                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
//                                SimulationModel.messages.getString("defaultSettings") + "<br />")
//                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
//                        .setDuration(2500)
//                        .show();
                new Notification(
                        "<strong>" + tr("TraLAMA") + "</strong><br />" +
                                tr("Set to Default Settings") + "<br />")
                        .setIcon(JOptionPane.INFORMATION_MESSAGE)
                        .setDuration(2500)
                        .show();
            }
        });
        wwwTralamaDeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(SimulationModel.webServiceGet));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        graphicalSimulationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSUMOSimulationAutomaticallyCheckBox.setEnabled(true);
            }
        });
        backgroundSimulationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSUMOSimulationAutomaticallyCheckBox.setEnabled(false);
            }
        });
        staticOnlyForKirchheimCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (staticOnlyForKirchheimCheckBox.isSelected()) {
                    updateDemandGroup(true);
                } else {
                    updateDemandGroup(false);
                }
            }
        });
    }

    private void updateDemandGroup(boolean b) {
        morningRadioButton.setEnabled(b);
        noonRadioButton.setEnabled(b);
        afternoonRadioButton.setEnabled(b);
        if (b) {
            //do not show label
            randomLabel.setText("");
        } else {
//            randomLabel.setText(SimulationModel.messages.getString("randomLabel"));
            randomLabel.setText("Simulation uses Random Demand");
        }
    }

    private void saveSettings() {
        //view or Background
        if (graphicalSimulationRadioButton.isSelected()) {
            SimulationSettings.sumoExe = "sumogui";
            SimulationSettings.skipPoly = false;
        } else {
            SimulationSettings.sumoExe = "sumo";
            SimulationSettings.skipPoly = true;
        }

        if (startSUMOSimulationAutomaticallyCheckBox.isSelected()) {
            //add autoStart
            SimulationSettings.autoStart = true;
        } else {
            SimulationSettings.autoStart = false;
        }

        if (staticOnlyForKirchheimCheckBox.isSelected()) {
            SimulationSettings.staticDemand = true;
        } else {
            SimulationSettings.staticDemand = false;
        }

        if (morningRadioButton.isSelected()) {
            SimulationSettings.demand = "morning";
        } else if (noonRadioButton.isSelected()) {
            SimulationSettings.demand = "noon";
        } else if (afternoonRadioButton.isSelected()) {
            SimulationSettings.demand = "afternoon";
        }

        if (webserviceCheckBox.isSelected()) {
            SimulationSettings.sendToWeb = true;
        } else {
            SimulationSettings.sendToWeb = false;
        }

        if (createSUMOSimulationStatisticsCheckBox.isSelected()) {
            SimulationSettings.statistics = true;
        } else {
            SimulationSettings.statistics = false;
        }

        try {
            int simLength = Integer.parseInt(simLengthTextField.getText());
            if (simLength < 60 || simLength > 10800) {
                setDefaultSimLength();
            } else {
                SimulationSettings.simLength = simLength;
            }
        } catch (NumberFormatException exception) {
            setDefaultSimLength();
        }

        try {
            double timeStep = Double.parseDouble(stepLengthTextField.getText());
            if (timeStep < 0.1 || timeStep > 10.0) {
                setDefaultStepLength();
            } else {
                SimulationSettings.stepLength = timeStep;
            }
        } catch (NumberFormatException exception) {
            setDefaultStepLength();
        }

        //Save Settings
        if (saveSettingsForFutureCheckBox.isSelected()) {
            SimulationSettings.saveSettings = true;
            preferencesModel.setPreferences();
        } else {
            SimulationSettings.saveSettings = false;
        }
    }

    private void setDefaultSimLength() {
        simLengthTextField.setText(SimulationModel.simLength + "");
//        Notification n = new Notification(SimulationModel.messages.getString("setDefaultSimLength") + SimulationModel.simLength);
        Notification n = new Notification("Couldn't read simLength. Must be a positive whole number in the range of 60 to 10800 seconds. Set to default-value " + SimulationModel.simLength);
        n.setDuration(5000);
        n.show();
        SimulationSettings.simLength = SimulationModel.simLength;
    }

    private void setDefaultStepLength() {
        stepLengthTextField.setText(SimulationModel.stepLength + "");
//        Notification n = new Notification(SimulationModel.messages.getString("setDefaultStepLength") + SimulationModel.stepLength);
        Notification n = new Notification("Couldn't read stepLength. Must be a positive comma-separated number in the range of 0.1 to 10.0. Set to default-value " + SimulationModel.stepLength);
        n.setDuration(5000);
        n.show();
        SimulationSettings.stepLength = SimulationModel.stepLength;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridBagLayout());
        rootPanel.setBorder(BorderFactory.createTitledBorder(""));
        panelLeft = new JPanel();
        panelLeft.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panelLeft, gbc);
        graphicalSimulationRadioButton = new JRadioButton();
        graphicalSimulationRadioButton.setSelected(true);
        graphicalSimulationRadioButton.setText("Graphical");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(graphicalSimulationRadioButton, gbc);
        startSUMOSimulationAutomaticallyCheckBox = new JCheckBox();
        startSUMOSimulationAutomaticallyCheckBox.setText("Start SUMO Simulation Automatically");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(startSUMOSimulationAutomaticallyCheckBox, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Traffic Demand Selection");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelLeft.add(label1, gbc);
        backgroundSimulationRadioButton = new JRadioButton();
        backgroundSimulationRadioButton.setText("Background");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(backgroundSimulationRadioButton, gbc);
        morningRadioButton = new JRadioButton();
        morningRadioButton.setSelected(true);
        morningRadioButton.setText("Morning");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(morningRadioButton, gbc);
        afternoonRadioButton = new JRadioButton();
        afternoonRadioButton.setText("Afternoon");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(afternoonRadioButton, gbc);
        noonRadioButton = new JRadioButton();
        noonRadioButton.setSelected(false);
        noonRadioButton.setText("Noon");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(noonRadioButton, gbc);
        staticOnlyForKirchheimCheckBox = new JCheckBox();
        staticOnlyForKirchheimCheckBox.setSelected(true);
        staticOnlyForKirchheimCheckBox.setText("Static (only for Kirchheim b. München)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelLeft.add(staticOnlyForKirchheimCheckBox, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Simulation Type");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelLeft.add(label2, gbc);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panelLeft.add(separator1, gbc);
        randomLabel = new JLabel();
        randomLabel.setText("otherwise, random demand is used");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelLeft.add(randomLabel, gbc);
        panelRight = new JPanel();
        panelRight.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panelRight, gbc);
        final JSeparator separator2 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelRight.add(separator2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Simulation Length");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelRight.add(label3, gbc);
        webserviceCheckBox = new JCheckBox();
        webserviceCheckBox.setEnabled(true);
        webserviceCheckBox.setSelected(false);
        webserviceCheckBox.setText("Send to Webservice");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelRight.add(webserviceCheckBox, gbc);
        simLengthTextField = new JTextField();
        simLengthTextField.setText("600");
        simLengthTextField.setToolTipText("[seconds]");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRight.add(simLengthTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("[steps] (range is 60 to 10800)");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelRight.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(0);
        label5.setHorizontalTextPosition(0);
        label5.setText("Simulation Step Length");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelRight.add(label5, gbc);
        createSUMOSimulationStatisticsCheckBox = new JCheckBox();
        createSUMOSimulationStatisticsCheckBox.setEnabled(false);
        createSUMOSimulationStatisticsCheckBox.setText("Create SUMO Simulation Statistics");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelRight.add(createSUMOSimulationStatisticsCheckBox, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Simulation Output");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panelRight.add(label6, gbc);
        stepLengthTextField = new JTextField();
        stepLengthTextField.setText("1.0");
        stepLengthTextField.setToolTipText("[seconds]");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRight.add(stepLengthTextField, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("[sec] (range is 0.1 to 10.0)");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelRight.add(label7, gbc);
        final JSeparator separator3 = new JSeparator();
        separator3.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        panelRight.add(separator3, gbc);
        panelSubmit = new JPanel();
        panelSubmit.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        rootPanel.add(panelSubmit, gbc);
        startButton = new JButton();
        startButton.setText("Save and Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSubmit.add(startButton, gbc);
        defaultButton = new JButton();
        defaultButton.setText("Default");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSubmit.add(defaultButton, gbc);
        wwwTralamaDeButton = new JButton();
        wwwTralamaDeButton.setEnabled(false);
        wwwTralamaDeButton.setText("Show Heatmap in Browser");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSubmit.add(wwwTralamaDeButton, gbc);
        saveSettingsForFutureCheckBox = new JCheckBox();
        saveSettingsForFutureCheckBox.setEnabled(true);
        saveSettingsForFutureCheckBox.setSelected(true);
        saveSettingsForFutureCheckBox.setText("Save Settings for Future");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panelSubmit.add(saveSettingsForFutureCheckBox, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panelSubmit.add(panel1, gbc);
        final JSeparator separator4 = new JSeparator();
        panel1.add(separator4, BorderLayout.CENTER);
        saveButton = new JButton();
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelSubmit.add(saveButton, gbc);
        buttonGroupGuiBackground = new ButtonGroup();
        buttonGroupGuiBackground.add(graphicalSimulationRadioButton);
        buttonGroupGuiBackground.add(backgroundSimulationRadioButton);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(morningRadioButton);
        buttonGroup.add(noonRadioButton);
        buttonGroup.add(afternoonRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
