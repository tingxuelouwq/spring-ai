package com.kevin.springai.flightbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FlightBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightBookingApplication.class, args);
    }

    @Bean
    public WebClientCustomizer webClientCustomizer() {
        return (builder) -> {
            builder.defaultHeader("Authorization","Bearer eyJraWQiOiI4OThjNWM1Yy0wYjNmLTQ2NTYtOTQyMy0xNGE5OWUwNjgxNzgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJrZXZpbiIsImF1ZCI6ImtldmluIiwibmJmIjoxNzg4MDA3NDcwLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODgiLCJleHAiOjE3ODgwMDc3NzAsImlhdCI6MTc4ODAwNzQ3MCwianRpIjoiMzRlZTRjNzMtZTBlOS00ODMxLWE3NGEtYWJjNGQ4ZWQ1MWQxIn0.kZqDjU_AOZg1gyzgHaCZGnGO58tRzEgYNWP37WPUa-xEMuNZJI0NlJ_9jqswBClzrTcojqfqZPd5p1s7qwj5lDlpp6pS_2tTMC5HvUk_36GgrPd24pPgtPtryNiXtkgT2BCKJxLemmCvQEu89wBYthCOoy3E5ccDTatVBcM_eEKM9uJ9360QsrR4D-2DL26_VKrFSruAd57wh_cWkIJ6Ik-Vy_LfQMT8hj9DyJ8p-ZonC31zq6mdTBN4vwd9-Rn23mcJSXsKRry9CLQsQk1HNnCpA16GhZzBPIZ9yrr0bo9kBJV1IQuw6Ay-vNqhIjPs75HJDxYU-xKVZNtjL-3c8Q");
        };
    }
}
