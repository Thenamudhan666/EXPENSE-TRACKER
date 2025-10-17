package com.thena.expense.tracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    public Long id;
    public LocalDate date;
    public BigDecimal amount;
    public String currency;
    public String category;
    public String payee;
    public String notes;
}
