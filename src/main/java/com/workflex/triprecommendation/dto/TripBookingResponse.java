package com.workflex.triprecommendation.dto;

import com.workflex.triprecommendation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripBookingResponse {
    private Long tripId;
    private User user;
    private String destinationCountry;
    private LocalDateTime bookedAt;
    private String message;
}
