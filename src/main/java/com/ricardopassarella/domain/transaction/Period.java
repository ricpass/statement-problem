package com.ricardopassarella.domain.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum Period {
    WEEKLY(5, 9),
    FORTNIGHTLY(10, 19),
    FOUR_WEEKLY(20, 28),
    MONTHLY(28, 35),
    UNKNOWN(Integer.MAX_VALUE, Integer.MIN_VALUE);

    private final int minimumNumberOfDays;
    private final int maximumNumberOfDays;

}
