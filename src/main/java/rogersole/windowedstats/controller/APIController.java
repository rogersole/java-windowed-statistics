package rogersole.windowedstats.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rogersole.windowedstats.model.Result;
import rogersole.windowedstats.model.Transaction;
import rogersole.windowedstats.service.StatisticsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Calendar;
import java.util.TimeZone;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
public class APIController {

    private final long windowSizeInMillis;

    private static final long WINDOW_CLEANING_INTERVAL_IN_MILLIS = 1 * 1000;

    private final StatisticsService statisticsService;
    private final Calendar calendar;

    public APIController(@Value("${window.seconds}") long windowSizeInSeconds) {
        this.windowSizeInMillis = windowSizeInSeconds * 1000;
        this.statisticsService = new StatisticsService(windowSizeInMillis, WINDOW_CLEANING_INTERVAL_IN_MILLIS);
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    @PostMapping(value = "/transactions", consumes = "application/json")
    public ResponseEntity<Void> create(@Valid @RequestBody TransactionDTO transactionDTO) {
        val now = calendar.getTimeInMillis();
        if (transactionDTO.timestamp + windowSizeInMillis < now) {
            log.info("Ignored transaction {} because it's too old (now: {})", transactionDTO, now);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            log.info("Accepted transaction {}", transactionDTO);
            statisticsService.enqueueTransaction(transactionDTO.createTransaction());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @GetMapping(value = "/statistics", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Result> getTransactions() {
        return new ResponseEntity<>(statisticsService.getStatistics(), OK);
    }

    static class TransactionDTO {
        @NotNull
        Long timestamp;
        @NotNull
        Double amount;

        public Transaction createTransaction() {
            return new Transaction(timestamp, amount);
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Transaction{timestamp=" + timestamp + ", amount=" + amount + '}';
        }
    }
}
