package com.github.jacobhillier.dynamoford

import org.buildobjects.process.ProcBuilder
import org.buildobjects.process.ProcResult
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

class AcceptanceTest extends Specification {

    static final LocalDateTime TODAY = LocalDateTime.of(2020, 3, 12, 0, 0)
    static final LocalDateTime FIVE_DAYS_TIME = LocalDateTime.of(2020, 3, 17, 0, 0)

    @Unroll
    void "should price basket correctly"() {
        when:
        ProcResult procResult = executeShellCommand(items, dateTime)

        then:
        procResult.exitValue == 0
        procResult.outputString.trim() == expectedTotalCost

        where:
        items                                                             | dateTime       | expectedTotalCost
        [new Item("soup", 3), new Item("bread", 2)]                       | TODAY          | "3.15"
        [new Item("apple", 6), new Item("milk", 1)]                       | TODAY          | "1.9"
        [new Item("apple", 6), new Item("milk", 1)]                       | FIVE_DAYS_TIME | "1.84"
        [new Item("apple", 3), new Item("soup", 2), new Item("bread", 1)] | FIVE_DAYS_TIME | "1.97"
    }

    private static ProcResult executeShellCommand(List<Item> items, LocalDateTime dateTime) {
        List<String> javaArgs = ["-cp", System.getProperty("java.class.path"), Main.class.name]
        List<String> itemArgs = items.collectMany { ["-i", "${it.productName}=${it.quantity}"] }
        List<String> dateArgs = ["-d", dateTime.toString()]

        return new ProcBuilder("java")
                .withArgs(javaArgs + itemArgs + dateArgs as String[])
                .run()
    }

    private static class Item {
        private final String productName
        private final int quantity

        private Item(String productName, int quantity) {
            this.productName = productName
            this.quantity = quantity
        }
    }
}
