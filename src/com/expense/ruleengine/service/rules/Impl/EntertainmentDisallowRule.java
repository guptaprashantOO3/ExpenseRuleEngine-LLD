package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.enums.ExpenseType;
import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.Optional;

/**
 * Rejects entertainment expenses.
 */
public class EntertainmentDisallowRule implements ExpenseRule {

    @Override
    public Optional<Violation> check(Expense e) {
        if (e != null && e.getExpenseType() == ExpenseType.ENTERTAINMENT) {
            return Optional.of(Violation.of(
                    "EntertainmentDisallowRule",
                    "Entertainment expenses are not permitted and must be rejected."
            ));
        }
        return Optional.empty();
    }
}
