package com.workflex.triprecommendation.controller;

import com.workflex.triprecommendation.dto.*;
import com.workflex.triprecommendation.service.RecommendationService;
import com.workflex.triprecommendation.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflex")
@RequiredArgsConstructor
public class WorkflexController {

    private final RecommendationService recommendationService;

    private final TripService tripService;

    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam String destinationCountry
    ) {

        if (destinationCountry == null || destinationCountry.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RecommendationResponse response = recommendationService.getRecommendations(
                destinationCountry.trim().toUpperCase());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/trip")
    public ResponseEntity<TripBookingResponse> bookTrip(@Valid @RequestBody TripBookingRequest request) {

        request.setDestinationCountry(request.getDestinationCountry().trim().toUpperCase());

        TripBookingResponse response = tripService.bookTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}