package com.officeflow.repository;

import com.officeflow.domain.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    @Query("{ 'resourceId': ?0, 'date': ?1, 'startHour': { '$lt': ?3 }, 'endHour': { '$gt': ?2 } }")
    List<Booking> findOverlappingBookings(String resourceId, LocalDate date, LocalTime startHour, LocalTime endHour);

    boolean existsByUserIdAndDate(String userId, LocalDate date);

    List<Booking> findByDateAndEndHourBeforeAndStatusNot(LocalDate date, LocalTime time, String status);

    List<Booking> findByDateBeforeAndStatusNot (LocalDate date , String status);

}