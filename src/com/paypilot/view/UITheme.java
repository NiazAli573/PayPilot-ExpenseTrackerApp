package com.paypilot.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class UITheme {

    // --- Professional Modern Palette ---
    public static final Color BG_COLOR = new Color(245, 247, 250); // Soft Grey Background
    public static final Color SIDEBAR_COLOR = new Color(26, 32, 44); // Professional Dark Blue
    public static final Color CARD_BG = Color.WHITE;
    
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Professional Blue
    public static final Color PRIMARY_HOVER = new Color(37, 99, 235);
    public static final Color ACCENT_COLOR = new Color(139, 92, 246); // Purple Accent
    
    public static final Color TEXT_DARK = new Color(17, 24, 39);
    public static final Color TEXT_MUTED = new Color(107, 114, 128);
    public static final Color BORDER_COLOR = new Color(229, 231, 235);

    public static final Color SUCCESS = new Color(16, 185, 129); // Green
    public static final Color WARNING = new Color(245, 158, 11); // Orange
    public static final Color DANGER = new Color(239, 68, 68); // Red
    public static final Color INFO = new Color(59, 130, 246); // Blue

    // Fonts - Professional & Readable (Roboto)
    public static final Font FONT_TITLE = new Font("Roboto", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Roboto", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Roboto", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Roboto", Font.BOLD, 14);
    public static final Font FONT_HUGE = new Font("Roboto", Font.BOLD, 36);
    public static final Font FONT_SMALL = new Font("Roboto", Font.PLAIN, 12);

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Global defaults to remove "Old Java" feel
            UIManager.put("Panel.background", BG_COLOR);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", TEXT_DARK);
            UIManager.put("Button.arc", 12); // Rounding for some L&Fs
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- CUSTOM ROUNDED PANEL (The "Card" Look) ---
    public static class RoundedPanel extends JPanel {
        private int radius = 15;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false); // Crucial for rounded corners
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Subtle Border
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
        }
    }

    // --- CUSTOM MODERN BUTTON ---
    public static class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color textColor;

        public ModernButton(String text, Color normal, Color hover, Color textCol) {
            super(text);
            this.normalColor = normal;
            this.hoverColor = hover;
            this.textColor = textCol;
            
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(textColor);
            setFont(FONT_BOLD);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(normalColor); repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(normalColor);
            }
            // Rounded Button
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g);
        }
    }

    // --- Factories ---

    public static JButton createPrimaryButton(String text) {
        return new ModernButton(text, PRIMARY_COLOR, PRIMARY_HOVER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        // White button with gray text and slight gray hover
        return new ModernButton(text, Color.WHITE, new Color(243, 244, 246), TEXT_DARK);
    }

    public static JButton createDangerButton(String text) {
        return new ModernButton(text, DANGER, DANGER.darker(), Color.WHITE);
    }
    
    public static JButton createSidebarButton(String text) {
        // Transparent background, hover effect is slightly lighter blue
        ModernButton btn = new ModernButton(text, SIDEBAR_COLOR, new Color(31, 41, 55), new Color(209, 213, 219));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return btn;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(45); // Taller rows for modern feel
        table.setFont(FONT_REGULAR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(PRIMARY_COLOR);
        
        // Header Styling
        if (table.getTableHeader() != null) {
            table.getTableHeader().setFont(FONT_BOLD);
            table.getTableHeader().setBackground(new Color(249, 250, 251)); // Very light gray header
            table.getTableHeader().setForeground(TEXT_MUTED);
            table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
            table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        }
        
        // Cell Padding
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 15, 0, 15)); // Left/Right padding
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    public static void showMessage(Component parent, String msg, String title, int type) {
        JOptionPane.showMessageDialog(parent, msg, title, type);
    }
    
    public static int showConfirm(Component parent, String msg, String title) {
        return JOptionPane.showConfirmDialog(parent, msg, title, JOptionPane.YES_NO_OPTION);
    }
}