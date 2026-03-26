package com.officeflow.service;

import com.officeflow.domain.Booking;
import com.officeflow.domain.enums.BookingStatusEnum;
import com.officeflow.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j //serve per i log
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    // secondi - minuti - ore - giorno - mese - anno
    //fixedrate per TEST (in millisecondi)
    @Scheduled(fixedRate = 100000)
    public void updatePastBookings() {
        log.info("Inizio task automatico: aggiornamento booking passati...");

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Booking> expiredToday = bookingRepository.findByDateAndEndHourBeforeAndStatusNot(today, now, BookingStatusEnum.COMPLETATO);

        if (expiredToday.isEmpty()) {
            log.info("Nessun booking scaduto al momento.");
        } else {
            for (Booking booking : expiredToday) {
                booking.setStatus(BookingStatusEnum.COMPLETATO);
                log.info("Booking " + booking.getId() + " completato (scaduto alle " + booking.getEndHour() + ")");
            }
            bookingRepository.saveAll(expiredToday);
            log.info("Database aggiornato con successo.");
        }
    }
}