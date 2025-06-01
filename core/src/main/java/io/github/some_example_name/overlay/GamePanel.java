package io.github.some_example_name.overlay;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private int leben = 3;
    private int punkte = 0;
    private int level = 1;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        // Beispiel: Lebenspunkte reduzieren alle 2 Sekunden
        Timer timer = new Timer(2000, e -> {
            leben = Math.max(0, leben - 1);
            punkte += 100;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Spielgrafik hier (wenn nötig)

        drawOverlay(g);
    }

    private void drawOverlay(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Transparenter Hintergrund
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), 40);

        // Textstil
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);

        // Overlay-Infos
        g2.drawString("Leben: " + leben, 20, 25);
        g2.drawString("Punkte: " + punkte, 150, 25);
        g2.drawString("Level: " + level, 300, 25);
    }
}
