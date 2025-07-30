package com.workflex.triprecommendation.service.impl;

import com.workflex.triprecommendation.dto.TripBookingRequest;
import com.workflex.triprecommendation.dto.TripBookingResponse;
import com.workflex.triprecommendation.exception.ResourceNotFoundException;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.repository.UserRepository;
import com.workflex.triprecommendation.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    public TripBookingResponse bookTrip(TripBookingRequest request) {
        log.info("Booking trip for user {} to {}", request.getUserEmail(), request.getDestinationCountry());

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email :"+request.getUserEmail()));

        Trip trip = new Trip(user, request.getDestinationCountry());
        Trip savedTrip = tripRepository.save(trip);

        log.info("Successfully booked trip with ID {} for user {} to {}",
                savedTrip.getId(), user.getId(), savedTrip.getDestinationCountry());

        return new TripBookingResponse(
                savedTrip.getId(),
                savedTrip.getUser(),
                savedTrip.getDestinationCountry(),
                savedTrip.getCreatedAt(),
                "Trip booked successfully"
        );
    }
}
