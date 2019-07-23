package com.ricardopassarella.domain.transaction;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;

public class TransactionServiceTest {

    private TransactionService service = new TransactionService();

    @Test
    public void getFrequency_whenThereAreWeeklyTransaction_shouldReturnWeeklyPeriod() {
        // given:
        Set<Enum<?>> tags = new HashSet<>(asList(Tag.CHARGE, Tag.RENT));

        List<Transaction> transactions = createTransactionList(tags, "2018-01-01", "2018-01-09", "2018-01-15", "2018-01-21", "2018-01-30");

        // when:
        Map<Set<Enum<?>>, Period> frequencyMap = service.getFrequency(transactions);

        // then:
        assertEquals(Period.WEEKLY, frequencyMap.get(tags));
    }

    @Test
    public void getFrequency_whenThereAreFortnightlyTransaction_shouldReturnFortnightlyPeriod() {
        // given:
        Set<Enum<?>> tags = new HashSet<>(asList(Tag.CHARGE, Tag.RENT));

        // and:
        List<Transaction> transactions = createTransactionList(tags, "2018-01-01", "2018-01-16", "2018-01-28", "2018-02-12", "2018-02-27");

        // when:
        Map<Set<Enum<?>>, Period> frequencyMap = service.getFrequency(transactions);

        // then:
        assertEquals(Period.FORTNIGHTLY, frequencyMap.get(tags));
    }

    @Test
    public void getFrequency_whenThereAreFourWeeklyTransaction_shouldReturnFourWeeklyPeriod() {
        // given:
        Set<Enum<?>> tags = new HashSet<>(asList(Tag.CHARGE, Tag.RENT));

        // and:
        List<Transaction> transactions = createTransactionList(tags, "2018-01-30", "2018-02-26", "2018-03-25", "2018-04-23", "2018-05-21");

        // when:
        Map<Set<Enum<?>>, Period> frequencyMap = service.getFrequency(transactions);

        // then:
        assertEquals(Period.FOUR_WEEKLY, frequencyMap.get(tags));
    }

    @Test
    public void getFrequency_whenThereAreMonthlyTransaction_shouldReturnMonthlyPeriod() {
        // given:
        Set<Enum<?>> tags1 = new HashSet<>(asList(Tag.CHARGE, Tag.RENT));
        Set<Enum<?>> tags2 = new HashSet<>(asList(Tag.PAYMENT, Tag.CASH));

        // and:
        List<Transaction> t1 = createTransactionList(tags1, "2018-01-01", "2018-02-02", "2018-03-04", "2018-04-01", "2018-05-02", "2018-06-01", "2018-07-01");
        List<Transaction> t2 = createTransactionList(tags2, "2018-07-25");
        List<Transaction> transactions = Stream.concat(t1.stream(), t2.stream())
                                               .collect(Collectors.toList());

        // when:
        Map<Set<Enum<?>>, Period> frequencyMap = service.getFrequency(transactions);

        // then:
        assertEquals(Period.MONTHLY, frequencyMap.get(tags1));
        assertEquals(Period.UNKNOWN, frequencyMap.get(tags2));
    }

    @Test
    public void getFrequency_whenTransactionsFromFile() throws IOException, URISyntaxException {
        // given:
        List<Transaction> transactions = getTransactionsFromFile("/statement");

        // when:
        Map<Set<Enum<?>>, Period> frequencyMap = service.getFrequency(transactions);

        // then:
        assertEquals(Period.MONTHLY, frequencyMap.get(new HashSet<Enum<?>>(asList(Tag.CHARGE, Tag.RENT))));
        assertEquals(Period.WEEKLY, frequencyMap.get(new HashSet<Enum<?>>(asList(Tag.PAYMENT, Tag.CASH))));
        assertEquals(Period.FOUR_WEEKLY, frequencyMap.get(new HashSet<Enum<?>>(asList(Tag.PAYMENT, Tag.HOUSING_BENEFIT))));
    }

    private List<Transaction> createTransactionList(Set<Enum<?>> tags, String... dates) {
        return Arrays.stream(dates)
                     .map(d -> TransactionImpl.builder()
                                              .date(LocalDate.parse(d))
                                              .id(UUID.randomUUID()
                                                      .toString())
                                              .transactionAmountInPence(1)
                                              .tags(tags)
                                              .build())
                     .collect(Collectors.toList());
    }

    private List<Transaction> getTransactionsFromFile(String fileName) throws IOException, URISyntaxException {
        return Files.lines(Paths.get(getClass().getResource(fileName)
                                               .toURI()))
                    .map(line -> {
                        String[] elements = line.split(",");
                        return TransactionImpl.builder()
                                              .date(LocalDate.parse(elements[0], DateTimeFormatter.ofPattern("dd MMM yyyy")))
                                              .id(elements[1])
                                              .tags(stream(elements[2].split(";"))
                                                            .map(tag -> Tag.valueOf(tag.toUpperCase()))
                                                            .collect(Collectors.toSet()))
                                              .transactionAmountInPence(Long.valueOf(elements[3].replace(".", "")))
                                              .build();
                    })
                    .collect(Collectors.toList());
    }

}