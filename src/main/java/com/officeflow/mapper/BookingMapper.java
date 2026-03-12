package com.officeflow.mapper;

import com.officeflow.domain.Booking;
import com.officeflow.dto.request.BookingRequestDTO;
import com.officeflow.dto.response.BookingResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    // Ignoriamo l'id perché non arriva dal DTO di richiesta,
    // ma verrà gestito dal database.
    @Mapping(target = "id", ignore = true)
    Booking toEntity(BookingRequestDTO dto);

    // Qui non serve ignorarlo perché l'id esiste nell'entità
    // e vogliamo che venga copiato nel ResponseDTO.
    BookingResponseDTO toResponse(Booking booking);
}