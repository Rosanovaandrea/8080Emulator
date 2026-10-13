package org.example;

//java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar target/8080Emulator-1.0-SNAPSHOT.jar
// comando per raccogliere  i metadati in esecuzione per la build nativa

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
