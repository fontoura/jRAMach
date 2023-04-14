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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.github.fontoura.jramach.ram.exceptions.IllegalInstructionArgumentException;
import com.github.fontoura.jramach.ram.exceptions.MissingInstructionArgumentException;
import com.github.fontoura.jramach.ram.instructions.AddInstructionType;
import com.github.fontoura.jramach.ram.instructions.HalfInstructionType;
import com.github.fontoura.jramach.ram.instructions.HaltInstructionType;
import com.github.fontoura.jramach.ram.instructions.JposInstructionType;
import com.github.fontoura.jramach.ram.instructions.JumpInstructionType;
import com.github.fontoura.jramach.ram.instructions.JzeroInstructionType;
import com.github.fontoura.jramach.ram.instructions.LoadInstructionType;
import com.github.fontoura.jramach.ram.instructions.NopInstructionType;
import com.github.fontoura.jramach.ram.instructions.ReadInstructionType;
import com.github.fontoura.jramach.ram.instructions.StoreInstructionType;
import com.github.fontoura.jramach.ram.instructions.SubInstructionType;
import com.github.fontoura.jramach.ram.instructions.WriteInstructionType;

public class Assembler {
    private static InstructionType[] knownInstructionTypes = new InstructionType[] {
        new NopInstructionType(),

        new AddInstructionType(),
        new SubInstructionType(),

        new HalfInstructionType(),

        new LoadInstructionType(),
        new StoreInstructionType(),

        new ReadInstructionType(),
        new WriteInstructionType(),

        new JzeroInstructionType(),
        new JposInstructionType(),
        new JumpInstructionType(),

        new HaltInstructionType(),
    };

    private static InstructionType lookForMnemonic(String mnemonic) {
        for (int i = 0; i < knownInstructionTypes.length; i ++) {
            if (knownInstructionTypes[i].getMnemonic().equals(mnemonic)) {
                return knownInstructionTypes[i];
            }
        }
        return null;
    }

