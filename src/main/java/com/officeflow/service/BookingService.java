package com.officeflow.service;

import com.officeflow.domain.Booking;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import com.officeflow.mapper.BookingMapper;
import com.officeflow.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        Booking booking = bookingMapper.toEntity(bookingRequestDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    public List<BookingResponseDTO> getAllBooking() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }
}