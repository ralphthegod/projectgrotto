package com.deemaso.grotto.ai;

import com.deemaso.core.Entity;

public interface Decision {
    boolean evaluate(AIContext context);
}
