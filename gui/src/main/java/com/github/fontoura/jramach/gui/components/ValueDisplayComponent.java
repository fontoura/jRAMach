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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class ValueDisplayComponent extends JPanel {
    private static final long serialVersionUID = 1L;

    private String nameV;
    private BallComponent ball1, ball2;
    private JLabel valueLabel;

    public void refresh() {
        if (ball1.isEnabled()) {
            if (!hasValueBeenChanged()) {
                ball1.setEnabled(false);
            }
        } else {
            if (hasValueBeenChanged()) {
                ball1.setEnabled(true);
            }
        }

        if (ball2.isEnabled()) {
            if (!hasValueBeenRead()) {
                ball2.setEnabled(false);
            }
        } else {
            if (hasValueBeenRead()) {
                ball2.setEnabled(true);
            }
        }

        valueLabel.setText(Integer.toString(getValue()));
        validate();
    }

    public ValueDisplayComponent(String name) {
        nameV = name;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0};
        gridBagLayout.rowWeights = new double[]{1.0};
        setLayout(gridBagLayout);

        ball1 = new BallComponent(16, Color.green, Color.gray);
        ball1.setToolTipText("Has value " + name + " been written?");
        GridBagConstraints gbc_ball1 = new GridBagConstraints();
        gbc_ball1.insets = new Insets(0, 0, 0, 5);
        gbc_ball1.gridx = 0;
        gbc_ball1.gridy = 0;
        add(ball1, gbc_ball1);

        ball2 = new BallComponent(16, Color.red, Color.gray);
        ball1.setToolTipText("Has value " + name + " been read?");
        GridBagConstraints gbc_ball2 = new GridBagConstraints();
        gbc_ball2.insets = new Insets(0, 0, 0, 5);
        gbc_ball2.gridx = 1;
        gbc_ball2.gridy = 0;
        add(ball2, gbc_ball2);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.WEST;
        gbc_nameLabel.insets = new Insets(0, 0, 0, 5);
        gbc_nameLabel.gridx = 2;
        gbc_nameLabel.gridy = 0;
        add(nameLabel, gbc_nameLabel);

        valueLabel = new JLabel("0");
        GridBagConstraints gbc_valueLabel = new GridBagConstraints();
        gbc_valueLabel.anchor = GridBagConstraints.EAST;
        gbc_valueLabel.insets = new Insets(0, 0, 0, 5);
        gbc_valueLabel.gridx = 3;
        gbc_valueLabel.gridy = 0;
        add(valueLabel, gbc_valueLabel);

        JButton setValue = new JButton("Set");
        setValue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(
                    ValueDisplayComponent.this,
                    "Please input the new value for " + nameV,
                    Integer.toString(getValue())
                );
                try {
                    int i = Integer.parseInt(s);
                    if (i < 0) {
                        JOptionPane.showMessageDialog(
                            ValueDisplayComponent.this,
                            "The value " + i + " is negative!",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        setValue(i);
                        refresh();
                    }
                } catch (Exception t) {
                    JOptionPane.showMessageDialog(
                        ValueDisplayComponent.this,
                        "The value \"" + s + "\" is not a number!",
                        "Error!",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        GridBagConstraints gbc_setValue = new GridBagConstraints();
        gbc_setValue.gridx = 4;
        gbc_setValue.gridy = 0;
        add(setValue, gbc_setValue);
    }

    public abstract int getValue();
    public abstract void setValue(int i);
    public abstract boolean hasValueBeenRead();
    public abstract boolean hasValueBeenChanged();
}
