package com.workflex.triprecommendation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripBookingRequest {
    @NotNull(message = "User Email cannot be null")
    private String userEmail;

    @NotBlank(message = "Destination country cannot be blank")
    private String destinationCountry;
}
