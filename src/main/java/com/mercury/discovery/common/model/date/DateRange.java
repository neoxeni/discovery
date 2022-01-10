package com.mercury.discovery.common.model.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateRange {
    private LocalDateTime start;

    private LocalDateTime end;

    public DateRange() {
        this.start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        this.end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
    }

    public DateRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "DateRange{" + "start=" + start + ", end=" + end + '}';
    }
}
