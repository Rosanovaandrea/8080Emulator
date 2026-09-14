package org.example;

public class Machine {
      private CoreEmulator emulator;
      private int inutBus;
      private int outputBus;
      private final int inOpcode = 0xdb;
      private final int outOpcode = 0xd3;
      private int shiftRegister = 0;
      private byte offset = 0;

      public void execution (){

            while (true){
                  int opcode = emulator.getOpcode();

                  switch (opcode) {
                        case inOpcode:
                              //inoperations
                              emulator.nextOpceAfetHw();
                              break;
                        case outOpcode:
                              //outputoperations
                              emulator.nextOpceAfetHw();
                              break;
                        default:
                              emulator.emulationProcess();
                        }

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
