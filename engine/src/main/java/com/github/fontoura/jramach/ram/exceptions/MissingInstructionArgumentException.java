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

package com.github.fontoura.jramach.ram.exceptions;

import com.github.fontoura.jramach.ram.InstructionType;

public class MissingInstructionArgumentException extends Exception {
    private final InstructionType instructionType;
    private final int expectedArguments;
    private final int providedArguments;

    public MissingInstructionArgumentException(InstructionType instructionType, int expectedArguments, int providedArguments) {
        super("Instruction " + instructionType.getMnemonic().toUpperCase() + " should have " + expectedArguments + " argument" + (expectedArguments != 1 ? "s" : "") + " but should have " + providedArguments + ".");
        this.instructionType = instructionType;
        this.expectedArguments = expectedArguments;
        this.providedArguments = providedArguments;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public int getExpectedArguments() {
        return expectedArguments;
    }

    public int getProvidedArguments() {
        return providedArguments;
    }
}
