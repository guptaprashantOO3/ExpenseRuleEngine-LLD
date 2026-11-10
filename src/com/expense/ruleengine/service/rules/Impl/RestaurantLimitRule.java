package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.enums.ExpenseType;
import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.Optional;

/**
 * Validates restaurant expense limit (default: $75).
 */
public class RestaurantLimitRule implements ExpenseRule {
    public static final double DEFAULT_LIMIT = 75.0;
    private final double limit;

    public RestaurantLimitRule() {
        this(DEFAULT_LIMIT);
    }

    public RestaurantLimitRule(double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public Optional<Violation> check(Expense e) {
        if (e != null && e.getExpenseType() == ExpenseType.RESTAURANT) {
            if (e.getAmountUsd() != null && e.getAmountUsd() > limit) {
                return Optional.of(Violation.of(
                        "RestaurantLimitRule",
                        "Restaurant expense of $" + e.getAmountUsd() + " exceeds the limit of $" + limit
                ));
            }
        }
        return Optional.empty();
    }
}
