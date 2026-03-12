package com.officeflow.controller;

import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import com.officeflow.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO request) {
        BookingResponseDTO response = bookingService.createBooking(request);
        System.out.println("Booking creato: " + response);
        return response;
    }

    @GetMapping
    public List<BookingResponseDTO> getBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBooking();
        System.out.println("Booking totali: " + bookings.size());
        return bookings;
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        System.out.println("Booking eliminato con ID: " + id);
    }
}