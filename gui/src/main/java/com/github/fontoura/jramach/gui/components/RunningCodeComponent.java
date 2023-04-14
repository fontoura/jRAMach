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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.fontoura.jramach.ram.Machine;

public class RunningCodeComponent extends JPanel {
    private static final long serialVersionUID = 1L;

    private InstructionDisplay[] code;

    public RunningCodeComponent(Machine machine) {
        code = new InstructionDisplay[machine.codeLength()];

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{1.0};
        gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        JPanel myPanel = new JPanel();
        myPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        myPanel.setLayout(new GridLayout(code.length, 0, 0, 5));
        for (int i = 0; i < code.length; i ++) {
            code[i] = new InstructionDisplay(machine, i);
            myPanel.add(code[i]);
        }

        GridBagConstraints gbc_myPanel = new GridBagConstraints();
        gbc_myPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_myPanel.gridx = 0;
        gbc_myPanel.gridy = 0;
        add(myPanel, gbc_myPanel);
        refresh();
    }

    public void refresh() {
        for (int i = 0; i < code.length; i ++)
            code[i].refresh();
    }
}
