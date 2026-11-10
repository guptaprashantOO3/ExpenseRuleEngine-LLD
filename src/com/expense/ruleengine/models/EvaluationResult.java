package com.expense.ruleengine.models;

import com.expense.ruleengine.enums.EvaluationStatus;
import com.expense.ruleengine.service.rules.Violation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base evaluation result model containing the evaluation status and violations.
 */
public abstract class EvaluationResult {
    private final EvaluationStatus status;
    private final List<Violation> violations;

    public EvaluationResult(EvaluationStatus status, List<Violation> violations) {
        this.status = status;
        this.violations = violations != null ? new ArrayList<>(violations) : new ArrayList<>();
    }

    public EvaluationStatus getStatus() {
        return status;
    }

    public boolean isApproved() {
        return status == EvaluationStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == EvaluationStatus.REJECTED;
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }
}
