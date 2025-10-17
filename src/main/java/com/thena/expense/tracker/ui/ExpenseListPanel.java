package com.thena.expense.tracker.ui;

import com.thena.expense.tracker.db.ExpenseDAO;
import com.thena.expense.tracker.model.Expense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExpenseListPanel extends JPanel {

    private final ExpenseDAO dao = new ExpenseDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField tfSearch = new JTextField(24);
    private final JLabel totalLabel = new JLabel("Total: —");

    public ExpenseListPanel() {
        setLayout(new BorderLayout(6,6));

        model = new DefaultTableModel(new String[]{
                "ID","Date","Amount","Currency","Category","Payee","Notes"
        }, 0) { @Override public boolean isCellEditable(int r,int c){ return false; }};

        table = new JTable(model);
        // hide ID column visually (keep in model)
        table.removeColumn(table.getColumnModel().getColumn(0));

        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusBar(), BorderLayout.SOUTH);

        reload(); // initial load from DB
    }

    private JToolBar buildToolbar() {
        JToolBar tb = new JToolBar();
        JButton bAdd = new JButton("Add");
        JButton bEdit = new JButton("Edit");
        JButton bDelete = new JButton("Delete");
        JButton bRefresh = new JButton("Refresh");

        bAdd.addActionListener(e -> onAdd());
        bEdit.addActionListener(e -> onEdit());
        bDelete.addActionListener(e -> onDelete());
        bRefresh.addActionListener(e -> reload());

        tb.add(bAdd); tb.add(bEdit); tb.add(bDelete); tb.add(bRefresh);
        tb.addSeparator();
        tb.add(new JLabel("Search:"));
        tb.add(tfSearch);
        tfSearch.addActionListener(e -> onSearch());

        return tb;
    }

    private JPanel statusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(totalLabel);
        return p;
    }

    // ===== actions =====

    private void reload() {
        runBG(dao::findAll, this::fillTable, this::showError);
    }

    private void onSearch() {
        String q = tfSearch.getText();
        runBG(() -> dao.search(q), this::fillTable, this::showError);
    }

    private void onAdd() {
        ExpenseDialog dlg = new ExpenseDialog(SwingUtilities.getWindowAncestor(this), null);
        Expense e = dlg.open();
        if (e == null) return;
        runBG(() -> dao.insert(e), ins -> { addRow(ins); updateTotal(); }, this::showError);
    }

    private void onEdit() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);

        Expense current = rowToExpense(modelRow);
        ExpenseDialog dlg = new ExpenseDialog(SwingUtilities.getWindowAncestor(this), current);
        Expense edited = dlg.open();
        if (edited == null) return;

        runBG(() -> { dao.update(edited); return edited; },
                ok -> { updateRow(modelRow, ok); updateTotal(); },
                this::showError);
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);
        long id = ((Number) model.getValueAt(modelRow, 0)).longValue();
        if (JOptionPane.showConfirmDialog(this, "Delete selected?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        runBG(() -> { dao.delete(id); return id; },
                ok -> { model.removeRow(modelRow); updateTotal(); },
                this::showError);
    }

    // ===== table helpers =====

    private void fillTable(List<Expense> rows) {
        model.setRowCount(0);
        rows.forEach(this::addRow);
        updateTotal();
    }

    private void addRow(Expense e) {
        model.addRow(new Object[] {
                e.id, e.date, e.amount, e.currency, e.category, e.payee, e.notes
        });
    }

    private void updateRow(int r, Expense e) {
        model.setValueAt(e.date,     r, 1);
        model.setValueAt(e.amount,   r, 2);
        model.setValueAt(e.currency, r, 3);
        model.setValueAt(e.category, r, 4);
        model.setValueAt(e.payee,    r, 5);
        model.setValueAt(e.notes,    r, 6);
    }

    private Expense rowToExpense(int r) {
        Expense e = new Expense();
        e.id       = ((Number) model.getValueAt(r, 0)).longValue();
        e.date     = (java.time.LocalDate) model.getValueAt(r, 1);
        e.amount   = (BigDecimal) model.getValueAt(r, 2);
        e.currency = (String) model.getValueAt(r, 3);
        e.category = (String) model.getValueAt(r, 4);
        e.payee    = (String) model.getValueAt(r, 5);
        e.notes    = (String) model.getValueAt(r, 6);
        return e;
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            total = total.add((BigDecimal) model.getValueAt(i, 2));
        }
        totalLabel.setText("Total: " + total.toPlainString());
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
    }

    // run DB work off the EDT
    private <T> void runBG(ThrowingSupplier<T> job,
                           java.util.function.Consumer<T> onOk,
                           java.util.function.Consumer<Exception> onErr) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return job.get(); }
            @Override protected void done() {
                try { onOk.accept(get()); } catch (Exception e) { onErr.accept(e); }
            }
        }.execute();
    }

    @FunctionalInterface private interface ThrowingSupplier<T> { T get() throws Exception; }
}
