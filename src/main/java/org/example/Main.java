package org.example;

public class Main {
    static void main() {
        CoreEmulator emulator = new CoreEmulator();
        CoreEmulator.resetRam();
        Machine machine = new Machine();
        machine.inizializeEmulatorRam();
        machine.setEmulator(emulator);
        machine.execution();

    }
}
