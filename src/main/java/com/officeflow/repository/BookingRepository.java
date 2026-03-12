package com.officeflow.repository;

import com.officeflow.domain.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking , String> {


}
