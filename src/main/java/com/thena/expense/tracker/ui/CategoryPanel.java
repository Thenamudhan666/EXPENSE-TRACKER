package com.thena.expense.tracker.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoryPanel extends JPanel {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Type","Active"}, 0) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable table = new JTable(model);

    public CategoryPanel() {
        setLayout(new BorderLayout(6,6));
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        add(toolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // sample row
        model.addRow(new Object[]{1,"Groceries","EXPENSE", true});
    }

    private JToolBar toolbar() {
        JToolBar tb = new JToolBar();
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        del.addActionListener(e -> onDelete());
        tb.add(add); tb.add(edit); tb.add(del);
        return tb;
    }

    private void onAdd() {
        String name = JOptionPane.showInputDialog(this, "Category name");
        if (name != null && !name.isBlank()) {
            model.addRow(new Object[]{0, name.trim(), "EXPENSE", true});
        }
    }

    private void onEdit() {
        int view = table.getSelectedRow();
        if (view < 0) { JOptionPane.showMessageDialog(this, "Select a category"); return; }
        int r = table.convertRowIndexToModel(view);
        String cur = (String) model.getValueAt(r, 1);
        String name = JOptionPane.showInputDialog(this, "Edit category", cur);
        if (name != null && !name.isBlank()) model.setValueAt(name.trim(), r, 1);
    }

    private void onDelete() {
        int view = table.getSelectedRow();
        if (view < 0) { JOptionPane.showMessageDialog(this, "Select a category"); return; }
        int r = table.convertRowIndexToModel(view);
        if (JOptionPane.showConfirmDialog(this, "Delete?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            model.removeRow(r);
        }
    }
}
