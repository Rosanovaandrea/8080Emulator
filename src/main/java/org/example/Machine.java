package org.example;

import java.io.IOException;

public class Machine {
      private CoreEmulator emulator;
      private volatile int inputBus;
      private volatile int inputBus2;
      private int outputBus;
      private final int inOpcode = 0xdb;
      private final int outOpcode = 0xd3;
      private int shiftRegister = 0;
      private byte offset = 0;
      private static final int MAX_OP = 4167; // per stare largo 5000, la media è 4166
      private int currentnumberOp;
      private boolean rtsNumber = false;
      public int[] RAM;
      Monitor monitor;

      public void inizializeEmulatorRam() {
            int pointer = 0;
            pointer = CoreEmulator.initializeRAM(ROMContainer.H,pointer);
            pointer = CoreEmulator.initializeRAM(ROMContainer.G,pointer);
            pointer = CoreEmulator.initializeRAM(ROMContainer.F,pointer);
            pointer = CoreEmulator.initializeRAM(ROMContainer.E,pointer);
      }

      public void setEmulator(CoreEmulator emulator){
            this.emulator = emulator;
      }

      public Machine(){
            currentnumberOp = 0;
            RAM = CoreEmulator.RAM;
            monitor = new Monitor(this);
            inputBus = 0;
            outputBus = 0;
            inputBus2 = 0;
      }

      public void execution () throws InterruptedException {

            while (true){

                  currentnumberOp++;



                  int opcode = emulator.getOpcode();


                  if(currentnumberOp > MAX_OP){

                        int rtsNumberInt =  rtsNumber ? 2 : 1;
                        rtsNumber = !rtsNumber;
                        currentnumberOp = 0;
                        emulator.rstEmulation(rtsNumberInt);

                        if(rtsNumber){
                              monitor.update();
                              Thread.sleep(16);
                        }
                        continue;
                  }



                  switch (opcode) {
                        case inOpcode:
                              inputHnadler();
                              emulator.nextOpceAfetHw();
                              break;
                        case outOpcode:
                              outputHandler();
                              emulator.nextOpceAfetHw();
                              break;
                        default:
                              emulator.emulationProcess();
                        }

            }


            }

      public void inputHnadler(){

            switch(emulator.getOpcodeData()){
                  case 3:
                        emulator.setA(getShiftRegisterWithOffset());
                        break;
                  case 1:
                        emulator.setA(inputBus);
                        break;
                  case 2:
                        inputBus2 = inputBus2 & 0x78;
                        emulator.setA(inputBus2);
                        break;
                  default:
                        throw new RuntimeException("Unknown port inpuit");

            }
      }


       public void outputHandler(){

            switch (emulator.getOpcodeData()) {
                  case 2:
                        setOffset(emulator.getA());
                        break;
                  case 3:
                        // not yet implemented
                        break;
                  case 4:
                        setShiftRegister(emulator.getA());
                        break;
                  case 5:
                        //not yet implemented
                        break;
                  case 6:
                        //not yet implemented
                        break;
                  default:
                        throw new RuntimeException("porta in output non riconosciuta " + emulator.getOpcodeData());
            }
       }

      public void machineCoinDown(){
            int code = 0xfe;
            inputBus = code & inputBus;
      }

      public void machineCoinUp(){
            int code = 0x01;
            inputBus = code | inputBus;
      }

       public void machineFireDown(){
            int code = 0x10;
            inputBus = code | inputBus;
            inputBus2 = code | inputBus2;
       }

      public void machineFireUp(){
            int code = 0xef;
            inputBus = code & inputBus;
            inputBus2 = code & inputBus2;
      }

      public void machineStartDown(){
            int code = 0x06;
            inputBus = code | inputBus;

      }

      public void machineStartUp(){
            int code = 0xf9;
            inputBus = code & inputBus;
      }

      public void machineLeftDown(){
            int code = 0x20;
            inputBus = code | inputBus;
            inputBus2 = code | inputBus2;
      }

      public void machineLeftUp(){
            int code = 0xdf;
            inputBus = code & inputBus;
            inputBus2 = code & inputBus2;
      }

      public void machineRightDown(){
            int code = 0x40;
            inputBus = code | inputBus;
            inputBus2 = code | inputBus2;
      }

      public void machineRIghtUp(){
            int code = 0xbf;
            inputBus = code & inputBus;
            inputBus2 = code & inputBus2;
      }








      public void setShiftRegister(int data){
            data = data & 0xff;
            shiftRegister = shiftRegister >>> 8 | data << 8;
      }

      public void setOffset(int data){
            offset = (byte) (data & 0x07);
      }

      public int getShiftRegisterWithOffset(){
            return (shiftRegister >>> (8 - offset)) & 0xff;
      }



}
