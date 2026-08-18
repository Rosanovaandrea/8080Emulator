package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CoreEmulator {

    private static final int[] RAM = new int[65536];

    //8 bit registers emulators
    private int a,b,c,d,e,h,l;

    //16 bit register emolators
    private int bc,hl,de,sp,pc;

    //processor flags
    private boolean z,s,p,cy,ac;


    private int temp, mask;

    public static void initializeRAM(String[] files) throws IOException {
        int pointer = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);

        for (String file : files) {
            try (RandomAccessFile reader = new RandomAccessFile(file, "r");
                 FileChannel channel = reader.getChannel()) {
                while (channel.read(byteBuffer) != -1) {
                    byteBuffer.flip();

                    while (byteBuffer.hasRemaining()) {
                        if (pointer < RAM.length) {
                            RAM[pointer++] = byteBuffer.get() & 0xff;
                        }
                    }
                    byteBuffer.clear();
                }
            }
        }
    }

    public CoreEmulator(){
        a = b = c = e = h = l = sp = pc = bc = hl = temp = mask = 0;
        z = s = p = cy = ac = false;
    }

    public void emulationProcess(){

        int opcode = RAM[pc];


        switch (opcode){
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
                pc ++;
                break;
            case 0x03:
                bc = (b << 8 | c);
                bc ++;
                bc = bc & 0xffff;
                c = bc & 0xff;
                b = bc >>> 8;
                pc++;
                break;
            case 0X04:
                ac = (b & 0x0f) == 0x0f;
                b = (b + 1)& 0xff;
                s = (b >>> 7) != 0;
                z = b == 0;
                p = (Integer.bitCount(b) % 2) == 0;
                pc++;
                break;
            case 0x05:
                ac = (b & 0x0f) == 0x00;
                b = (b - 1) & 0xff;
                s = (b >>> 7)  != 0;
                z = b == 0;
                p = (Integer.bitCount(b) % 2) == 0;
                pc++;
                break;
            case 0x06:
                b = RAM[pc+1];
                pc +=2;
                break;
            case 0x07:
                cy = (a >>> 7) == 1;
                a =  a >>> 7 | a << 1;
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
                c = (c-1) & 0xff;
                s = (c >>> 7) != 0;
                z = c == 0;
                p = Integer.bitCount(c) % 2 == 0;
                pc++;
                break;
            case 0x0e:
                c = RAM[pc+1];
                pc +=2;
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
                e = RAM[pc+1];
                d = RAM[pc+2];
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
                d = RAM[pc+1];
                pc += 2;
                break;
            case 0x17:
                temp =  cy ? 0x01: 0x00;
                cy = (a >>> 7) == 1;
                a =  (a << 1 | temp) & 0xff;
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
                de --;
                d = (de >>> 8) & 0xFF;
                e = de & 0xFF;
                pc++;
                break;
            case 0x1c:
                ac = (e & 0xFF) == 0xFF;
                e = (e + 1) & 0xff;
                z = e == 0;
                p = (Integer.bitCount(e) & 0x01) == 0;
                s = (e >>> 7) != 0;
                pc++;
                break;
            case 0x1d:
                ac = (e & 0xFF) == 0x00;
                e = (e - 1) & 0xff;
                z = e == 0;
                p = (Integer.bitCount(e) & 0x01) == 0;
                s = (e >>> 7) != 0;
                pc++;
                break;
            case 0x1e:
                e = RAM[pc+1];
                pc +=2;
                break;
            case 0x1f:
                cy = (a & 0x01) == 0x01;
                temp = a & 0x80;
                a = temp | a >>> 1;
                a = a & 0xff;
                pc++;
                break;
            case 0x20:
                pc++;
                break;
            case 0x21:
                h = RAM[pc+2];
                l  = RAM[pc+1];
                pc += 3;
                break;
            case 0x22:
                temp = (RAM[pc+2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                RAM[temp] = l;
                RAM[temp +1] = h;
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
            case 0x29:
                hl = h << 8 | l & 0xff;
                hl = hl * 2;
                cy = hl > 0xFFFF;
                hl = hl & 0xFFFF;
                h = hl >>> 8;
                l = hl & 0xff;
                pc++;
                break;
            case 0x31:
                sp = (RAM[pc+2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                pc += 3;
                break;
            case 0x32:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                RAM[temp] = a & 0xff;
                pc += 3;
                break;
            case 0x36:
                hl = (h & 0xff) << 8 | l & 0xff;
                temp = RAM[pc+1] & 0xff;
                RAM[hl] = temp;
                pc += 2;
                break;
            case 0x3a:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc + 1] & 0xff;
                a = RAM[temp] & 0xff;
                pc+=3;
                break;
            case 0x3e:
                a = RAM[pc+1] & 0xff;
                pc += 2;
                break;
            case 0x56:
                hl = (h & 0xff) << 8 | l & 0xff;
                d = RAM[hl] & 0xff;
                pc ++;
                break;
            case 0x5e:
                hl = (h & 0xff) << 8 | l & 0xff;
                e = RAM[hl] & 0xff;
                pc ++;
                break;
            case 0x66:
                hl = (h & 0xff) << 8 | l & 0xff;
                h = RAM[hl] & 0xff;
                pc ++;
                break;
            case 0x6f:
                l = a;
                pc++;
                break;
            case 0x77:
                hl = (h & 0xff) << 8 | l & 0xff;
                RAM[hl] = c & 0xff;
                pc ++;
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
            case 0x7e:
                hl = (h & 0xff) << 8 | l & 0xff;
                a = RAM[hl] & 0xff;
                pc ++;
                break;
            case 0xa7:
                a = a & a;
                z = a == 0;
                ac = true;
                cy = false;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xaf:
                a = a ^ a;
                z = a == 0;
                ac = false;
                cy = false;
                p = Integer.bitCount(a) % 2 == 0;
                s = a >>> 7 != 0;
                pc++;
                break;
            case 0xc1:
                c = RAM[sp] & 0xff;
                b = RAM[sp + 1] & 0xff;
                sp += 2;
                pc++;
                break;
            case 0xc2:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                mask = z ? 0 : -1;
                pc = temp & mask | (pc + 3) & ~mask;
                break;
            case 0xc3:
                temp = (RAM[pc + 2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                pc = temp;
                break;
            case 0xc5:
                RAM[sp-1] = b;
                RAM[sp-2] = c;
                sp -= 2;
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
            case 0xc9:
                pc = RAM[sp] & 0xff;
                pc = pc | ( (RAM[sp + 1] & 0xff) << 8);
                sp += 2;
                break;
            case 0xcd:
                RAM[sp-2] =  (pc + 3) >>> 8;
                RAM[sp-1] =  (pc + 3) & 0xff;
                sp -= 2;
                pc = (RAM[pc + 2] & 0xff) << 8 | RAM[pc+1] & 0xff;
                break;
            case 0xd1:
                e = RAM[sp] & 0xff;
                d = RAM[sp + 1] & 0xff;
                sp += 2;
                pc++;
                break;
            case 0xd3:
                pc += 2;
                break;
            case 0xd5:
                RAM[sp-2] = e;
                RAM[sp-1] = d;
                sp -= 2;
                pc++;
                break;
            case 0xe1:
                l = RAM[sp] & 0xff;
                h = RAM[sp + 1] & 0xff;
                sp += 2;
                pc ++;
                break;
            case 0xe5:
                RAM[sp-2] = l;
                RAM[sp-1] = h;
                sp -= 2;
                pc++;
                break;
            case 0xe6:
                temp = RAM[pc + 1] & 0xff;
                cy = false;
                ac = true;
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


        }

    }
}
