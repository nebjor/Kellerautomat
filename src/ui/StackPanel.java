package ui;

import pda.EvaluationStep;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.util.Arrays;

public final class StackPanel extends JPanel {

    private static final int BOX_W = 180, BOX_H = 36, GAP = 8;
    private static final Color BG = new Color(248, 250, 252);
    private static final Color CELL_FILL = new Color(209, 250, 229);
    private static final Color CELL_ACTIVE = new Color(110, 231, 183);
    private static final Color CELL_BORDER = new Color(5, 150, 105);
    private static final Color OP_BG = new Color(254, 243, 199);
    private static final Color OP_BORDER = new Color(217, 119, 6);
    private static final Color TEXT_COLOR = new Color(17, 24, 39);
    private static final Color LABEL_COLOR = new Color(107, 114, 128);

    private int[] current = new int[0];
    private String lastToken = "";
    private String operation = "";
    private boolean isOp = false;
    private float highlight = 0f;

    private final Timer anim;

    public StackPanel() {
        setPreferredSize(new Dimension(320, 360));
        setBackground(BG);
        anim = new Timer(30, null);
        anim.addActionListener(e -> {
            highlight = Math.max(0f, highlight - 0.06f);
            if (highlight <= 0f) anim.stop();
            repaint();
        });
    }

    public void setSnapshot(int[] snap) {
        anim.stop();
        current = Arrays.copyOf(snap, snap.length);
        lastToken = "";
        operation = "";
        isOp = false;
        highlight = 0f;
        repaint();
    }

    public void setStep(EvaluationStep step) {
        anim.stop();
        int[] prev = current;
        current = step.stack();
        lastToken = step.token();
        isOp = "+".equals(lastToken) || "*".equals(lastToken);

        if (isOp && prev.length >= 2) {
            int a = prev[prev.length - 2];
            int b = prev[prev.length - 1];
            operation = a + " " + lastToken + " " + b + " = " + current[current.length - 1];
        } else {
            operation = "";
        }

        highlight = 1f;
        anim.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int bottomY = getHeight() - 24;
        int x = (getWidth() - BOX_W) / 2;

        g2.setColor(LABEL_COLOR);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
        g2.drawString("Keller / Stack", 16, 22);

        if (!lastToken.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
            g2.drawString("Token: " + lastToken, 16, 40);
        }

        if (!operation.isEmpty()) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
            g2.drawString(operation, 16, 58);
        }

        if (current.length == 0) {
            g2.setColor(LABEL_COLOR);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
            g2.drawString("Stack ist leer", x + 40, getHeight() / 2);
            return;
        }

        for (int i = 0; i < current.length; i++) {
            int y = bottomY - (i + 1) * BOX_H - i * GAP;
            boolean top = (i == current.length - 1);
            Color fill = top && highlight > 0f ? blend(CELL_FILL, CELL_ACTIVE, highlight) : CELL_FILL;

            g2.setColor(fill);
            g2.fillRoundRect(x, y, BOX_W, BOX_H, 10, 10);
            g2.setColor(CELL_BORDER);
            g2.drawRoundRect(x, y, BOX_W, BOX_H, 10, 10);

            g2.setColor(TEXT_COLOR);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            String text = String.valueOf(current[i]);
            int tw = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, x + (BOX_W - tw) / 2, y + 24);

            if (top && isOp && highlight > 0.3f) {
                int badgeW = 32, badgeH = 24;
                int bx = x + BOX_W + 8;
                int by = y + (BOX_H - badgeH) / 2;
                g2.setColor(OP_BG);
                g2.fillRoundRect(bx, by, badgeW, badgeH, 8, 8);
                g2.setColor(OP_BORDER);
                g2.drawRoundRect(bx, by, badgeW, badgeH, 8, 8);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
                int ow = g2.getFontMetrics().stringWidth(lastToken);
                g2.drawString(lastToken, bx + (badgeW - ow) / 2, by + 18);
            }
        }
    }

    private static Color blend(Color a, Color b, float t) {
        int r = a.getRed()   + (int)((b.getRed()   - a.getRed())   * t);
        int g = a.getGreen() + (int)((b.getGreen() - a.getGreen()) * t);
        int bl = a.getBlue() + (int)((b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
    }
}
