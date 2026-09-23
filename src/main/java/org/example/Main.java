package org.example;

public class Main {
    public static void main(String[] args)
             {
        CoreEmulator emulator = new CoreEmulator();
        Machine machine = new Machine();
        machine.inizializeEmulatorRam();
        machine.setEmulator(emulator);
                 try {
                     machine.execution();
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }

             }
}
