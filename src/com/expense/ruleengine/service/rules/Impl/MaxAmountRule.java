package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.Optional;

public class MaxAmountRule implements ExpenseRule {
    private final double maxAmount;

    public MaxAmountRule(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    @Override
    public Optional<Violation> check(Expense e) {
        if (e != null && e.getAmountUsd() != null && e.getAmountUsd() > maxAmount) {
            return Optional.of(Violation.of(
                    "Amount $" + e.getAmountUsd() + " exceeds maximum allowed limit of $" + maxAmount
            ));
        }
        return Optional.empty();
    }
}
