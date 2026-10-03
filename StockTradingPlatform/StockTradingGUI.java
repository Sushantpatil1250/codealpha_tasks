import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

/** Swing GUI for the Stock Trading Platform - CodeAlpha Java Internship, Task 2. */
public class StockTradingGUI extends JFrame {
    private Market market;
    private User user;

    private final DefaultTableModel marketModel = readOnlyModel("Symbol", "Company", "Price", "Change %");
    private final DefaultTableModel portfolioModel = readOnlyModel("Symbol", "Qty", "Avg Cost", "Price", "Value", "P/L");
    private final JTable marketTable = new JTable(marketModel);
    private final JTable portfolioTable = new JTable(portfolioModel);
    private final JTextArea txArea = new JTextArea();
    private final JLabel lblUser = new JLabel();
    private final JLabel lblDay = new JLabel();
    private final JLabel lblCash = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblPL = new JLabel();
    private final JLabel lblStatus = new JLabel(" ");
    private final JComboBox<String> cmbSymbol = new JComboBox<>();
    private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final ChartPanel chart = new ChartPanel();

    public StockTradingGUI() {
        super("CodeAlpha - Stock Trading Platform");
        DataStore.State saved = DataStore.load();
        if (saved != null) {
            market = saved.market;
            user = saved.user;
        } else {
            String name = JOptionPane.showInputDialog(null, "Enter your name:", "Welcome",
                    JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.trim().isEmpty()) name = "Trader";
            market = new Market();
            user = new User(name.trim(), 100000.00);
        }
        for (Stock s : market.getStocks()) cmbSymbol.addItem(s.getSymbol());
        buildUI();
        refresh();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                DataStore.save(market, user);
                dispose();
                System.exit(0);
            }
        });
        setSize(980, 680);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static DefaultTableModel readOnlyModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        // ---- header ----
        JPanel header = new JPanel(new GridLayout(1, 5, 10, 0));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        for (JLabel l : new JLabel[]{lblUser, lblDay, lblCash, lblTotal, lblPL}) {
            l.setFont(l.getFont().deriveFont(Font.BOLD, 14f));
            header.add(l);
        }
        add(header, BorderLayout.NORTH);

        // ---- tabs ----
        marketTable.setRowHeight(24);
        portfolioTable.setRowHeight(24);
        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                                     boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String txt = String.valueOf(v);
                if (!sel) comp.setForeground(txt.startsWith("-") ? new Color(200, 0, 0) : new Color(0, 130, 0));
                return comp;
            }
        };
        marketTable.getColumnModel().getColumn(3).setCellRenderer(colorRenderer);
        portfolioTable.getColumnModel().getColumn(5).setCellRenderer(colorRenderer);
        marketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        marketTable.getSelectionModel().addListSelectionListener(e -> {
            int row = marketTable.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0)
                cmbSymbol.setSelectedItem(marketModel.getValueAt(row, 0));
        });

        JScrollPane marketPane = new JScrollPane(marketTable);
        marketPane.setBorder(BorderFactory.createTitledBorder("Market Data (click a row to select stock)"));
        JScrollPane portPane = new JScrollPane(portfolioTable);
        portPane.setBorder(BorderFactory.createTitledBorder("My Portfolio"));
        JPanel mainTab = new JPanel(new GridLayout(2, 1, 0, 8));
        mainTab.add(marketPane);
        mainTab.add(portPane);

        txArea.setEditable(false);
        txArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Market & Portfolio", mainTab);
        tabs.addTab("Transactions", new JScrollPane(txArea));
        tabs.addTab("Performance", chart);
        add(tabs, BorderLayout.CENTER);

        // ---- trade bar ----
        JButton btnBuy = new JButton("Buy");
        JButton btnSell = new JButton("Sell");
        JButton btnNext = new JButton("Next Day >>");
        btnBuy.setBackground(new Color(40, 167, 69));
        btnBuy.setForeground(Color.WHITE);
        btnSell.setBackground(new Color(220, 53, 69));
        btnSell.setForeground(Color.WHITE);
        btnBuy.setOpaque(true);
        btnSell.setOpaque(true);
        btnBuy.addActionListener(e -> trade(true));
        btnSell.addActionListener(e -> trade(false));
        btnNext.addActionListener(e -> {
            market.nextDay();
            user.recordSnapshot(market);
            lblStatus.setText("Moved to day " + market.getDay() + ". Prices updated.");
            refresh();
        });

        JPanel trade = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        trade.add(new JLabel("Stock:"));
        trade.add(cmbSymbol);
        trade.add(new JLabel("Quantity:"));
        trade.add(spnQty);
        trade.add(btnBuy);
        trade.add(btnSell);
        trade.add(btnNext);

        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 10));
        south.add(trade, BorderLayout.CENTER);
        south.add(lblStatus, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    private void trade(boolean buy) {
        try {
            Stock s = market.getStock((String) cmbSymbol.getSelectedItem());
            int qty = (Integer) spnQty.getValue();
            if (buy) user.buy(s, qty, market.getDay());
            else user.sell(s, qty, market.getDay());
            lblStatus.setText(String.format("%s %d x %s @ %.2f successful.",
                    buy ? "Bought" : "Sold", qty, s.getSymbol(), s.getPrice()));
            refresh();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Trade failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Re-reads all data and updates every part of the screen. */
    private void refresh() {
        int sel = marketTable.getSelectedRow();
        marketModel.setRowCount(0);
        for (Stock s : market.getStocks())
            marketModel.addRow(new Object[]{s.getSymbol(), s.getName(),
                    String.format("%.2f", s.getPrice()), String.format("%+.2f%%", s.getChangePercent())});
        if (sel >= 0 && sel < marketModel.getRowCount()) marketTable.setRowSelectionInterval(sel, sel);

        portfolioModel.setRowCount(0);
        for (Map.Entry<String, Integer> e : user.getHoldings().entrySet()) {
            Stock s = market.getStock(e.getKey());
            int q = e.getValue();
            double cost = user.getCostBasis().get(e.getKey());
            double value = s.getPrice() * q;
            portfolioModel.addRow(new Object[]{s.getSymbol(), q, String.format("%.2f", cost / q),
                    String.format("%.2f", s.getPrice()), String.format("%.2f", value),
                    String.format("%+.2f", value - cost)});
        }

        double total = user.totalValue(market);
        double pl = total - user.getStartingCash();
        lblUser.setText("User: " + user.getName());
        lblDay.setText("Day: " + market.getDay());
        lblCash.setText(String.format("Cash: %.2f", user.getCash()));
        lblTotal.setText(String.format("Total: %.2f", total));
        lblPL.setText(String.format("P/L: %+.2f (%+.2f%%)", pl, pl / user.getStartingCash() * 100));
        lblPL.setForeground(pl >= 0 ? new Color(0, 130, 0) : new Color(200, 0, 0));

        StringBuilder sb = new StringBuilder();
        if (user.getTransactions().isEmpty()) sb.append("No transactions yet.");
        for (Transaction t : user.getTransactions()) sb.append(t).append('\n');
        txArea.setText(sb.toString());

        chart.repaint();
    }

    /** Line chart of portfolio value per day. */
    private class ChartPanel extends JPanel {
        ChartPanel() { setBackground(Color.WHITE); }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            List<double[]> h = user.getHistory();
            int w = getWidth(), ht = getHeight(), left = 90, right = 30, top = 40, bottom = 40;
            g.setColor(Color.DARK_GRAY);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.drawString("Portfolio Value Over Time", left, 25);
            if (h.size() < 2) {
                g.setFont(g.getFont().deriveFont(Font.PLAIN, 13f));
                g.drawString("Click 'Next Day >>' to build performance history.", left, ht / 2);
                return;
            }
            double min = user.getStartingCash(), max = user.getStartingCash();
            for (double[] p : h) { min = Math.min(min, p[1]); max = Math.max(max, p[1]); }
            double pad = Math.max((max - min) * 0.1, 1);
            min -= pad; max += pad;
            double maxDay = h.get(h.size() - 1)[0];
            int cw = w - left - right, ch = ht - top - bottom;

            g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
            for (int i = 0; i <= 4; i++) {            // grid + y labels
                int y = top + ch * i / 4;
                g.setColor(new Color(230, 230, 230));
                g.drawLine(left, y, left + cw, y);
                g.setColor(Color.GRAY);
                g.drawString(String.format("%.0f", max - (max - min) * i / 4), 10, y + 4);
            }
            int baseY = top + (int) ((max - user.getStartingCash()) / (max - min) * ch);
            g.setColor(Color.LIGHT_GRAY);             // starting capital line
            g.drawLine(left, baseY, left + cw, baseY);
            g.drawString("Start", left + cw - 30, baseY - 4);

            g.setStroke(new BasicStroke(2f));
            int px = -1, py = -1;
            for (double[] p : h) {
                int x = left + (int) (p[0] / maxDay * cw);
                int y = top + (int) ((max - p[1]) / (max - min) * ch);
                g.setColor(new Color(30, 100, 220));
                if (px >= 0) g.drawLine(px, py, x, y);
                g.fillOval(x - 3, y - 3, 6, 6);
                g.setColor(Color.GRAY);
                g.drawString("D" + (int) p[0], x - 8, ht - 15);
                px = x; py = y;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockTradingGUI::new);
    }
}
