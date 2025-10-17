package com.thena.expense.tracker.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {
    private final CardLayout card = new CardLayout();
    private final JPanel content = new JPanel(card);

    public DashboardFrame() {
        super("Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left navigation
        String[] pages = {"Overview", "Expenses", "Categories", "Budgets", "Reports", "Settings"};
        JList<String> nav = new JList<>(pages);
        nav.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nav.setSelectedIndex(1);
        JScrollPane navScroll = new JScrollPane(nav);
        navScroll.setPreferredSize(new Dimension(220, 0));
        add(navScroll, BorderLayout.WEST);

        // Content panels
        Map<String, JComponent> panels = new LinkedHashMap<>();
        panels.put("Overview", labelCenter("Overview"));
        panels.put("Expenses", new ExpenseListPanel());
        panels.put("Categories", new CategoryPanel());    // simple placeholder panel
        panels.put("Budgets", labelCenter("Budgets"));
        panels.put("Reports", labelCenter("Reports"));
        panels.put("Settings", labelCenter("Settings"));

        panels.forEach((name, comp) -> content.add(comp, name));
        add(content, BorderLayout.CENTER);

        // Menu (optional)
        setJMenuBar(buildMenuBar());

        // Nav switching
        nav.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String page = nav.getSelectedValue();
                card.show(content, page);
            }
        });

        card.show(content, "Expenses");
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.add(new JMenu("File"));
        mb.add(new JMenu("Help"));
        return mb;
    }

    private JComponent labelCenter(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }
}
