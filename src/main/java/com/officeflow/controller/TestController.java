package com.officeflow.controller;

import com.officeflow.dto.request.BookingRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping
    public BookingRequestDTO test(@RequestBody BookingRequestDTO request) {
        System.out.println("DTO ricevuto: " + request);
        return request;
    }
}