    public static Instruction[] compile(String code) {
        String[] codeSplit = code.split("\n");
        Map<String, Integer> labelToCommandNumber = new TreeMap<String, Integer>();
        Map<Integer, List<String>> commandNumberToLabel = new TreeMap<Integer, List<String>>();

        // first pass
        //  - discover labels
        //  - skip comments
        int commandNumber = 0;
        for (int lineIndex = 0; lineIndex < codeSplit.length; lineIndex ++) {
            String codeLine = codeSplit[lineIndex].trim().toLowerCase();

            // skip comments.
            int indexOfComment = codeLine.indexOf(';');
            if (indexOfComment >= 0) {
                codeLine = codeLine.substring(0, indexOfComment).trim();
            }

            // read labels.
            int indexOfColon = codeLine.indexOf(':');
            while (indexOfColon >= 0) {
                String label = codeLine.substring(0, indexOfColon).trim();
                if (label.length() > 0) {
                    int jumpPosition = commandNumber + 1;
                    if (labelToCommandNumber.containsKey(label)) {
                        if (labelToCommandNumber.get(label) != jumpPosition) {
                            throw new IllegalArgumentException("Label " + label + " appears more than once! First at line " + labelToCommandNumber.get(label) + " and then at line " + (lineIndex + 1) + ".");
                        } else {
                            // label appeart twice on same line. ignore.
                        }
                    }
                    labelToCommandNumber.put(label, jumpPosition);

                    List<String> labelsOfLine = commandNumberToLabel.get(jumpPosition);
                    if (labelsOfLine == null) {
                        labelsOfLine = new ArrayList<String>();
                        commandNumberToLabel.put(jumpPosition, labelsOfLine);
                    }
                    labelsOfLine.add(label);
                }
                codeLine = codeLine.substring(indexOfColon + 1).trim();
                indexOfColon = codeLine.indexOf(':');
            }

            if (codeLine.length() > 0) {
                codeSplit[lineIndex] = codeLine;
                commandNumber ++;
            } else {
                codeSplit[lineIndex] = null;
            }
        }
        if (commandNumberToLabel.containsKey(commandNumber + 1)) {
            // ensure lingering labels have an associated instruction.
            codeSplit[codeSplit.length - 1] = HaltInstructionType.MNEMONIC;
            commandNumber ++;
        }
        int totalCommands = commandNumber;

        // create the compiled code as a list of instructions.
        Instruction[] instructions = new Instruction[totalCommands];
        commandNumber = 0;
        for (int lineIndex = 0; lineIndex < codeSplit.length; lineIndex ++) {
            String line = codeSplit[lineIndex];
            if (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                String mnemonic = tokenizer.nextToken();
                InstructionType instructionType = lookForMnemonic(mnemonic);
                if (instructionType == null) {
                    throw new IllegalArgumentException("Invalid mnemonic \"" + mnemonic + "\" at line " + (lineIndex + 1));
                }
                int argumentCount = instructionType.getArgumentCount();

                Instruction.Builder builder = new Instruction.Builder().instructionType(instructionType);

                List<String> labelsOfLine = commandNumberToLabel.get(commandNumber + 1);
                if (labelsOfLine != null) {
                    for (String label : labelsOfLine) {
                        builder.label(label);
                    }
                }

                for (int argumentIndex = 0; argumentIndex < argumentCount; argumentIndex ++) {
                    String argumentString = null;
                    try {
                        argumentString = tokenizer.nextToken();
                    } catch (NoSuchElementException e) {
                        throw new IllegalArgumentException("Instruction \"" + instructionType.getMnemonic() + "\" at line " + (lineIndex + 1) + " requires " + argumentCount + " arguments, but there are only " + argumentIndex + "!");
                    }
                    InstructionArgument value = null;

                    if (argumentString.startsWith("r")) {
                        int registerNumber;
                        try {
                            registerNumber = Integer.parseInt(argumentString.substring(1));
                        } catch(NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid argument " + argumentString + " at line " + (lineIndex + 1));
                        }
                        value = InstructionArgument.register(registerNumber);
                    } else if (Character.isLowerCase(argumentString.charAt(0))) {
                        if (!labelToCommandNumber.containsKey(argumentString)) {
                            throw new IllegalArgumentException("Invalid label \"" + argumentString + "\" at line " + (lineIndex + 1));
                        }
                        int jumpPosition = labelToCommandNumber.get(argumentString);
                        value = InstructionArgument.label(argumentString, jumpPosition);
                    } else if (argumentString.startsWith("0x")) {
                        int parsedHexadecimal;
                        try {
                            parsedHexadecimal = Integer.parseInt(argumentString.substring(2), 16);
                        } catch(NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid argument " + argumentString + " at line " + (lineIndex + 1));
                        }
                        value = InstructionArgument.literal(parsedHexadecimal);
                    } else if (argumentString.startsWith("0b")) {
                        int parsedBinary;
                        try {
                            parsedBinary = Integer.parseInt(argumentString.substring(2), 2);
                        } catch(NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid argument " + argumentString + " at line " + (lineIndex + 1));
                        }
                        value = InstructionArgument.literal(parsedBinary);
                    } else {
                        int parsedNumber;
                        try {
                            parsedNumber = Integer.parseInt(argumentString, 10);
                        } catch(NumberFormatException e) {
                            throw new IllegalArgumentException("Illegal argument " + value + " at line " + (lineIndex + 1));
                        }
                        value = InstructionArgument.literal(parsedNumber);
                    }
                    if (value instanceof InstructionArgument.Literal && ((InstructionArgument.Literal)value).getValue() < 0) {
                        throw new IllegalArgumentException("Negative literal value " + value + " at line " + (lineIndex + 1));
                    }
                    try {
                        builder.argument(value);
                    } catch (IllegalInstructionArgumentException e) {
                        throw new IllegalArgumentException("Unsupported value " + value + " at line " + (lineIndex + 1), e);
                    }
                }

                try {
                    instructions[commandNumber] = builder.build();
                } catch (MissingInstructionArgumentException e) {
                    throw new IllegalArgumentException("Illegal instruction \"" + line + "\" at line " + (lineIndex + 1), e);
                }
                commandNumber ++;
            }
            codeSplit[lineIndex] = null;
        }
        return instructions;
    }

    public static String decompile(Instruction[] i) {
        StringBuilder s = new StringBuilder();
        if (i.length > 0) {
            writeInstruction(s, i[0]);
            for (int j = 1; j < i.length; j ++) {
                s.append("\n");
                writeInstruction(s, i[j]);
            }
        }
        return s.toString();
    }

    private static void writeInstruction(StringBuilder builder, Instruction instruction) {
        for (String label : instruction.getLabels()) {
            builder.append(label).append(": ");
        }
        builder.append(instruction.getInstructionType().getMnemonic().toUpperCase());
        for (InstructionArgument argument : instruction.getArguments()) {
            builder.append(" ").append(argument.toString());
        }
    }
}
