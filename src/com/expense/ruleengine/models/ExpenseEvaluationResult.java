package com.expense.ruleengine.models;

import com.expense.ruleengine.enums.EvaluationStatus;
import com.expense.ruleengine.service.rules.Violation;

import java.util.List;

/**
 * Result of evaluating an individual expense against expense-level rules.
 */
public class ExpenseEvaluationResult extends EvaluationResult {
    private final Expense expense;

    public ExpenseEvaluationResult(Expense expense, List<Violation> violations) {
        super((violations != null && !violations.isEmpty()) ? EvaluationStatus.REJECTED : EvaluationStatus.APPROVED, violations);
        this.expense = expense;
    }

    public Expense getExpense() {
        return expense;
    }

    public String getExpenseId() {
        return expense != null ? expense.getExpenseId() : null;
    }

    @Override
    public String toString() {
        return "ExpenseEvaluationResult{" +
                "expenseId='" + (expense != null ? expense.getExpenseId() : "N/A") + '\'' +
                ", amount=" + (expense != null ? expense.getAmountUsd() : 0.0) +
                ", type=" + (expense != null ? expense.getExpenseType() : "N/A") +
                ", status=" + getStatus() +
                ", violations=" + getViolations() +
                '}';
    }
}
