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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.github.fontoura.jramach.ram.Assembler;
import com.github.fontoura.jramach.ram.Instruction;
import com.github.fontoura.jramach.ram.Machine;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextArea textArea;
    private JSpinner tapeLengthInput;
    private JSpinner registerCountInput;

    private File lastChosenFile;

    public static final void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

    public MainWindow() {
        lastChosenFile = new File(".");

        setTitle("JRAMach");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem menuItemNew = new JMenuItem("New");
        menuItemNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtNewFile();
            }
        });
        menuFile.add(menuItemNew);

        JMenuItem menuItemOpen = new JMenuItem("Open");
        menuItemOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtOpenFile();
            }
        });
        menuFile.add(menuItemOpen);

        JMenuItem menuItemSaveAs = new JMenuItem("Save as");
        menuItemSaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtSaveFile();
            }
        });
        menuFile.add(menuItemSaveAs);

        menuFile.addSeparator();

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuFile.add(menuItemExit);

        JMenu menuAbout = new JMenu("About");
        menuBar.add(menuAbout);

        JMenuItem menuItemAbout = new JMenuItem("About jRAMach");
        menuItemAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtAbout();
            }
        });
        menuAbout.add(menuItemAbout);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 0.0};
        contentPane.setLayout(gbl_contentPane);
        setContentPane(contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);

        JPanel settingsPanel = new JPanel();
        GridBagConstraints gbc_settingsPanel = new GridBagConstraints();
        gbc_settingsPanel.fill = GridBagConstraints.NONE;
        gbc_settingsPanel.anchor = GridBagConstraints.CENTER;
        gbc_settingsPanel.gridx = 0;
        gbc_settingsPanel.gridy = 1;
        contentPane.add(settingsPanel, gbc_settingsPanel);
        settingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_settingsPanel = new GridBagLayout();
        gbl_settingsPanel.columnWidths = new int[]{0, 0};
        gbl_settingsPanel.rowHeights = new int[]{0, 0, 0};
        gbl_settingsPanel.columnWeights = new double[]{0.0, 1.0};
        gbl_settingsPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        settingsPanel.setLayout(gbl_settingsPanel);

        JLabel tapeLengthLabel = new JLabel("Tape length: ");
        GridBagConstraints gbc_tapeLengthLabel = new GridBagConstraints();
        gbc_tapeLengthLabel.fill = GridBagConstraints.NONE;
        gbc_tapeLengthLabel.anchor = GridBagConstraints.EAST;
        gbc_tapeLengthLabel.insets = new Insets(0, 0, 5, 5);
        gbc_tapeLengthLabel.gridx = 0;
        gbc_tapeLengthLabel.gridy = 0;
        settingsPanel.add(tapeLengthLabel, gbc_tapeLengthLabel);

        tapeLengthInput = new JSpinner();
        tapeLengthInput.setModel(new SpinnerNumberModel(256, 1, 1000, 1));
        GridBagConstraints gbc_tapeLengthInput = new GridBagConstraints();
        gbc_tapeLengthInput.fill = GridBagConstraints.BOTH;
        gbc_tapeLengthInput.insets = new Insets(0, 0, 5, 0);
        gbc_tapeLengthInput.gridx = 1;
        gbc_tapeLengthInput.gridy = 0;
        settingsPanel.add(tapeLengthInput, gbc_tapeLengthInput);

        JLabel registerCountLabel = new JLabel("Registers: ");
        GridBagConstraints gbc_registerCountLabel = new GridBagConstraints();
        gbc_registerCountLabel.fill = GridBagConstraints.NONE;
        gbc_registerCountLabel.anchor = GridBagConstraints.EAST;
        gbc_registerCountLabel.insets = new Insets(0, 0, 0, 5);
        gbc_registerCountLabel.gridx = 0;
        gbc_registerCountLabel.gridy = 1;
        settingsPanel.add(registerCountLabel, gbc_registerCountLabel);

        registerCountInput = new JSpinner();
        registerCountInput.setModel(new SpinnerNumberModel(16, 1, 100, 1));
        GridBagConstraints gbc_registerCountInput = new GridBagConstraints();
        gbc_registerCountInput.fill = GridBagConstraints.BOTH;
        gbc_registerCountInput.insets = new Insets(0, 0, 0, 0);
        gbc_registerCountInput.gridx = 1;
        gbc_registerCountInput.gridy = 1;
        settingsPanel.add(registerCountInput, gbc_registerCountInput);

        JPanel buttonPanel = new JPanel();
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.fill = GridBagConstraints.NONE;
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 2;
        contentPane.add(buttonPanel, gbc_buttonPanel);
        buttonPanel.setLayout(new GridLayout(1, 0, 5, 0));

        JButton buttonCompile = new JButton("Compile");
        buttonCompile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtCompile();
            }
        });
        buttonPanel.add(buttonCompile);

        JButton buttonExecute = new JButton("Execute");
        buttonExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evtExecute();
            }
        });
        buttonPanel.add(buttonExecute);
    }

    protected void evtAbout() {
        JOptionPane.showMessageDialog(this, "jRAMach was developed by Felipe Michels Fontoura in 2014. Is is released under the MIT license.", "About jRAMach", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void evtNewFile() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Do you really want to create a new file? You will lose all unsaved changes.",
            "Create a new file?",
            JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            textArea.setText("");
        }
    }

    protected void evtOpenFile() {
        JFileChooser chooser = new JFileChooser(lastChosenFile);
        int saida = chooser.showOpenDialog(this);
        if (saida == JFileChooser.APPROVE_OPTION) {
            saida = JOptionPane.showConfirmDialog(
                this,
                "Do you really want to open a file? You will lose all unsaved changes.",
                "Open a file?",
                JOptionPane.YES_NO_OPTION
            );
            if (saida == JOptionPane.YES_OPTION) {
                try {
                    File chosen_file = chooser.getSelectedFile();
                    lastChosenFile = chosen_file;
                    FileInputStream fin = new FileInputStream(chosen_file);
                    byte[] data;
                    try {
                        data = new byte[fin.available()];
                        fin.read(data);
                    } finally {
                        fin.close();
                    }
                    textArea.setText(new String(data, "UTF8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void evtSaveFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(this);
        File chosen = chooser.getSelectedFile();
        if (chosen != null) {
            lastChosenFile = chosen;
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(chosen);
                try {
                    fout.write(textArea.getText().getBytes("UTF8"));
                } finally {
                    fout.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void evtCompile() {
        try {
            Instruction[] i = Assembler.compile(textArea.getText());
            if (i.length == 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "There is no code to display!",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            String code = Assembler.decompile(i);
            CompiledWindow display = new CompiledWindow(this, code);
            display.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "An error has occured: " + e.getClass().getCanonicalName() + "\n" + e.toString(),
                "Error!",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    protected void evtExecute() {
        try {
            Instruction[] instructions = Assembler.compile(textArea.getText());
            if (instructions.length == 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "There is no code to execute!",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int tapeLength = (Integer) tapeLengthInput.getValue();
            int registerCount = (Integer) registerCountInput.getValue();

            Machine machine = new Machine(tapeLength, registerCount);
            machine.setProgram(instructions);
            RunWindow display = new RunWindow(this, machine);
            display.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "An error has occured: " + e.getClass().getCanonicalName() + "\n" + e.toString(),
                "Error!",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

}
