package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.dto.TripBookingRequest;
import com.workflex.triprecommendation.dto.TripBookingResponse;
import com.workflex.triprecommendation.exception.ResourceNotFoundException;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.repository.UserRepository;
import com.workflex.triprecommendation.service.impl.TripServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void testBookTrip_Success() {
        TripBookingRequest request = new TripBookingRequest("john@example.com", "FRANCE");
        User user = new User(1L, "john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip trip = invocation.getArgument(0);
            trip.setId(101L);
            return trip;
        });

        TripBookingResponse response = tripService.bookTrip(request);

        assertEquals("FRANCE", response.getDestinationCountry());
        assertEquals("Trip booked successfully", response.getMessage());
        assertEquals(user, response.getUser());
    }

    @Test
    void testBookTrip_UserNotFound() {
        TripBookingRequest request = new TripBookingRequest("missing@example.com", "FRANCE");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripService.bookTrip(request));
    }
}
