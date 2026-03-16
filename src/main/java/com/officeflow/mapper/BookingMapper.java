package com.officeflow.mapper;

import com.officeflow.domain.Booking;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toEntity(BookingRequestDTO dto);

    BookingResponseDTO toResponse(Booking booking);
}