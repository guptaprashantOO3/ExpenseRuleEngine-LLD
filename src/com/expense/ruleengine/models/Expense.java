package com.expense.ruleengine.models;

import com.expense.ruleengine.enums.ExpenseType;

import java.util.Objects;

public class Expense {
    private final String expenseId;
    private final String tripId;
    private final Double amountUsd;
    private final ExpenseType expenseType;

    public Expense(String expenseId, Double amountUsd, ExpenseType expenseType, String tripId) {
        this.expenseId = expenseId;
        this.amountUsd = amountUsd;
        this.expenseType = expenseType;
        this.tripId = tripId;
    }

    public Expense(String expenseId, String tripId, Double amountUsd, ExpenseType expenseType) {
        this(expenseId, amountUsd, expenseType, tripId);
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getTripId() {
        return tripId;
    }

    public Double getAmountUsd() {
        return amountUsd;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId='" + expenseId + '\'' +
                ", tripId='" + tripId + '\'' +
                ", amountUsd=" + amountUsd +
                ", expenseType=" + expenseType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(expenseId, expense.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId);
    }
}
