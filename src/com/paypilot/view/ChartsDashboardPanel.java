package com.paypilot.view;

import com.paypilot.controller.ExpenseManager;
import com.paypilot.model.Expense;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * ChartsDashboardPanel - Visual analytics dashboard with charts
 * Displays pie chart, bar chart, and line chart for expense analysis
 */
public class ChartsDashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private String currentUser;
    private ExpenseManager expenseManager;
    private ArrayList<Expense> expenses;
    
    public ChartsDashboardPanel(String username, ExpenseManager expenseManager) {
        this.currentUser = username;
        this.expenseManager = expenseManager;
        this.expenses = new ArrayList<>();
        
        setBackground(UITheme.BG_COLOR);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initializeUI();
        refreshData();
    }
    
    private void initializeUI() {
        // Title
        JLabel titleLabel = new JLabel("📊 Expense Analytics");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 26));
        titleLabel.setForeground(UITheme.TEXT_DARK);
        add(titleLabel, BorderLayout.NORTH);
        
        // Charts grid
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        chartsPanel.setOpaque(false);
        
        // Chart 1: Pie Chart - Category Distribution
        chartsPanel.add(createChartCard("Category Distribution", createPieChart()));
        
        // Chart 2: Bar Chart - Top 5 Categories
        chartsPanel.add(createChartCard("Top 5 Categories", createBarChart()));
        
        // Chart 3: Line Chart - Monthly Trend
        chartsPanel.add(createChartCard("Monthly Spending Trend", createLineChart()));
        
        // Chart 4: Summary Stats
        chartsPanel.add(createSummaryCard());
        
        add(chartsPanel, BorderLayout.CENTER);
    }
    
    private UITheme.RoundedPanel createChartCard(String title, JPanel chartPanel) {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_BOLD);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        card.add(titleLabel, BorderLayout.NORTH);
        
        card.add(chartPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Create simple pie chart using custom painting
     */
    private JPanel createPieChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Map<String, Double> categoryTotals = getCategoryTotals();
                if (categoryTotals.isEmpty()) {
                    drawNoDataMessage(g2, "No expenses to display");
                    return;
                }
                
                double total = categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum();
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2 - 20;
                int radius = Math.min(getWidth(), getHeight()) / 3;
                
                double startAngle = 0;
                Color[] colors = {
                    new Color(99, 102, 241),   // Indigo
                    new Color(16, 185, 129),   // Green
                    new Color(239, 68, 68),    // Red
                    new Color(251, 191, 36),   // Yellow
                    new Color(139, 92, 246),   // Purple
                    new Color(236, 72, 153),   // Pink
                    new Color(20, 184, 166),   // Teal
                    new Color(249, 115, 22)    // Orange
                };
                
                int colorIndex = 0;
                int legendY = 20;
                
                for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                    double percentage = (entry.getValue() / total) * 100;
                    double arcAngle = (entry.getValue() / total) * 360;
                    
                    Color color = colors[colorIndex % colors.length];
                    g2.setColor(color);
                    g2.fillArc(centerX - radius, centerY - radius, 
                              radius * 2, radius * 2, 
                              (int) startAngle, (int) arcAngle);
                    
                    // Legend
                    g2.fillRect(10, legendY, 15, 15);
                    g2.setColor(UITheme.TEXT_DARK);
                    g2.setFont(new Font("Roboto", Font.PLAIN, 13));
                    g2.drawString(entry.getKey() + String.format(" (%.1f%%)", percentage), 
                                 30, legendY + 12);
                    
                    startAngle += arcAngle;
                    colorIndex++;
                    legendY += 25;
                }
            }
        };
    }
    
    /**
     * Create bar chart for top 5 categories
     */
    private JPanel createBarChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Map<String, Double> categoryTotals = getCategoryTotals();
                if (categoryTotals.isEmpty()) {
                    drawNoDataMessage(g2, "No expenses to display");
                    return;
                }
                
                // Get top 5 categories
                List<Map.Entry<String, Double>> topCategories = categoryTotals.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());
                
                double maxValue = topCategories.get(0).getValue();
                int barHeight = 30;
                int spacing = 15;
                int startY = 30;
                int maxBarWidth = getWidth() - 150;
                
                g2.setFont(new Font("Roboto", Font.PLAIN, 13));
                
                for (int i = 0; i < topCategories.size(); i++) {
                    Map.Entry<String, Double> entry = topCategories.get(i);
                    int barWidth = (int) ((entry.getValue() / maxValue) * maxBarWidth);
                    int y = startY + (i * (barHeight + spacing));
                    
                    // Draw bar
                    g2.setColor(UITheme.PRIMARY_COLOR);
                    g2.fillRoundRect(120, y, barWidth, barHeight, 8, 8);
                    
                    // Draw category name
                    g2.setColor(UITheme.TEXT_DARK);
                    g2.drawString(entry.getKey(), 10, y + 20);
                    
                    // Draw value
                    g2.drawString(String.format("$%.2f", entry.getValue()), 
                                 125 + barWidth, y + 20);
                }
            }
        };
    }
    
    /**
     * Create line chart for monthly trend (last 6 months)
     */
    private JPanel createLineChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Map<String, Double> monthlyTotals = getMonthlyTotals(6);
                if (monthlyTotals.isEmpty()) {
                    drawNoDataMessage(g2, "No expense history");
                    return;
                }
                
                int padding = 40;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;
                
                // Draw axes
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // X-axis
                g2.drawLine(padding, padding, padding, getHeight() - padding); // Y-axis
                
                // Get data points
                List<Map.Entry<String, Double>> dataPoints = new ArrayList<>(monthlyTotals.entrySet());
                if (dataPoints.isEmpty()) return;
                
                double maxValue = dataPoints.stream()
                    .mapToDouble(Map.Entry::getValue)
                    .max()
                    .orElse(1.0);
                
                int pointSpacing = width / (dataPoints.size() + 1);
                
                // Draw lines and points
                g2.setColor(UITheme.PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2));
                
                int prevX = 0, prevY = 0;
                for (int i = 0; i < dataPoints.size(); i++) {
                    Map.Entry<String, Double> entry = dataPoints.get(i);
                    int x = padding + (i + 1) * pointSpacing;
                    int y = getHeight() - padding - (int) ((entry.getValue() / maxValue) * height);
                    
                    // Draw line from previous point
                    if (i > 0) {
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    
                    // Draw point
                    g2.fillOval(x - 4, y - 4, 8, 8);
                    
                    // Draw month label
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(new Font("Roboto", Font.PLAIN, 10));
                    String monthLabel = entry.getKey().substring(5, 7); // Get month part
                    g2.drawString(monthLabel, x - 10, getHeight() - padding + 20);
                    
                    // Draw value
                    g2.setColor(UITheme.TEXT_DARK);
                    g2.setFont(new Font("Roboto", Font.BOLD, 10));
                    g2.drawString(String.format("$%.0f", entry.getValue()), x - 15, y - 10);
                    
                    g2.setColor(UITheme.PRIMARY_COLOR);
                    prevX = x;
                    prevY = y;
                }
            }
        };
    }
    
    /**
     * Create summary statistics card with dynamic updates
     */
    private UITheme.RoundedPanel createSummaryCard() {
        UITheme.RoundedPanel card = new UITheme.RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📊 Quick Stats");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel statsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Recalculate stats every time panel is repainted
                removeAll();
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBackground(Color.WHITE);
                
                double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
                double average = expenses.isEmpty() ? 0 : total / expenses.size();
                double max = expenses.stream().mapToDouble(Expense::getAmount).max().orElse(0);
                long count = expenses.size();
                
                add(createStatRow("💰 Total Expenses", String.format("$%.2f", total)));
                add(Box.createVerticalStrut(12));
                add(createStatRow("📊 Average", String.format("$%.2f", average)));
                add(Box.createVerticalStrut(12));
                add(createStatRow("📈 Highest", String.format("$%.2f", max)));
                add(Box.createVerticalStrut(12));
                add(createStatRow("🔢 Count", String.valueOf(count)));
                
                revalidate();
            }
        };
        statsPanel.setBackground(Color.WHITE);
        
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createStatRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Roboto", Font.PLAIN, 14));
        labelComp.setForeground(UITheme.TEXT_MUTED);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Roboto", Font.BOLD, 16));
        valueComp.setForeground(UITheme.PRIMARY_COLOR);
        
        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.EAST);
        
        return panel;
    }
    
    private void drawNoDataMessage(Graphics2D g2, String message) {
        g2.setColor(UITheme.TEXT_MUTED);
        g2.setFont(new Font("Roboto", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2.drawString(message, x, y);
    }
    
    /**
     * Calculate category totals
     */
    private Map<String, Double> getCategoryTotals() {
        Map<String, Double> totals = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            totals.put(category, totals.getOrDefault(category, 0.0) + expense.getAmount());
        }
        return totals;
    }
    
    /**
     * Calculate monthly totals for last N months
     */
    private Map<String, Double> getMonthlyTotals(int months) {
        Map<String, Double> totals = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            String monthKey = monthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            totals.put(monthKey, 0.0);
        }
        
        for (Expense expense : expenses) {
            String expenseMonth = expense.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (totals.containsKey(expenseMonth)) {
                totals.put(expenseMonth, totals.get(expenseMonth) + expense.getAmount());
            }
        }
        
        return totals;
    }
    
    /**
     * Refresh data and update charts
     */
    public void refreshData() {
        expenses = expenseManager.getAllExpenses(currentUser);
        repaint();
    }
}
