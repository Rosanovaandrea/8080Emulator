package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Monitor {

    @FunctionalInterface
    public interface KeyHandler {
        void handle(boolean down);
    }

    private Machine machine;

    private static final int SCREEN_W  = 224;
    private static final int SCREEN_H  = 256;
    private static final int VRAM_BASE = 0x2400;
    private static final int VRAM_COL  = 32;
    private static final int SCALE     = 3;

    private static final int ON  = 0xEDE8D0;
    private static final int OFF = 0x002b36;

    private final BufferedImage image =
            new BufferedImage(SCREEN_W, SCREEN_H, BufferedImage.TYPE_INT_RGB);
    private final JFrame frame;
    private final JPanel panel;

    // Handler: ricevono true = down, false = up
    private KeyHandler onLeft   = down -> {
        if(down)  machine.machineLeftDown(); else  machine.machineLeftUp();
    };
    private KeyHandler onRight  = down -> {
        if(down)  machine.machineRightDown(); else machine.machineRIghtUp();
    };
    private KeyHandler onFire   = down -> {
        if(down)  machine.machineFireDown(); else machine.machineFireUp();
    };
    private KeyHandler onCoin   = down -> {
        if(down)  machine.machineCoinDown(); else machine.machineCoinUp();
    };
    private KeyHandler onStart1 = down -> {
        if(down) machine.machineStartDown(); else machine.machineStartUp();
    };
    private KeyHandler onStart2 = down -> {
        if(down) machine.machineStartDown(); else machine.machineStartUp();
    };

    public Monitor(Machine machine) {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, SCREEN_W * SCALE, SCREEN_H * SCALE, null);
            }
        };
        panel.setPreferredSize(new Dimension(SCREEN_W * SCALE, SCREEN_H * SCALE));
        panel.setBackground(Color.BLACK);
        panel.setDoubleBuffered(true);
        panel.setFocusable(true);

        panel.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { dispatch(e.getKeyCode(), true);  }
            @Override public void keyReleased(KeyEvent e) { dispatch(e.getKeyCode(), false); }
        });

        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.requestFocusInWindow();
        this.machine = machine;
    }

    private void dispatch(int keyCode, boolean down) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:  onLeft.handle(down);   break;
            case KeyEvent.VK_RIGHT: onRight.handle(down);  break;
            case KeyEvent.VK_SPACE: onFire.handle(down);   break;
            case KeyEvent.VK_5:     onCoin.handle(down);   break;
            case KeyEvent.VK_1:     onStart1.handle(down); break;
            case KeyEvent.VK_2:     onStart2.handle(down); break;
        }
    }

    public void update() {
        for (int y = 0; y < SCREEN_H; y++) {
            int yVram   = 255 - y;
            int byteRow = yVram / 8;
            int bit     = yVram % 8;
            for (int x = 0; x < SCREEN_W; x++) {
                int idx = VRAM_BASE + x * VRAM_COL + byteRow;
                boolean on = ((CoreEmulator.RAM[idx] >> bit) & 1) != 0;
                image.setRGB(x, y, on ? ON : OFF);
            }
        }
        panel.repaint();
    }

    // --- Setter ---

    public void setOnLeft(KeyHandler h)   { onLeft = h; }
    public void setOnRight(KeyHandler h)  { onRight = h; }
    public void setOnFire(KeyHandler h)   { onFire = h; }
    public void setOnCoin(KeyHandler h)   { onCoin = h; }
    public void setOnStart1(KeyHandler h) { onStart1 = h; }
    public void setOnStart2(KeyHandler h) { onStart2 = h; }
}

