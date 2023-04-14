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

import com.github.fontoura.jramach.ram.exceptions.IllegalMachineOperationException;

public class Machine {
    private int[] tape;
    private int[] registers;

    private Instruction[] program;
    private int programCounter;

    private boolean shouldIncrementProgramCounter;
    private boolean halted;

    private boolean accumulatorHasBeenRead;
    private int lastReadRegisterNumber;
    private int lastReadTapeAddress;
    private boolean accumulatorHasBeenChanged;
    private int lastChangedRegisterNumber;
    private int lastChangedTapeAddress;
    private boolean runningInstruction;

    public Machine(int tapeLength, int registerCount) {
        if (tapeLength < 0) {
            throw new IllegalArgumentException("The tape length must not be negative!");
        }
        if (registerCount < 1) {
            throw new IllegalArgumentException("There must be at least one register!");
        }

        accumulatorHasBeenRead = false;
        lastReadRegisterNumber = -1;
        lastReadTapeAddress = -1;
        accumulatorHasBeenChanged = false;
        lastChangedRegisterNumber = -1;
        lastChangedTapeAddress = -1;

        tape = new int[tapeLength];
        registers = new int[registerCount + 1];

        programCounter = 1;
        runningInstruction = false;
    }

    public void setProgram(Instruction[] program) {
        this.program = program;
    }

    public void nextInstruction() {
        accumulatorHasBeenRead = false;
        lastReadRegisterNumber = -1;
        lastReadTapeAddress = -1;
        accumulatorHasBeenChanged = false;
        lastChangedRegisterNumber = -1;
        lastChangedTapeAddress = -1;

        try {
            runningInstruction = true;
            shouldIncrementProgramCounter = true;
            if (halted) {
                return;
            }
            if (programCounter > program.length) {
                halted = true;
                programCounter = 1;
            } else {
                try {
                    program[programCounter - 1].execute(this);
                    if (shouldIncrementProgramCounter) {
                        programCounter ++;
                    }
                    if (programCounter > program.length) {
                        halted = true;
                        programCounter = 1;
                    }
                } catch (IllegalMachineOperationException e) {
                    e.printStackTrace();
                    halted = true;
                }
            }
        } finally {
            runningInstruction = false;
        }
    }

    public int getAccumulator() {
        if (runningInstruction) {
            accumulatorHasBeenRead = true;
        }
        return registers[0];
    }

    public int getRegister(int registerNumber) {
        if (runningInstruction) {
            lastReadRegisterNumber = registerNumber;
        }
        return registers[registerNumber];
    }

    public int getTape(int tapeAddress) {
        if (tapeAddress >= tape.length) {
            throw new IllegalArgumentException("Out of boundaries!");
        }
        if (runningInstruction) {
            lastReadTapeAddress = tapeAddress;
        }
        return tape[tapeAddress];
    }

    public void setAccumulator(int value) {
        accumulatorHasBeenChanged = true;
        registers[0] = value;
    }

    public void setRegister(int registerNumber, int value) {
        lastChangedRegisterNumber = registerNumber;
        registers[registerNumber] = value;
    }

    public void setTape(int tapeAddress, int value) {
        if (tapeAddress >= tape.length) {
            throw new IllegalArgumentException("Out of boundaries!");
        }
        lastChangedTapeAddress = tapeAddress;
        tape[tapeAddress] = value;
    }

    public void setProgramCounter(int instructionAddress) {
        shouldIncrementProgramCounter = false;
        programCounter = instructionAddress;
    }

    public void halt() {
        programCounter = 1;
        shouldIncrementProgramCounter = false;
        halted = true;
    }

    public boolean isHalted() {
        return halted;
    }

    public int getTapeLength() {
        return tape.length;
    }

    public int getRegisterCount() {
        return registers.length;
    }

    public Instruction getInstruction(int instructionAddress) {
        return program[instructionAddress - 1];
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int codeLength() {
        return program.length;
    }

    public int resolveArgumentValue(InstructionArgument argument) {
        if (argument instanceof InstructionArgument.Literal) {
            return ((InstructionArgument.Literal)argument).getValue();
        } else if (argument instanceof InstructionArgument.Register) {
            return getRegister(((InstructionArgument.Register)argument).getNumber());
        } else if (argument instanceof InstructionArgument.Label) {
            return ((InstructionArgument.Label)argument).getPosition();
        } else {
            throw new UnsupportedOperationException("Could not resolve value of argument " + argument);
        }
    }

    public void reset() {
        programCounter = 1;
        halted = false;
        for (int i = 0; i < registers.length; i ++) {
            registers[i] = 0;
        }

        accumulatorHasBeenRead = false;
        lastReadRegisterNumber = -1;
        lastReadTapeAddress = -1;
        accumulatorHasBeenChanged = false;
        lastChangedRegisterNumber = -1;
        lastChangedTapeAddress = -1;
    }

    public boolean hasAccumulatorBeenRead() {
        return accumulatorHasBeenRead;
    }

    public boolean hasRegisterBeenRead(int registerNumber) {
        return registerNumber == lastReadRegisterNumber;
    }

    public boolean hasTapeBeenRead(int tapeAddress) {
        return tapeAddress == lastReadTapeAddress;
    }

    public boolean hasAccumulatorBeenChanged() {
        return accumulatorHasBeenChanged;
    }

    public boolean hasRegisterBeenChanged(int registerNumber) {
        return registerNumber == lastChangedRegisterNumber;
    }

    public boolean hasTapeBeenChanged(int tapeAddress) {
        return tapeAddress == lastChangedTapeAddress;
    }

    public boolean isRunningInstruction() {
        return runningInstruction;
    }
}
