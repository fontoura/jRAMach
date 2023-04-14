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

package com.github.fontoura.jramach.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.fontoura.jramach.ram.Instruction;
import com.github.fontoura.jramach.ram.InstructionArgument;
import com.github.fontoura.jramach.ram.Machine;

public class InstructionDisplay extends JPanel {
    private static final long serialVersionUID = 1L;

    private BallComponent ball1;
    private int i;
    private Machine machine;

    private boolean executing() {
        return machine.getProgramCounter() == i;
    }

    public void refresh() {
        if (ball1.isEnabled()) {
            if (!executing()) {
                ball1.setEnabled(false);
            }
        } else {
            if (executing()) {
                ball1.setEnabled(true);
            }
        }
    }

    public InstructionDisplay(Machine machine, int i) {
        i = i + 1;
        this.i = i;
        this.machine = machine;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0};
        gridBagLayout.rowWeights = new double[]{1.0};
        setLayout(gridBagLayout);

        ball1 = new BallComponent(16, Color.blue, Color.gray);
        ball1.setToolTipText("Will execute next line?");
        GridBagConstraints gbc_ball1 = new GridBagConstraints();
        gbc_ball1.insets = new Insets(0, 0, 0, 5);
        gbc_ball1.gridx = 0;
        gbc_ball1.gridy = 0;
        add(ball1, gbc_ball1);

        Instruction instruction = machine.getInstruction(i);

        if (!instruction.getLabels().isEmpty()) {
            JLabel labelsLabel = new JLabel(getLabels(instruction) + ": ");
            labelsLabel.setFont(labelsLabel.getFont().deriveFont(Font.ITALIC));
            GridBagConstraints gbc_labelsLabel = new GridBagConstraints();
            gbc_labelsLabel.anchor = GridBagConstraints.EAST;
            gbc_labelsLabel.gridx = 1;
            gbc_labelsLabel.gridy = 0;
            add(labelsLabel, gbc_labelsLabel);
        }

        JLabel nameLabel = new JLabel(instruction.getInstructionType().getMnemonic());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.EAST;
        gbc_nameLabel.gridx = 2;
        gbc_nameLabel.gridy = 0;
        add(nameLabel, gbc_nameLabel);

        JLabel argumentsLabel = new JLabel(getArguments(instruction));
        GridBagConstraints gbc_argumentsLabel = new GridBagConstraints();
        gbc_argumentsLabel.anchor = GridBagConstraints.WEST;
        gbc_argumentsLabel.insets = new Insets(0, 0, 0, 5);
        gbc_argumentsLabel.gridx = 3;
        gbc_argumentsLabel.gridy = 0;
        add(argumentsLabel, gbc_argumentsLabel);
    }

    private String getLabels(Instruction instruction) {
        String labels = "";
        for (String label : instruction.getLabels()) {
            if (labels.length() > 0) {
                labels += ", ";
            }
            labels += label;
        }
        return labels;
    }

    private String getArguments(Instruction instruction) {
        String arguments = "";
        for (InstructionArgument argument : instruction.getArguments()) {
            arguments += " " + argument;
        }
        return arguments;
    }
}
