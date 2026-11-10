package com.expense.ruleengine.service.rules.Impl;

import com.expense.ruleengine.models.Expense;
import com.expense.ruleengine.service.rules.ExpenseRule;
import com.expense.ruleengine.service.rules.Violation;

import java.util.Optional;

public class DisallowRule implements ExpenseRule {
    private final String customReason;

    public DisallowRule() {
        this(null);
    }

    public DisallowRule(String customReason) {
        this.customReason = customReason;
    }

    @Override
    public Optional<Violation> check(Expense e) {
        if (e != null) {
            String msg = (customReason != null && !customReason.isBlank())
                    ? customReason
                    : "Expense Type " + e.getExpenseType() + " is not allowed.";
            return Optional.of(Violation.of(msg));
        }
        return Optional.empty();
    }
}
