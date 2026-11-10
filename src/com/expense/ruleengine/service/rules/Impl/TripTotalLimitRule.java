package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.models.Trip;
import com.expense.ruleengine.service.rules.TripRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.List;
import java.util.Optional;

/**
 * Validates total trip expense limit (default: $2,000).
 */
public class TripTotalLimitRule implements TripRule {
    public static final double DEFAULT_LIMIT = 2000.0;
    private final double limit;

    public TripTotalLimitRule() {
        this(DEFAULT_LIMIT);
    }

    public TripTotalLimitRule(double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public Optional<List<Violation>> check(Trip trip) {
        if (trip != null) {
            double total = trip.calculateTotalExpenses();
            if (total > limit) {
                return Optional.of(List.of(Violation.of(
                        "TripTotalLimitRule",
                        "Total trip expenses ($" + total + ") exceed the allowed trip limit of $" + limit
                )));
            }
        }
        return Optional.empty();
    }
}
