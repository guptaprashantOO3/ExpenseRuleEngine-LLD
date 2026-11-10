package com.expense.ruleengine.service.rules;

import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.models.Trip;

import java.util.List;
import java.util.Optional;

/**
 * Rule interface for evaluating trips (aggregations of expenses).
 */
public interface TripRule extends Rule<Trip> {

    /**
     * Checks a trip for rule violations.
     *
     * @param trip the trip to evaluate
     * @return an Optional containing violations if any, or empty if valid
     */
    Optional<List<Violation>> check(Trip trip);

    /**
     * Convenience method to check a raw list of expenses as a trip.
     *
     * @param expenses the list of expenses
     * @return an Optional containing violations if any, or empty if valid
     */
    default Optional<List<Violation>> check(List<Expense> expenses) {
        String tripId = (expenses != null && !expenses.isEmpty()) ? expenses.get(0).getTripId() : "UNKNOWN";
        return check(new Trip(tripId, expenses));
    }

    @Override
    default List<Violation> evaluate(Trip trip) {
        return check(trip).orElse(List.of());
    }
}
