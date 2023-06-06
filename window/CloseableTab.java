package window;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import editor.Canvas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseableTab extends JPanel {
    private TabManager tabManager;
    private Canvas canvas;
    private JLabel titleLabel;

    public CloseableTab(TabManager manager, Canvas canvas) {
        super();
        this.tabManager = manager;
        this.canvas = canvas;
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
        this.titleLabel = new JLabel() {
            public String getText() {
                int i = tabManager.indexOfComponent(canvas);
                if (i != -1) {
                    return tabManager.getTitleAt(i);
                }
                return null;
            }
        };
        this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        this.add(this.titleLabel);
        this.add(new CloseButton());
    }

    @Override
    public String getName() {
        return this.titleLabel.getText();
    }

    private class CloseButton extends JButton {
        private static final int SIZE = 20;

        public CloseButton() {
            this.setPreferredSize(new Dimension(SIZE, SIZE));
            this.setContentAreaFilled(false);
            this.setFocusable(false);
            this.setRolloverEnabled(true);
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    int i = tabManager.indexOfComponent(canvas);
                    if (i != -1) {
                        tabManager.remove(i);
                    }
                }
            });
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            // shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta, getHeight() - delta);
            g2.drawLine(getWidth() - delta, delta, delta, getHeight() - delta);
        }
    }
}
