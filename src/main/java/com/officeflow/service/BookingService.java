package com.officeflow.service;

import com.officeflow.domain.Booking;
import com.officeflow.domain.enums.BookingStatusEnum;
import com.officeflow.domain.enums.ResourceTypeEnum;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import com.officeflow.exception.BookingConflictException; // Assicurati di aver creato queste classi
import com.officeflow.exception.ResourceNotFoundException;
import com.officeflow.mapper.BookingMapper;
import com.officeflow.repository.BookingRepository;
import com.officeflow.repository.ResourceRepository;
import com.officeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;


    public BookingResponseDTO createBooking(BookingRequestDTO request) {

        LocalDate oggi = LocalDate.now();
        LocalTime adesso = LocalTime.now();

        // utente inesistente
        if (!userRepository.existsById(request.getUserId())) {
            throw new ResourceNotFoundException("L'utente con ID " + request.getUserId() + " non esiste.");
        }

        // risorsa inesisetnte
        var resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("La risorsa con ID " + request.getResourceId() + " non esiste."));

        // data prenotazione precedente a quella corrente
        if (request.getDate().isBefore(oggi)) {
            throw new BookingConflictException("La data di prenotazione non puo' essere precedente a quella corrente");
        }

        // orario inizio prenotazione precedente a quello corrente solo se la prenotazione e' per oggi
        if (request.getDate().isEqual(oggi) && !request.getStartHour().isAfter(adesso)) {
            throw new BookingConflictException("L'orario di inizio deve essere successivo all'orario corrente");
        }

        // valisazione oraria
        if (!request.getStartHour().isBefore(request.getEndHour())) {
            throw new BookingConflictException("L'orario di inizio deve essere precedente alla fine.");
        }

        // limite DESK giornaliero
        if (resource.getType() == ResourceTypeEnum.DESK) {
            boolean hasAlreadyBookedADesk = bookingRepository.existsByUserIdAndDate(request.getUserId(), request.getDate());

            if (hasAlreadyBookedADesk) {
                throw new BookingConflictException("L'utente ha già una prenotazione per un DESK in data " + request.getDate());
            }
        }

        // risorsa occupata
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                request.getResourceId(),
                request.getDate(),
                request.getStartHour(),
                request.getEndHour()
        );

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("La risorsa è già occupata per l'orario selezionato.");
        }

        // salvataggio
        Booking entity = bookingMapper.toEntity(request);
        entity.setStatus(BookingStatusEnum.ATTESA);
        Booking saved = bookingRepository.save(entity);
        return bookingMapper.toResponse(saved);
    }

    public List<BookingResponseDTO> getAllBooking() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    public void deleteBooking(String id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossibile eliminare: prenotazione non trovata con ID: " + id);
        }
        bookingRepository.deleteById(id);
    }
}
