package com.ricardopassarella.domain.transaction;

import java.time.LocalDate;
import java.util.Set;

/**
 * Defines a financial transaction.
 */
public interface Transaction {
    /**
     * @return Uniquely identifies a transaction within the current scope.
     */
    String getId();

    /**
     * @return The date on which the transaction occurred; the real chronological
     * order of transactions occurring on the same date is undefined.
     */
    LocalDate getDate();

    /**
     * @return The amount of money transferred as a result of applying this transaction.
     * May be negative, zero or positive.
     */
    long getTransactionAmountInPence();

    /**
     * @return The tags on a particular transaction help to denote its type or intended usage.
     */
    Set<Enum<?>> getTags();

    /**
     * @param tag Any enumerated value
     *
     * @return True if the tag is present on this particular transaction, false otherwise.
     */
    default boolean hasTag(Enum<?> tag) {
        Set<Enum<?>> tags = getTags();
        return tags != null && tags.contains(tag);
    }

}