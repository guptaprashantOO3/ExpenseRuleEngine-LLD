package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.models.Trip;
import com.expense.ruleengine.service.rules.TripRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.List;
import java.util.Optional;

/**
 * Validates total meal/restaurant expense limit for a trip (default: $1,000).
 */
public class TripMealLimitRule implements TripRule {
    public static final double DEFAULT_LIMIT = 1000.0;
    private final double limit;

    public TripMealLimitRule() {
        this(DEFAULT_LIMIT);
    }

    public TripMealLimitRule(double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public Optional<List<Violation>> check(Trip trip) {
        if (trip != null) {
            double mealTotal = trip.calculateTotalRestaurantExpenses();
            if (mealTotal > limit) {
                return Optional.of(List.of(Violation.of(
                        "TripMealLimitRule",
                        "Total meal expenses ($" + mealTotal + ") exceed the allowed meal limit of $" + limit
                )));
            }
        }
        return Optional.empty();
    }
}
