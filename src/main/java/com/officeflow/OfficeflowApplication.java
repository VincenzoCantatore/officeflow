package com.officeflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.officeflow")
public class OfficeflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficeflowApplication.class, args);
    }

}
