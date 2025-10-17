package com.thena.expense.tracker.ui;

import com.thena.expense.tracker.model.Expense;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExpenseDialog extends JDialog {
    private final JTextField tfDate     = new JTextField(10); // yyyy-MM-dd
    private final JTextField tfAmount   = new JTextField(10);
    private final JTextField tfCurrency = new JTextField(6);
    private final JTextField tfCategory = new JTextField(12);
    private final JTextField tfPayee    = new JTextField(12);
    private final JTextField tfNotes    = new JTextField(20);

    private Expense result;

    public ExpenseDialog(Window owner, Expense existing) {
        super(owner, (existing == null ? "Add Expense" : "Edit Expense"), ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.add(new JLabel("Date (yyyy-MM-dd):")); form.add(tfDate);
        form.add(new JLabel("Amount:"));            form.add(tfAmount);
        form.add(new JLabel("Currency:"));          form.add(tfCurrency);
        form.add(new JLabel("Category:"));          form.add(tfCategory);
        form.add(new JLabel("Payee:"));             form.add(tfPayee);
        form.add(new JLabel("Notes:"));             form.add(tfNotes);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> onOK(existing));
        cancel.addActionListener(e -> { result = null; dispose(); });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(ok); buttons.add(cancel);

        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);

        // Defaults / existing values
        if (existing == null) {
            tfDate.setText(LocalDate.now().toString());
            tfCurrency.setText("INR");
        } else {
            tfDate.setText(existing.date.toString());
            tfAmount.setText(existing.amount.toPlainString());
            tfCurrency.setText(existing.currency);
            tfCategory.setText(existing.category);
            tfPayee.setText(existing.payee);
            tfNotes.setText(existing.notes);
        }
    }

    public Expense open() {
        setVisible(true);   // modal
        return result;
    }

    private void onOK(Expense existing) {
        try {
            Expense e = (existing == null ? new Expense() : existing);
            e.date     = LocalDate.parse(tfDate.getText().trim());
            e.amount   = new BigDecimal(tfAmount.getText().trim());
            e.currency = tfCurrency.getText().trim();
            e.category = tfCategory.getText().trim();
            e.payee    = tfPayee.getText().trim();
            e.notes    = tfNotes.getText().trim();
            result = e;
            dispose();
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or amount format.");
        }
    }
}
