package com.expense.ruleengine.service.rules;

import com.expense.ruleengine.models.Expense;

import java.util.List;
import java.util.Optional;

/**
 * Rule interface for evaluating individual expenses.
 */
public interface ExpenseRule extends Rule<Expense> {

    /**
     * Checks an expense for a rule violation.
     *
     * @param e the expense to evaluate
     * @return an Optional containing the violation if found, or empty if valid
     */
    Optional<Violation> check(Expense e);

    @Override
    default List<Violation> evaluate(Expense expense) {
        return check(expense).map(List::of).orElse(List.of());
    }
}
