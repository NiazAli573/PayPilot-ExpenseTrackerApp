package com.paypilot.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * ReceiptViewerDialog - Image viewer with zoom functionality
 * Displays receipt images with pan and zoom support
 */
public class ReceiptViewerDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private BufferedImage originalImage;
    private JLabel imageLabel;
    private JScrollPane scrollPane;
    private double zoomLevel = 1.0;
    private static final double ZOOM_STEP = 0.2;
    
    public ReceiptViewerDialog(Window parent, String imagePath) {
        super(parent, "Receipt Viewer", ModalityType.APPLICATION_MODAL);
        
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new Exception("Receipt file not found");
            }
            originalImage = ImageIO.read(imageFile);
            
            initializeUI();
            setSize(800, 600);
            setLocationRelativeTo(parent);
            
        } catch (Exception e) {
            UITheme.showMessage(parent, "Failed to load receipt: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_COLOR);
        
        // Toolbar
        JPanel toolbarPanel = createToolbar();
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);
        
        // Image panel
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        updateImageDisplay();
        
        scrollPane = new JScrollPane(imageLabel);
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add mouse wheel zoom
        scrollPane.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });
        
        setContentPane(mainPanel);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JButton zoomInBtn = UITheme.createSecondaryButton("🔍 +");
        zoomInBtn.setToolTipText("Zoom In (Ctrl + Mouse Wheel)");
        zoomInBtn.addActionListener(e -> zoomIn());
        
        JButton zoomOutBtn = UITheme.createSecondaryButton("🔍 -");
        zoomOutBtn.setToolTipText("Zoom Out");
        zoomOutBtn.addActionListener(e -> zoomOut());
        
        JButton fitBtn = UITheme.createSecondaryButton("Fit");
        fitBtn.setToolTipText("Fit to Window");
        fitBtn.addActionListener(e -> fitToWindow());
        
        JButton actualSizeBtn = UITheme.createSecondaryButton("100%");
        actualSizeBtn.setToolTipText("Actual Size");
        actualSizeBtn.addActionListener(e -> actualSize());
        
        JLabel zoomLabel = new JLabel(String.format("%.0f%%", zoomLevel * 100));
        zoomLabel.setFont(UITheme.FONT_REGULAR);
        zoomLabel.setForeground(UITheme.TEXT_MUTED);
        
        leftPanel.add(zoomInBtn);
        leftPanel.add(zoomOutBtn);
        leftPanel.add(fitBtn);
        leftPanel.add(actualSizeBtn);
        leftPanel.add(zoomLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(closeBtn);
        
        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private void zoomIn() {
        zoomLevel += ZOOM_STEP;
        if (zoomLevel > 5.0) zoomLevel = 5.0;
        updateImageDisplay();
    }
    
    private void zoomOut() {
        zoomLevel -= ZOOM_STEP;
        if (zoomLevel < 0.1) zoomLevel = 0.1;
        updateImageDisplay();
    }
    
    private void fitToWindow() {
        int viewportWidth = scrollPane.getViewport().getWidth();
        int viewportHeight = scrollPane.getViewport().getHeight();
        
        double widthRatio = (double) viewportWidth / originalImage.getWidth();
        double heightRatio = (double) viewportHeight / originalImage.getHeight();
        
        zoomLevel = Math.min(widthRatio, heightRatio) * 0.95; // 95% to add padding
        updateImageDisplay();
    }
    
    private void actualSize() {
        zoomLevel = 1.0;
        updateImageDisplay();
    }
    
    private void updateImageDisplay() {
        int newWidth = (int) (originalImage.getWidth() * zoomLevel);
        int newHeight = (int) (originalImage.getHeight() * zoomLevel);
        
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        
        // Update zoom label in toolbar
        Component[] components = ((JPanel)((JPanel)getContentPane().getComponent(0))
            .getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(String.format("%.0f%%", zoomLevel * 100));
                break;
            }
        }
    }
}
