# Java implementation of a Random-Access Machine (RAM)

This project is an implementation of a Random-Access Machine that is loosely based on the Cook and Reckhow model.

The software was implemented back in 2014 and has since been adapted to use Maven as its build tool.

## Operation

The machine consists of several registers and a tape with a fixed length. The first register (`REG[0]`) is the accumulator (`ACC`). All registers hold natural numbers, which are non-negative integers. Any operation that would typically result in a negative number will instead yield zero.

Labels may be placed before commands or on their own, and must always be followed by a colon (:).

Single-line comments must be preceded by a semicolon (;).

The current implementation supports the following instructions:

| Name                             | Syntax            | Effect                                                   |
|----------------------------------|-------------------|----------------------------------------------------------|
| Halt program                     | `HALT`            | Stops program execution                                  |
| No operation                     | `NOP`             | No effect                                                |
| Load value into accumulator      | `LOAD <value>`    | `ACC = value`                                            |
| Store accumulator into register  | `STORE R<number>` | `REG[number] = ACC`                                      |
| Add value to accumulator         | `ADD <value>`     | `ACC = ACC + value`                                      |
| Substract value from accumulator | `SUB <value>`     | `ACC = ACC - value`                                      |
| Halve accumulator                | `HALF`            | `ACC = ACC / 2`                                          |
| Read from tape on given address  | `READ <value>`    | `ACC = TAPE[value]`                                      |
| Write to tape on given address   | `WRITE <value>`   | `TAPE[value] = ACC`                                      |
| Jump to position                 | `JUMP <label>`    | Jump to the instruction with label `label`               |
| Jump to position if not zero     | `JPOS <label>`    | Jump to the instruction with label `label` if `ACC != 0` |
| Jump to position if zero         | `JZERO <label>`   | Jump to the instruction with label `label` if `ACC == 0` |

The values referred to in the syntax of certain instructions can be either registers, indicated by an `R` followed by a number (e.g. `R2`), or numeric literals (e.g. `22`).

## Sample code

The following code is an implementation of the bubblesort algorithm. The length of the list is stored at address `0`, immediately followed by the list itself which starts at address `1`.

```
LOAD 1
STORE R1
READ 0
STORE R2
label1: LOAD R1
ADD 1
SUB R2
JPOS label4
label2: LOAD R1
ADD 1
STORE R3
READ R1
STORE R4
READ R3
STORE R5
ADD 1
SUB R4
JPOS label3
LOAD R4
WRITE R3
LOAD R5
WRITE R1
label3: LOAD R1
ADD 1
STORE R1
ADD 1
SUB R2
JZERO label2
LOAD 1
STORE R1
LOAD R2
SUB 1
STORE R2
JUMP label1
label4: HALT
```

## Build

To generate a runnable JAR with all dependencies bundled (a fat runnable JAR), use the following command:

```
mvn clean package
```

After executing the above command, the compiled JAR will be available at `gui/target`.

## License

This code has been released under [the MIT license](LICENSE).
