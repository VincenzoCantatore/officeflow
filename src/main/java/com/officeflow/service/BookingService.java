package com.officeflow.service;

import com.officeflow.domain.Booking;
import com.officeflow.domain.ResourceType;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import com.officeflow.mapper.BookingMapper;
import com.officeflow.repository.BookingRepository;
import com.officeflow.repository.ResourceRepository;
import com.officeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public BookingResponseDTO createBooking(BookingRequestDTO request) {

        // 1. Verifica esistenza Utente
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("Errore: L'utente con ID " + request.getUserId() + " non esiste.");
        }

        // 2. Recupero Risorsa (Ci serve l'oggetto intero per controllare il TYPE)
        var resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Errore: La risorsa con ID " + request.getResourceId() + " non esiste."));

        // 3. Validazione oraria base
        if (!request.getStartHour().isBefore(request.getEndHour())) {
            throw new RuntimeException("L'orario di inizio deve essere precedente alla fine.");
        }

        // 4. LOGICA FASE 2.3: Limite 1 DESK al giorno per utente
        if (resource.getType() == ResourceType.DESK) {
            boolean hasAlreadyBookedADesk = bookingRepository.existsByUserIdAndDate(request.getUserId(), request.getDate());

            if (hasAlreadyBookedADesk) {
                throw new RuntimeException("L'utente ha già una prenotazione per un DESK in data " + request.getDate());
            }
        }

        // 5. LOGICA FASE 2.2: Anti-sovrapposizione oraria
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                request.getResourceId(),
                request.getDate(),
                request.getStartHour(),
                request.getEndHour()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Risorsa " + request.getResourceId() + " già occupata per l'orario selezionato.");
        }

        // 6. Salvataggio
        Booking entity = bookingMapper.toEntity(request);
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
            throw new RuntimeException("Prenotazione non trovata con ID: " + id);
        }
        bookingRepository.deleteById(id);
    }
}