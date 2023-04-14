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

import com.github.fontoura.jramach.ram.Machine;

public class AccumulatorDisplayComponent extends ValueDisplayComponent {
    private static final long serialVersionUID = 1L;

    private Machine machine;

    public AccumulatorDisplayComponent(Machine machine) {
        super("Accumulator (register #0)");
        this.machine = machine;
        refresh();
    }

    @Override
    public int getValue() {
        return machine.getAccumulator();
    }

    @Override
    public void setValue(int v) {
        machine.setAccumulator(v);
    }

    @Override
    public boolean hasValueBeenRead() {
        return machine.hasAccumulatorBeenRead();
    }

    @Override
    public boolean hasValueBeenChanged() {
        return machine.hasAccumulatorBeenChanged();
    }
}
