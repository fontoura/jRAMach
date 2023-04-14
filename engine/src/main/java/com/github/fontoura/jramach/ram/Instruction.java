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

package com.github.fontoura.jramach.ram;

import java.util.ArrayList;
import java.util.List;

import com.github.fontoura.jramach.ram.exceptions.IllegalInstructionArgumentException;
import com.github.fontoura.jramach.ram.exceptions.IllegalMachineOperationException;
import com.github.fontoura.jramach.ram.exceptions.MissingInstructionArgumentException;

public class Instruction {
    public static class Builder {
        private InstructionType instructionType;
        private List<String> labels = new ArrayList<String>();
        private List<InstructionArgument> arguments = new ArrayList<InstructionArgument>();

        public Builder() {}

        public Builder instructionType(InstructionType instructionType) {
            this.instructionType = instructionType;
            return this;
        }

        public Builder label(String label) {
            labels.add(label);
            return this;
        }

        public Builder argument(InstructionArgument argument) throws IllegalInstructionArgumentException {
            if (arguments.size() >= instructionType.getArgumentCount()) {
                throw new IllegalInstructionArgumentException(instructionType, arguments.size(), argument, "The " + instructionType.getMnemonic().toUpperCase() + " instruction takes " + instructionType.getArgumentCount() + " arguments!");
            }
            instructionType.validateArgument(arguments.size(), argument);
            arguments.add(argument);
            return this;
        }

        public Instruction build() throws MissingInstructionArgumentException {
            if (arguments.size() < instructionType.getArgumentCount()) {

            }
            return new Instruction(instructionType, labels, arguments);
        }
    }

    private InstructionType instructionType;
    private List<String> labels;
    private List<InstructionArgument> arguments;

    public Instruction(InstructionType instructionType, List<String> labels, List<InstructionArgument> arguments) {
        this.instructionType = instructionType;
        this.labels = labels;
        this.arguments = arguments;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<InstructionArgument> getArguments() {
        return arguments;
    }

    public void execute(Machine machine) throws IllegalMachineOperationException {
        instructionType.execute(machine, arguments);
    }
}
