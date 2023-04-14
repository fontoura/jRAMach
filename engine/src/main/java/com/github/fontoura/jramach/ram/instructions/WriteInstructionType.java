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

package com.github.fontoura.jramach.ram.instructions;

import java.util.List;

import com.github.fontoura.jramach.ram.InstructionArgument;
import com.github.fontoura.jramach.ram.Machine;
import com.github.fontoura.jramach.ram.exceptions.IllegalInstructionArgumentException;
import com.github.fontoura.jramach.ram.exceptions.IllegalMachineOperationException;

public class WriteInstructionType extends AbstractInstructionType {
    public static final String MNEMONIC = "write";

    @Override
    public String getMnemonic() {
        return MNEMONIC;
    }

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public void validateArgument(int argumentIndex, InstructionArgument argument) throws IllegalInstructionArgumentException {
        mustBeLiteralOrRegister(argumentIndex, argument);
    }

    @Override
    public void execute(Machine machine, List<InstructionArgument> arguments) throws IllegalMachineOperationException {
        InstructionArgument argument = arguments.get(0);
        int tapeAddress = machine.resolveArgumentValue(argument);
        try {
            machine.setTape(tapeAddress, machine.getAccumulator());
        } catch (IllegalArgumentException e) {
            throw new IllegalMachineOperationException(this, machine.getProgramCounter(), "Attempted to write to out-of-bounds tape item.");
        }
    }
}
