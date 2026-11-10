package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.Optional;

/**
 * Validates global maximum expense limit across all expense types (default: $250).
 */
public class MaxExpenseLimitRule implements ExpenseRule {
    public static final double DEFAULT_LIMIT = 250.0;
    private final double limit;

    public MaxExpenseLimitRule() {
        this(DEFAULT_LIMIT);
    }

    public MaxExpenseLimitRule(double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public Optional<Violation> check(Expense e) {
        if (e != null && e.getAmountUsd() != null && e.getAmountUsd() > limit) {
            return Optional.of(Violation.of(
                    "MaxExpenseLimitRule",
                    "Expense amount $" + e.getAmountUsd() + " exceeds the maximum allowed limit of $" + limit
            ));
        }
        return Optional.empty();
    }
}
