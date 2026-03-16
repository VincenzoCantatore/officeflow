package com.officeflow.service;

import com.officeflow.domain.Booking;
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

        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("Errore: L'utente con ID " + request.getUserId() + " non esiste.");
        }

        if (!resourceRepository.existsById(request.getResourceId())) {
            throw new RuntimeException("Errore: La risorsa con ID " + request.getResourceId() + " non esiste.");
        }

        if (!request.getStartHour().isBefore(request.getEndHour())) {
            throw new RuntimeException("L'orario di inizio deve essere precedente alla fine.");
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                request.getResourceId(),
                request.getDate(),
                request.getStartHour(),
                request.getEndHour()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Risorsa " + request.getResourceId()+ " già occupata per l'orario selezionato.");
        }

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