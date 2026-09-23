package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CoreEmulator {

    public static  int[] RAM = new int[65536];

    //8 bit registers emulators
    private int a,b,c,d,e,h,l;

    //16 bit register emolators
    private int bc,hl,de,sp,pc;

    //processor flags
    private boolean z,s,p,cy,ac;


    private int temp, mask;


    public static int initializeRAM(short[] data, int startWrite) {
        int pointer = startWrite;
        int i = 0;

        for(; i < data.length; i++){
            RAM[i + pointer] = (int) data[i] & 0xFF;
        }

        return pointer + i;
    }

    // solo a scopo di test
    public static void setRAM(int[] newRAM){
        RAM = newRAM;
    }

    public int getOpcode(){
        return RAM[pc];
    }

    public int getOpcodeData(){
        return RAM[pc + 1];
    }

    public void nextOpceAfetHw(){
        pc += 2;
    }

    public void rstEmulation(int interruptnumber){

        RAM[sp-1] = (pc & 0xff00) >>> 8;
        RAM[sp-2] = pc & 0xff;
        sp -= 2;

        pc = interruptnumber * 8;

    }



    public void emulationProcess(){

        int opcode;

        try {

             opcode = RAM[pc];

        }catch (NullPointerException e){

            throw new NullPointerException("ram non impostata");

        }catch (ArrayIndexOutOfBoundsException e){

            throw new ArrayIndexOutOfBoundsException("il program counter punta ad una locazione non presente");

        }

        switch (opcode) {


            case 0X00:
                pc++;
                break;
            case 0x01:
                b = RAM[pc + 2];
                c = RAM[pc + 1];
                pc += 3;
                break;
            case 0x02:
                temp = (b << 8 | c);
                RAM[temp] = a;
                pc++;
                break;
            case 0x03:
                bc = (b << 8 | c);
                bc++;
                bc = bc & 0xffff;
                c = bc & 0xff;
                b = bc >>> 8;
                pc++;
                break;
            case 0X04:
                ac = (b & 0x0f) == 0x0f;
                b = (b + 1) & 0xff;
                s = (b >>> 7) != 0;
                z = b == 0;
                p = (Integer.bitCount(b) % 2) == 0;
                pc++;
                break;
            case 0x05:
                ac = (b & 0x0f) == 0x00;
                b = (b - 1) & 0xff;
                s = (b >>> 7) != 0;
                z = b == 0;
                p = (Integer.bitCount(b) % 2) == 0;
                pc++;
                break;
            case 0x06:
                b = RAM[pc + 1];
                pc += 2;
                break;
            case 0x07:
                cy = (a >>> 7) == 1;
                a = a >>> 7 | a << 1;
                a = a & 0xFF;
                pc++;
                break;
            case 0x08:
                pc++;
                break;
            case 0x09:
                hl = h << 8 | l;
                bc = b << 8 | c;
                hl = hl + bc;
                cy = hl > 0xFFFF;
                hl = hl & 0xFFFF;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x0a:
                bc = b << 8 | c;
                a = RAM[bc];
                pc++;
                break;
            case 0x0b:
                bc = b << 8 | c;
                bc--;
                bc = bc & 0xFFFF;
                b = bc >>> 8;
                c = bc & 0xff;
                pc++;
                break;
            case 0x0c:
                ac = (c & 0x0f) == 0x0f;
                c = (c + 1) & 0xff;
                s = (c >>> 7) != 0;
                z = c == 0;
                p = Integer.bitCount(c) % 2 == 0;
                pc++;
                break;
            case 0x0d:
                ac = (c & 0x0f) == 0x00;
                c = (c - 1) & 0xff;
                s = (c >>> 7) != 0;
                z = c == 0;
                p = Integer.bitCount(c) % 2 == 0;
                pc++;
                break;
            case 0x0e:
                c = RAM[pc + 1];
                pc += 2;
                break;
            case 0x0f:
                cy = (a & 0x01) == 1;
                a = (a >>> 1 | a << 7) & 0xff;
                pc++;
                break;
            case 0x10:
                pc++;
                break;
            case 0x11:
                e = RAM[pc + 1];
                d = RAM[pc + 2];
                pc += 3;
                break;
            case 0x12:
                de = (d << 8 | e) & 0xffff;
                RAM[de] = a;
                pc++;
                break;
            case 0x13:
                de = d << 8 | e;
                de++;
                de = de & 0xffff;
                d = de >>> 8;
                e = de & 0xff;
                pc++;
                break;
            case 0x14:
                ac = (d & 0x0f) == 0x0f;
                d = (d + 1) & 0xff;
                s = (d >>> 7) != 0;
                z = d == 0;
                p = Integer.bitCount(d) % 2 == 0;
                pc++;
                break;
            case 0x15:
                ac = (d & 0x0f) == 0x00;
                d = (d - 1) & 0xff;
                s = (d >>> 7) != 0;
                z = d == 0;
                p = Integer.bitCount(d) % 2 == 0;
                pc++;
                break;
            case 0x16:
                d = RAM[pc + 1];
                pc += 2;
                break;
            case 0x17:
                temp = cy ? 0x01 : 0x00;
                cy = (a >>> 7) == 1;
                a = (a << 1 | temp) & 0xff;
                pc++;
                break;
            case 0x18:
                pc++;
                break;
            case 0x19:
                hl = h << 8 | l;
                de = d << 8 | e;
                hl = hl + de;
                cy = hl > 0xFFFF;
                hl = hl & 0xFFFF;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x1a:
                de = (d << 8) | e;
                a = RAM[de];
                pc++;
                break;
            case 0x1b:
                de = (d << 8) | e;
                de--;
                d = (de >>> 8) & 0xFF;
                e = de & 0xFF;
                pc++;
                break;
            case 0x1c:
                ac = (e & 0x0F) == 0x0F;
                e = (e + 1) & 0xff;
                z = e == 0;
                p = (Integer.bitCount(e) & 0x01) == 0;
                s = (e >>> 7) != 0;
                pc++;
                break;
            case 0x1d:
                ac = (e & 0x0F) == 0x00;
                e = (e - 1) & 0xff;
                z = e == 0;
                p = (Integer.bitCount(e) & 0x01) == 0;
                s = (e >>> 7) != 0;
                pc++;
                break;
            case 0x1e:
                e = RAM[pc + 1];
                pc += 2;
                break;
            case 0x1f:
                temp = cy ? 0x80 : 0x00;
                cy = (a & 0x01) == 0x01;
                a = (a >>> 1) | temp;
                a = a & 0xFF;
                pc++;
                break;
            case 0x20:
                pc++;
                break;
            case 0x21:
                h = RAM[pc + 2];
                l = RAM[pc + 1];
                pc += 3;
                break;
            case 0x22:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                RAM[temp] = l;
                RAM[temp + 1] = h;
                pc += 3;
                break;
            case 0x23:
                hl = h << 8 | l & 0xff;
                hl = (hl + 1) & 0xffff;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x26:
                temp = RAM[pc + 1];
                h = temp;
                pc += 2;
                break;
            case 0x27:
                pc ++;
                break;
            case 0x29:
                hl = h << 8 | l & 0xff;
                hl = hl * 2;
                cy = hl > 0xFFFF;
                hl = hl & 0xFFFF;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x2a:
                temp = RAM[pc + 1] & 0xff | (RAM[pc + 2] & 0xff) << 8;
                l = RAM[temp] & 0xff;
                h = RAM[temp + 1] & 0xff;
                pc+=3;
                break;
            case 0x2b:
                hl = h << 8 | l & 0xff;
                hl--;
                hl = hl & 0xFFFF;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x2c:
                ac = (l & 0x0F) == 0x0f;
                l = (l + 1) & 0xff;
                z = l == 0;
                p = (Integer.bitCount(l) & 0x01) == 0;
                s = (l >>> 7) != 0;
                pc++;
                break;
            case 0x2e:
                l = RAM[pc + 1] & 0xff;
                pc+=2;
                break;
            case 0x2f:
                a = ~a;
                pc++;
                break;
            case 0x31:
                sp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                pc += 3;
                break;
            case 0x32:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                RAM[temp] = a & 0xff;
                pc += 3;
                break;
            case 0x34:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                ac = (temp & 0x0f) == 0x0f;
                temp = (temp + 1) & 0xff;
                s = (temp >>> 7) != 0;
                z = temp == 0;
                p = (Integer.bitCount(temp) % 2) == 0;
                RAM[hl] = temp;
                pc++;
                break;
            case 0x35:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl];
                ac = (temp & 0x0f) == 0x00;
                temp = (temp - 1) & 0xff;
                s = (temp >>> 7) != 0;
                z = temp == 0;
                p = (Integer.bitCount(temp) % 2) == 0;
                RAM[hl] = temp;
                pc++;
                break;
            case 0x36:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[pc + 1] & 0xff;
                RAM[hl] = temp;
                pc += 2;
                break;
            case 0x37:
                cy = true;
                pc++;
                break;
            case 0x3a:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                a = RAM[temp] & 0xff;
                pc += 3;
                break;
            case 0x3c:
                ac = (a & 0x0f) == 0x0f;
                a = a + 1;
                a = a & 0xff;
                z = a == 0;
                p = (Integer.bitCount(a) % 2) == 0;
                s = (a >>> 7) != 0;
                pc++;
                break;
            case 0x3d:
                ac = (a & 0x0f) == 0x00;
                a = a - 1;
                a = a & 0xff;
                z = a == 0;
                p = (Integer.bitCount(a) % 2) == 0;
                s = (a >>> 7) != 0;
                pc++;
                break;
            case 0x3e:
                a = RAM[pc + 1] & 0xff;
                pc += 2;
                break;
            case 0x41:
                b = c & 0xff;
                pc++;
                break;
            case 0x42:
                b = d & 0xff;
                pc++;
                break;
            case 0x43:
                b = e & 0xff;
                pc++;
                break;
            case 0x44:
                b = h & 0xff;
                pc++;
                break;
            case 0x45:
                b = l & 0xff;
                pc++;
                break;
            case 0x46:
                hl = (h & 0xff) << 8 | l & 0xff;
                b = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x47:
                b = a & 0xff;
                pc++;
                break;
            case 0x4e:
                hl = (h & 0xff) << 8 | l & 0xff;
                c = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x4f:
                c = a;
                pc++;
                break;
            case 0x56:
                hl = (h & 0xff) << 8 | l & 0xff;
                d = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x57:
                d = a & 0xff;
                pc++;
                break;
            case 0x5e:
                hl = (h & 0xff) << 8 | l & 0xff;
                e = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x5f:
                e = a & 0xff;
                pc++;
                break;
            case 0x61:
                h = c & 0xff;
                pc++;
                break;
            case 0x62:
                h = d & 0xff;
                pc++;
                break;
            case 0x63:
                h = e & 0xff;
                pc++;
                break;
            case 0x64:
                h = h & 0xff;
                pc++;
                break;
            case 0x65:
                h = l & 0xff;
                pc++;
                break;
            case 0x66:
                hl = (h & 0xff) << 8 | l & 0xff;
                h = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x67:
                h = a & 0xff;
                pc++;
                break;
            case 0x68:
                l = b & 0xff;
                pc++;
                break;
            case 0x69:
                l = c & 0xff;
                pc++;
                break;
            case 0x6f:
                l = a;
                pc++;
                break;
            case 0x70:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = b;
                pc++;
                break;
            case 0x71:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = c;
                pc++;
                break;
            case 0x72:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = d;
                pc++;
                break;
            case 0x73:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = e;
                pc++;
                break;
            case 0x74:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = h;
                pc++;
                break;
            case 0x75:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = l;
                pc++;
            case 0x76:
                //HLT instruction
                throw new RuntimeException("HLT RAGGIUNTO: "+Integer.toHexString(opcode));
            case 0x77:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = a & 0xff;
                pc++;
                break;
            case 0x78:
                a = b;
                pc++;
                break;
            case 0x79:
                a = c;
                pc++;
                break;
            case 0x7a:
                a = d;
                pc++;
                break;
            case 0x7b:
                a = e;
                pc++;
                break;
            case 0x7c:
                a = h;
                pc++;
                break;
            case 0x7d:
                a = l;
                pc++;
                break;
            case 0x7e:
                hl = (h & 0xff) << 8 | l & 0xff;
                a = RAM[hl] & 0xff;
                pc++;
                break;
            case 0x80:
                ac = (~a & 0x0f) < (b & 0x0f);
                cy = (~a & 0xff) < b;
                a = a + b;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x81:
                ac = (~a & 0x0f) < (c & 0x0f);
                cy = (~a & 0xff) < c;
                a = a + c;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x82:
                ac = (~a & 0x0f) < (d & 0x0f);
                cy = (~a & 0xff) < d;
                a = a + d;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x83:
                ac = (~a & 0x0f) < (e & 0x0f);
                cy = (~a & 0xff) < e;
                a = a + e;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x84:
                ac = (~a & 0x0f) < (h & 0x0f);
                cy = (~a & 0xff) < h;
                a = a + h;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x85:
                ac = (~a & 0x0f) < (l & 0x0f);
                cy = (~a & 0xff) < l;
                a = a + l;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x86:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                ac = (~a & 0x0f) < (temp & 0x0f);
                cy = (~a & 0xff) < temp;
                a = a + temp;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x87:
                ac = (~a & 0x0f) < (a & 0x0f);
                cy = (~a & 0xff) < a;
                a = a + a;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x90:
                ac = (a & 0x0f) < (b & 0x0f);
                cy = (a & 0xff) < b;
                a = a - b;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x91:
                ac = (a & 0x0f) < (c & 0x0f);
                cy = (a & 0xff) < c;
                a = a - c;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x92:
                ac = (a & 0x0f) < (d & 0x0f);
                cy = (a & 0xff) < d;
                a = a - d;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x93:
                ac = (a & 0x0f) < (e & 0x0f);
                cy = (a & 0xff) < e;
                a = a - e;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x94:
                ac = (a & 0x0f) < (h & 0x0f);
                cy = (a & 0xff) < h;
                a = a - h;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x95:
                ac = (a & 0x0f) < (l & 0x0f);
                cy = (a & 0xff) < l;
                a = a - l;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x96:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                ac = (a & 0x0f) < (temp & 0x0f);
                cy = (a & 0xff) < temp;
                a = a - temp;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0x97:
                ac = (a & 0x0f) < (a & 0x0f);
                cy = (a & 0xff) < a;
                a = a - a;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc ++;
                break;
            case 0xa0:
                cy = false;
                a = a & b;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xa6:
                cy = false;
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                a = a & temp;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xa7:
                cy = false;
                a = a & a;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xa8:
                cy = false;
                a = (a & 0xff) ^ (b & 0xff);
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xaf:
                cy = false;
                a = a ^ a;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xb0:
                cy = false;
                a = (a & 0xff) | (b & 0xff);
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xb4:
                cy = false;
                a = (a & 0xff) | (h & 0xff);
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xb6:
                cy = false;
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                a = (a & 0xff) | (temp & 0xff);
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xb8:
                ac = (a & 0x0f) < (b & 0x0f);
                cy = (a & 0xff) < b;
                temp = a - b;
                temp = temp & 0xff;
                z = temp == 0;
                p = Integer.bitCount(temp) % 2 == 0;
                s = temp >>> 7 != 0;
                pc ++;
                break;
            case 0xbc:
                ac = (a & 0x0f) < (h & 0x0f);
                cy = (a & 0xff) < h;
                temp = a - h;
                temp = temp & 0xff;
                z = temp == 0;
                p = Integer.bitCount(temp) % 2 == 0;
                s = temp >>> 7 != 0;
                pc ++;
                break;
            case 0xbe:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[hl] & 0xff;
                ac = (a & 0x0f) < (temp & 0x0f);
                cy = (a & 0xff) < temp;
                temp = a - temp;
                temp = temp & 0xff;
                z = temp == 0;
                p = Integer.bitCount(temp) % 2 == 0;
                s = temp >>> 7 != 0;
                pc ++;
                break;
            case 0xe3:
                l =  l ^ RAM[sp];
                RAM[sp] =  RAM[sp] ^ l;
                l = l ^ RAM[sp];
                h = h ^ RAM[sp + 1];
                RAM[sp + 1] = RAM[sp + 1] ^ h;
                h = h ^ RAM[sp + 1];
                pc++;
                break;
            case 0xe9:
                pc = (h & 0xff) << 8 | (l & 0xff);
                break;
            case 0xc0:
                temp = RAM[sp] & 0xff;
                temp = temp | ((RAM[sp + 1] & 0xff) << 8);
                sp = z ? sp : sp + 2;
                pc = z ? (pc + 1) : temp ;
                break;
            case 0xc1:
                c = RAM[sp] & 0xff;
                b = RAM[sp + 1] & 0xff;
                sp += 2;
                pc++;
                break;
            case 0xc2:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                mask = z ? 0 : -1;
                pc = temp & mask | (pc + 3) & ~mask;
                break;
            case 0xc3:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                pc = temp;
                break;
            case 0xc4:
                mask = z ? 0 : -1;
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                RAM[sp - 2] = mask  & ((pc + 3) & 0xff) | RAM[sp - 2] & ~mask;
                RAM[sp - 1] =mask  & (((pc + 3) >>> 8) & 0xff) | RAM[sp - 1] & ~mask;
                pc = mask & (temp & 0xFFFF) | (pc + 3) & ~mask;
                sp = ((sp - 2) & 0xffff) & mask | sp & ~mask;
                break;
            case 0xc5:
                RAM[sp - 1] = b;
                RAM[sp - 2] = c;
                sp -= 2;
                pc++;
                break;
            case 0xc6:
                temp = RAM[pc + 1] & 0xff;
                ac = (~a & 0x0f) < (temp & 0x0f);
                a = a + temp;
                cy = a > 0xff;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc += 2;
                break;
            case 0xc8:
                temp = RAM[sp] & 0xff;
                temp = temp | ((RAM[sp + 1] & 0xff) << 8);
                sp = z ? sp + 2 : sp;
                pc = z ? temp : (pc + 1);
                break;
            case 0xc9:
                pc = RAM[sp] & 0xff;
                pc = pc | ((RAM[sp + 1] & 0xff) << 8);
                sp += 2;
                break;
            case 0xca:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                mask = z ? -1 : 0;
                pc = temp & mask | (pc + 3) & ~mask;
                break;
            case 0xcc:
                mask = z ? -1 : 0;
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                RAM[sp - 2] = mask  & ((pc + 3) & 0xff) | RAM[sp - 2] & ~mask;
                RAM[sp - 1] =mask  & (((pc + 3) >>> 8) & 0xff) | RAM[sp - 1] & ~mask;
                pc = mask & (temp & 0xFFFF) | (pc + 3) & ~mask;
                sp = ((sp - 2) & 0xffff) & mask | sp & ~mask;
                break;
            case 0xcd:
                RAM[sp - 1] = (pc + 3) >>> 8;
                RAM[sp - 2] = (pc + 3) & 0xff;
                sp -= 2;
                pc = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                break;
            case 0xd0:
                temp = (RAM[sp + 1] & 0xff) << 8 | RAM[sp] & 0xff;
                mask = cy ? 0 : -1;
                sp = (sp + 2) & mask | sp & ~mask;
                pc = temp & mask | (pc + 1) & ~mask;
                break;
            case 0xd1:
                e = RAM[sp] & 0xff;
                d = RAM[sp + 1] & 0xff;
                sp += 2;
                pc++;
                break;
            case 0xd2:
                temp = ((RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff);
                mask = cy ? 0 : -1;
                pc =  ((pc + 3) & ~mask) | (temp & mask);
                break;
            case 0xd3:
                pc += 2;
                break;
            case 0xd4:
                mask = cy ? 0 : -1;
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                RAM[sp - 2] = mask  & ((pc + 3) & 0xff) | RAM[sp - 2] & ~mask;
                RAM[sp - 1] =mask  & (((pc + 3) >>> 8) & 0xff) | RAM[sp - 1] & ~mask;
                pc = mask & (temp & 0xFFFF) | (pc + 3) & ~mask;
                sp = ((sp - 2) & 0xffff) & mask | sp & ~mask;
                break;
            case 0xd5:
                RAM[sp - 2] = e;
                RAM[sp - 1] = d;
                sp -= 2;
                pc++;
                break;
            case 0xd6:
                temp = RAM[pc + 1] & 0xff;
                ac = (a & 0x0f) < (temp & 0x0f);
                cy = (a & 0xff) < temp;
                a = a - temp;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc += 2;
                break;
            case 0xd8:
                temp = (RAM[sp + 1] & 0xff) << 8 | RAM[sp] & 0xff;
                mask = cy ? -1 : 0;
                sp = (sp + 2) & mask | sp & ~mask;
                pc = temp & mask | (pc + 1) & ~mask;
                break;
            case 0xda:
                temp = ((RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff);
                mask = cy ? -1 : 0;
                pc =  ((pc + 3) & ~mask) | (temp & mask);
                break;
            case 0xde:
                mask = cy ? 1 : 0;
                temp = RAM[pc + 1] & 0xff;
                ac = (a & 0x0f) - (temp & 0x0f) - mask < 0;
                a = a - temp - mask;
                cy = a < 0;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc += 2;
                break;
            case 0xe1:
                l = RAM[sp] & 0xff;
                h = RAM[sp + 1] & 0xff;
                sp += 2;
                pc++;
                break;
            case 0xe5:
                RAM[sp - 2] = l;
                RAM[sp - 1] = h;
                sp -= 2;
                pc++;
                break;
            case 0xe6:
                temp = RAM[pc + 1] & 0xff;
                cy = false;
                a = a & temp;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc += 2;
                break;
            case 0xeb:
                h = h ^ d;
                d = d ^ h;
                h = h ^ d;
                l = l ^ e;
                e = e ^ l;
                l = l ^ e;
                pc++;
                break;
            case 0xf1:
                cy = (RAM[sp] & 0x01) == 1;
                p = (RAM[sp] >>> 2 & 0x01) == 1;
                ac = (RAM[sp] >>> 4 & 0x01) == 1;
                z = (RAM[sp] >>> 6 & 0x01) == 1;
                s = (RAM[sp] >>> 7 & 0x01) == 1;
                a = RAM[sp + 1];
                sp += 2;
                pc++;
                break;
            case 0xf5:
                temp = 0;
                temp = temp | (cy ? 0x01 : 0x00);
                temp = temp | 0x01 << 1;
                temp = temp | (p ? 0x01 : 0x00) << 2;
                temp = temp | (ac ? 0x01 : 0x00) << 4;
                temp = temp | (z ? 0x01 : 0x00) << 6;
                temp = temp | (s ? 0x01 : 0x00) << 7;
                RAM[sp - 2] = temp;
                RAM[sp - 1] = a;
                sp -= 2;
                pc++;
                break;
            case 0xf6:
                temp = RAM[pc + 1] & 0xff;
                ac = false;
                cy = false;
                a = a | temp;
                a = a & 0xff;
                z = a == 0;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc += 2;
                break;
            case 0xfa:
                temp = (RAM[pc + 2] & 0xff) << 8 | (RAM[pc + 1] & 0xff);
                mask = s ? -1 : 0;
                pc = (temp & mask) | ((pc + 3) & ~mask);
                break;
            case 0xfb:
                pc++;
                break;
            case 0xfe:
                temp = RAM[pc + 1] & 0xff;
                ac = (a & 0x0f) < (temp & 0x0f);
                cy = (a & 0xff) < temp;
                temp = a - temp;
                temp = temp & 0xff;
                z = temp == 0;
                p = Integer.bitCount(temp) % 2 == 0;
                s = temp >>> 7 != 0;
                pc += 2;
                break;
            default:
                throw new RuntimeException("opcode non gestito e riconosciuto: "+Integer.toHexString(opcode));

            }
        }

    public CoreEmulator(){
        a = b = c = e = h = l = sp = pc = bc = hl = temp = mask = 0;
        z = s = p = cy = ac = false;
    }

    //metodi da utilizzare solo nei test :

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getE() {
        return e;
    }

    public void setE(int e) {
        this.e = e;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getBc() {
        return bc;
    }

    public void setBc(int bc) {
        this.bc = bc;
    }

    public int getHl() {
        return hl;
    }

    public void setHl(int hl) {
        this.hl = hl;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public boolean isZ() {
        return z;
    }

    public void setZ(boolean z) {
        this.z = z;
    }

    public boolean isS() {
        return s;
    }

    public void setS(boolean s) {
        this.s = s;
    }

    public boolean isP() {
        return p;
    }

    public void setP(boolean p) {
        this.p = p;
    }

    public boolean isCy() {
        return cy;
    }

    public void setCy(boolean cy) {
        this.cy = cy;
    }

    public boolean isAc() {
        return ac;
    }

    public void setAc(boolean ac) {
        this.ac = ac;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }




}
