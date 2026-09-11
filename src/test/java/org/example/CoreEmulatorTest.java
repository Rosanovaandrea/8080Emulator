package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoreEmulatorTest {

    @Test
    void noValidOpTry() {
        CoreEmulator emulator = new CoreEmulator();
        int[] ram = {0xff};
        CoreEmulator.setRAM(ram);
        Exception exception = assertThrows(RuntimeException.class, () -> {emulator.emulationProcess();});
        assertEquals("opcode non gestito e riconosciuto",exception.getMessage());
    }

    @Test
    void noRamOpTry() {
        CoreEmulator emulator = new CoreEmulator();
        CoreEmulator.setRAM(null);
        Exception exception = assertThrows(NullPointerException.class, () -> {emulator.emulationProcess();});
        assertEquals("ram non impostata",exception.getMessage());
    }

    @Test
    void indexPcOutOfBoundTry() {
        CoreEmulator emulator = new CoreEmulator();
        int[] ram = {0xff};
        CoreEmulator.setRAM(ram);
        emulator.setPc(1000);
        Exception exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {emulator.emulationProcess();});
        assertEquals("il program counter punta ad una locazione non presente",exception.getMessage());
    }


}