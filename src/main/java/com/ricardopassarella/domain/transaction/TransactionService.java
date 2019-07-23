package com.ricardopassarella.domain.transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

class TransactionService {

    Map<Set<Enum<?>>, Period> getFrequency(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyMap();
        }

        return transactions.stream()
                           .collect(Collectors.groupingBy(Transaction::getTags, Collectors.toList()))
                           .entrySet()
                           .stream()
                           .map(this::findFrequency)
                           .collect(Collectors.toMap(SimpleEntry::getKey,
                                                     SimpleEntry::getValue));
    }

    private SimpleEntry<Set<Enum<?>>, Period> findFrequency(Entry<Set<Enum<?>>, List<Transaction>> entry) {
        Set<Enum<?>> tags = entry.getKey();

        if (entry.getValue()
                 .size() < 2) {
            return new SimpleEntry<>(tags, Period.UNKNOWN);
        }

        List<Transaction> transactionsSorted = entry.getValue()
                                                    .stream()
                                                    .sorted(Comparator.comparing(Transaction::getDate))
                                                    .collect(Collectors.toList());

        LocalDate firstDate = transactionsSorted.get(0)
                                                .getDate();
        LocalDate lastDate = transactionsSorted.get(transactionsSorted.size() - 1)
                                               .getDate();
        long numberOfMonths = ChronoUnit.MONTHS.between(firstDate, lastDate);

        if (numberOfMonths + 1 == transactionsSorted.size()) {
            return new SimpleEntry<>(tags, Period.MONTHLY);
        }

        long numberOfDays = ChronoUnit.DAYS.between(firstDate, lastDate);
        long numberOfDaysPerTransaction = numberOfDays / transactionsSorted.size();

        Period period = Arrays.stream(Period.values())
                              .filter(type -> numberOfDaysPerTransaction >= type.getMinimumNumberOfDays() &&
                                              numberOfDaysPerTransaction <= type.getMaximumNumberOfDays())
                              .findAny()
                              .orElse(Period.UNKNOWN);

        return new SimpleEntry<>(tags, period);
    }

}
