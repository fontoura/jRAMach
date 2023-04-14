/*
 * Copyright (c) 2014-2023 Felipe Michels Fontoura
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.fontoura.jramach.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.fontoura.jramach.gui.components.RegistersDisplay;
import com.github.fontoura.jramach.gui.components.RunningCodeComponent;
import com.github.fontoura.jramach.gui.components.TapeDisplayComponent;
import com.github.fontoura.jramach.ram.Machine;

public class RunWindow extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final long[] SLEEP_INTERVALS_MS = {
        1000L,
        500L,
        100L,
        50L,
        10L
    };

    private static final int INITIAL_SLEEP_INTERVAL_INDEX = 1;

    private JButton buttonAction;
    private JButton buttonStep;
    private JButton buttonReset;
    private JSlider speedSlider;
    private RegistersDisplay registersDisplay;
    private TapeDisplayComponent tapeDisplay;
    private RunningCodeComponent codeDisplay;

    private Machine machine;
    private RunThread runThread;
    private long sleepIntervalMs = SLEEP_INTERVALS_MS[INITIAL_SLEEP_INTERVAL_INDEX];

    public RunWindow(JFrame frame, Machine machine) {
        super(frame, true);

        setTitle("jRAMach - Execution");
        setLocationByPlatform(true);
        setSize(800, 600);

        this.machine = machine;

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0};
        gbl_contentPane.rowHeights = new int[]{0};
        gbl_contentPane.columnWeights = new double[]{0.5, 1.0};
        gbl_contentPane.rowWeights = new double[]{1.0};
        contentPane.setLayout(gbl_contentPane);

        JPanel leftPanel = new JPanel();
        GridBagConstraints gbc_leftPanel = new GridBagConstraints();
        gbc_leftPanel.insets = new Insets(0, 0, 5, 5);
        gbc_leftPanel.fill = GridBagConstraints.BOTH;
        gbc_leftPanel.gridx = 0;
        gbc_leftPanel.gridy = 0;
        contentPane.add(leftPanel, gbc_leftPanel);
        GridBagLayout gbl_leftPanel = new GridBagLayout();
        gbl_leftPanel.columnWidths = new int[]{0};
        gbl_leftPanel.rowHeights = new int[]{0, 0};
        gbl_leftPanel.columnWeights = new double[]{1.0};
        gbl_leftPanel.rowWeights = new double[]{0.0, 1.0};
        leftPanel.setLayout(gbl_leftPanel);

        JLabel programLabel = new JLabel("Program");
        GridBagConstraints gbc_programLabel = new GridBagConstraints();
        gbc_programLabel.insets = new Insets(0, 0, 5, 0);
        gbc_programLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_programLabel.gridx = 0;
        gbc_programLabel.gridy = 0;
        leftPanel.add(programLabel, gbc_programLabel);

        JScrollPane codeScroll = new JScrollPane();
        codeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        codeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagConstraints gbc_codeScroll = new GridBagConstraints();
        gbc_codeScroll.fill = GridBagConstraints.BOTH;
        gbc_codeScroll.gridx = 0;
        gbc_codeScroll.gridy = 1;
        leftPanel.add(codeScroll, gbc_codeScroll);

        codeDisplay = new RunningCodeComponent(machine);
        codeScroll.setViewportView(codeDisplay);

        JPanel rightPanel = new JPanel();
        GridBagConstraints gbc_rightPanel = new GridBagConstraints();
        gbc_rightPanel.fill = GridBagConstraints.BOTH;
        gbc_rightPanel.gridx = 1;
        gbc_rightPanel.gridy = 0;
        contentPane.add(rightPanel, gbc_rightPanel);
        GridBagLayout gbl_rightPanel = new GridBagLayout();
        gbl_rightPanel.columnWidths = new int[]{0};
        gbl_rightPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_rightPanel.columnWeights = new double[]{1.0};
        gbl_rightPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0};
        rightPanel.setLayout(gbl_rightPanel);

        JPanel panelLabels = new JPanel();
        GridBagConstraints gbc_panelLabels = new GridBagConstraints();
        gbc_panelLabels.fill = GridBagConstraints.HORIZONTAL;
        gbc_panelLabels.insets = new Insets(0, 0, 5, 0);
        gbc_panelLabels.gridx = 0;
        gbc_panelLabels.gridy = 0;
        rightPanel.add(panelLabels, gbc_panelLabels);
        panelLabels.setLayout(new GridLayout(1, 0, 5, 0));

        JLabel labelRegisters = new JLabel("Registers");
        panelLabels.add(labelRegisters);

        JLabel labelTape = new JLabel("Tape");
        panelLabels.add(labelTape);

        JPanel panelLists = new JPanel();
        GridBagConstraints gbc_panelLists = new GridBagConstraints();
        gbc_panelLists.insets = new Insets(0, 0, 5, 0);
        gbc_panelLists.fill = GridBagConstraints.BOTH;
        gbc_panelLists.gridx = 0;
        gbc_panelLists.gridy = 1;
        rightPanel.add(panelLists, gbc_panelLists);
        panelLists.setLayout(new GridLayout(1, 0, 5, 0));

        JScrollPane scrollRegisters = new JScrollPane();
        scrollRegisters.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollRegisters.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelLists.add(scrollRegisters);

        registersDisplay = new RegistersDisplay(machine);
        scrollRegisters.setViewportView(registersDisplay);

        JScrollPane scrollTape = new JScrollPane();
        scrollTape.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollTape.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelLists.add(scrollTape);

        tapeDisplay = new TapeDisplayComponent(machine);
        scrollTape.setViewportView(tapeDisplay);

        JPanel speedPanel = new JPanel();
        GridBagConstraints gbc_speedPanel = new GridBagConstraints();
        gbc_speedPanel.fill = GridBagConstraints.BOTH;
        gbc_speedPanel.insets = new Insets(0, 0, 5, 0);
        gbc_speedPanel.gridx = 0;
        gbc_speedPanel.gridy = 2;
        rightPanel.add(speedPanel, gbc_speedPanel);
        GridBagLayout gbl_speedPanel = new GridBagLayout();
        gbl_speedPanel.columnWidths = new int[]{0, 0};
        gbl_speedPanel.rowHeights = new int[]{0};
        gbl_speedPanel.columnWeights = new double[]{0.0, 1.0};
        gbl_speedPanel.rowWeights = new double[]{1.0};
        speedPanel.setLayout(gbl_speedPanel);

        JLabel speedLabel = new JLabel("Execution speed: ");
        GridBagConstraints gbc_speedLabel = new GridBagConstraints();
        gbc_speedLabel.fill = GridBagConstraints.NONE;
        gbc_speedLabel.anchor = GridBagConstraints.EAST;
        gbc_speedLabel.insets = new Insets(0, 0, 0, 5);
        gbc_speedLabel.gridx = 0;
        gbc_speedLabel.gridy = 0;
        speedPanel.add(speedLabel, gbc_speedLabel);

        speedSlider = new JSlider();
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                if (!slider.getValueIsAdjusting()) {
                    speedSliderEvent();
                }
            }
        });
        speedSlider.setValue(INITIAL_SLEEP_INTERVAL_INDEX);
        speedSlider.setMaximum(SLEEP_INTERVALS_MS.length - 1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        GridBagConstraints gbc_speedSlider = new GridBagConstraints();
        gbc_speedSlider.fill = GridBagConstraints.HORIZONTAL;
        gbc_speedSlider.gridx = 1;
        gbc_speedSlider.gridy = 0;
        speedPanel.add(speedSlider, gbc_speedSlider);

        JPanel buttonPanel = new JPanel();
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 3;
        rightPanel.add(buttonPanel, gbc_buttonPanel);
        buttonPanel.setLayout(new GridLayout(1, 0, 5, 0));

        buttonAction = new JButton("Execute");
        buttonAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonEvent();
            }
        });
        buttonPanel.add(buttonAction);

        buttonStep = new JButton("Step");
        buttonStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonStep();
            }
        });
        buttonPanel.add(buttonStep);

        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonReset();
            }
        });
        buttonPanel.add(buttonReset);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                runThread = null;
                dispose();
            }
        });
    }

    protected void speedSliderEvent() {
        sleepIntervalMs = SLEEP_INTERVALS_MS[speedSlider.getValue()];
    }

    private void buttonEvent() {
        synchronized (this) {
            if (runThread == null) {
                runThread = new RunThread();
                refreshButton();
                runThread.start();
            } else {
                runThread = null;
                buttonAction.setEnabled(false);
            }
        }
    }

    private void buttonStep() {
        synchronized (this) {
            if (runThread == null) {
                doStep();
            }
        }
    }

    private void buttonReset() {
        synchronized (this) {
            machine.reset();
            registersDisplay.refresh();
            tapeDisplay.refresh();
            codeDisplay.refresh();
            refreshButton();
        }
    }

    private void refreshButton() {
        if (machine.isHalted()) {
            buttonAction.setText("Execute");
            buttonAction.setEnabled(false);
            buttonStep.setEnabled(false);
            buttonReset.setEnabled(true);
        } else if (runThread == null) {
            buttonAction.setText("Execute");
            buttonAction.setEnabled(true);
            buttonStep.setEnabled(true);
            buttonReset.setEnabled(true);
        } else {
            buttonAction.setText("Halt");
            buttonAction.setEnabled(true);
            buttonStep.setEnabled(false);
            buttonReset.setEnabled(false);
        }
    }

    private void doStep() {
        if (!machine.isHalted()) {
            machine.nextInstruction();
            registersDisplay.refresh();
            tapeDisplay.refresh();
            codeDisplay.refresh();
        }
        refreshButton();
    }

    class RunThread extends Thread {
        @Override
        public void run() {
            try {
                boolean running = true;
                while (running) {
                    if (machine.isHalted()) {
                        running = false;
                    }
                    try {
                        sleep(sleepIntervalMs);
                    } catch (InterruptedException e) {}
                    machine.nextInstruction();
                    registersDisplay.refresh();
                    tapeDisplay.refresh();
                    codeDisplay.refresh();
                    if (runThread == null) {
                        running = false;
                    }
                }
            } catch (Throwable t) {}
            runThread = null;
            refreshButton();
        }
    }
}
