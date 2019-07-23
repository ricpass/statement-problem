package com.ricardopassarella.domain.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@RequiredArgsConstructor
public class TransactionImpl implements Transaction {

    private final String id;
    private final LocalDate date;
    private final long transactionAmountInPence;
    private final Set<Enum<?>> tags;

}
