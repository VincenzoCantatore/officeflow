package com.officeflow.service;

import com.officeflow.domain.Booking;
import com.officeflow.domain.Resource;
import com.officeflow.domain.enums.BookingStatusEnum;
import com.officeflow.domain.enums.ResourceTypeEnum;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import com.officeflow.exception.BookingConflictException;
import com.officeflow.mapper.BookingMapper;
import com.officeflow.repository.BookingRepository;
import com.officeflow.repository.ResourceRepository;
import com.officeflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final String USER_ID = "user-1";
    private static final String RESOURCE_ID = "room-1";
    private static final LocalDate CURRENT_DATE = LocalDate.of(2026, 5, 7);
    private static final LocalDate BOOKING_DATE = LocalDate.of(2026, 5, 8);
    private static final LocalTime START = LocalTime.of(10, 0);
    private static final LocalTime END = LocalTime.of(11, 0);
    private static final LocalTime CURRENT_TIME = LocalTime.of(8, 0);

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_whenOverlapCheckFindsNoBookingsAtBoundaries_savesBooking() {
        BookingRequestDTO request = bookingRequest(START, END);
        Booking entity = bookingEntity();
        Booking saved = bookingEntity();
        saved.setId("booking-1");
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId("booking-1");

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);
            when(bookingRepository.findOverlappingBookings(RESOURCE_ID, BOOKING_DATE, START, END))
                    .thenReturn(List.of());
            when(bookingMapper.toEntity(request)).thenReturn(entity);
            when(bookingRepository.save(entity)).thenReturn(saved);
            when(bookingMapper.toResponse(saved)).thenReturn(response);

            BookingResponseDTO result = bookingService.createBooking(request);

            assertThat(result).isSameAs(response);
            assertThat(entity.getStatus()).isEqualTo(BookingStatusEnum.ATTESA);
            verify(bookingRepository).save(entity);
        }
    }

    @Test
    void createBooking_whenOverlapCheckFindsConflict_throwsAndDoesNotSave() {
        BookingRequestDTO request = bookingRequest(START, END);

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);
            when(bookingRepository.findOverlappingBookings(RESOURCE_ID, BOOKING_DATE, START, END))
                    .thenReturn(List.of(bookingEntity()));

            assertThatThrownBy(() -> bookingService.createBooking(request))
                    .isInstanceOf(BookingConflictException.class)
                    .hasMessageContaining("risorsa");

            verify(bookingRepository, never()).save(any());
            verify(bookingMapper, never()).toEntity(any());
        }
    }

    @Test
    void createBooking_passesExactRequestedIntervalToOverlapCheck() {
        BookingRequestDTO request = bookingRequest(START, END);
        Booking entity = bookingEntity();
        BookingResponseDTO response = new BookingResponseDTO();

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);
            when(bookingRepository.findOverlappingBookings(eq(RESOURCE_ID), eq(BOOKING_DATE), any(), any()))
                    .thenReturn(List.of());
            when(bookingMapper.toEntity(request)).thenReturn(entity);
            when(bookingRepository.save(entity)).thenReturn(entity);
            when(bookingMapper.toResponse(entity)).thenReturn(response);

            bookingService.createBooking(request);

            ArgumentCaptor<LocalTime> startCaptor = ArgumentCaptor.forClass(LocalTime.class);
            ArgumentCaptor<LocalTime> endCaptor = ArgumentCaptor.forClass(LocalTime.class);
            verify(bookingRepository).findOverlappingBookings(
                    eq(RESOURCE_ID),
                    eq(BOOKING_DATE),
                    startCaptor.capture(),
                    endCaptor.capture()
            );
            assertThat(startCaptor.getValue()).isEqualTo(START);
            assertThat(endCaptor.getValue()).isEqualTo(END);
        }
    }

    @Test
    void createBooking_whenDateIsFuture_allowsStartHourBeforeCurrentTime() {
        LocalTime futureDateStart = LocalTime.of(7, 0);
        LocalTime futureDateEnd = LocalTime.of(8, 0);
        BookingRequestDTO request = bookingRequest(BOOKING_DATE, futureDateStart, futureDateEnd);
        Booking entity = bookingEntity(request);
        BookingResponseDTO response = new BookingResponseDTO();

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);
            when(bookingRepository.findOverlappingBookings(RESOURCE_ID, BOOKING_DATE, futureDateStart, futureDateEnd))
                    .thenReturn(List.of());
            when(bookingMapper.toEntity(request)).thenReturn(entity);
            when(bookingRepository.save(entity)).thenReturn(entity);
            when(bookingMapper.toResponse(entity)).thenReturn(response);

            BookingResponseDTO result = bookingService.createBooking(request);

            assertThat(result).isSameAs(response);
            verify(bookingRepository).findOverlappingBookings(RESOURCE_ID, BOOKING_DATE, futureDateStart, futureDateEnd);
            verify(bookingRepository).save(entity);
        }
    }

    @Test
    void createBooking_whenDateIsTodayAndStartHourIsBeforeCurrentTime_throwsConflict() {
        BookingRequestDTO request = bookingRequest(CURRENT_DATE, LocalTime.of(7, 0), LocalTime.of(9, 0));

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);

            assertThatThrownBy(() -> bookingService.createBooking(request))
                    .isInstanceOf(BookingConflictException.class)
                    .hasMessageContaining("orario corrente");

            verify(bookingRepository, never()).findOverlappingBookings(any(), any(), any(), any());
            verify(bookingRepository, never()).save(any());
        }
    }

    @Test
    void createBooking_whenDateIsPast_throwsConflict() {
        BookingRequestDTO request = bookingRequest(CURRENT_DATE.minusDays(1), START, END);

        try (MockedStatic<LocalDate> ignoredDate = mockCurrentDate();
             MockedStatic<LocalTime> ignoredTime = mockCurrentTime()) {
            givenValidMeetingRoomBooking(request);

            assertThatThrownBy(() -> bookingService.createBooking(request))
                    .isInstanceOf(BookingConflictException.class)
                    .hasMessageContaining("data");

            verify(bookingRepository, never()).findOverlappingBookings(any(), any(), any(), any());
            verify(bookingRepository, never()).save(any());
        }
    }

    private void givenValidMeetingRoomBooking(BookingRequestDTO request) {
        Resource resource = new Resource();
        resource.setId(request.getResourceId());
        resource.setType(ResourceTypeEnum.MEETING_ROOM);

        when(userRepository.existsById(request.getUserId())).thenReturn(true);
        when(resourceRepository.findById(request.getResourceId())).thenReturn(Optional.of(resource));
    }

    private BookingRequestDTO bookingRequest(LocalTime startHour, LocalTime endHour) {
        return bookingRequest(BOOKING_DATE, startHour, endHour);
    }

    private BookingRequestDTO bookingRequest(LocalDate date, LocalTime startHour, LocalTime endHour) {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId(USER_ID);
        request.setResourceId(RESOURCE_ID);
        request.setDate(date);
        request.setStartHour(startHour);
        request.setEndHour(endHour);
        return request;
    }

    private Booking bookingEntity() {
        return bookingEntity(bookingRequest(START, END));
    }

    private Booking bookingEntity(BookingRequestDTO request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setResourceId(request.getResourceId());
        booking.setDate(request.getDate());
        booking.setStartHour(request.getStartHour());
        booking.setEndHour(request.getEndHour());
        return booking;
    }

    private MockedStatic<LocalDate> mockCurrentDate() {
        MockedStatic<LocalDate> mockedStatic = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        mockedStatic.when(LocalDate::now).thenReturn(CURRENT_DATE);
        return mockedStatic;
    }

    private MockedStatic<LocalTime> mockCurrentTime() {
        MockedStatic<LocalTime> mockedStatic = Mockito.mockStatic(LocalTime.class, Mockito.CALLS_REAL_METHODS);
        mockedStatic.when(LocalTime::now).thenReturn(CURRENT_TIME);
        return mockedStatic;
    }
}
