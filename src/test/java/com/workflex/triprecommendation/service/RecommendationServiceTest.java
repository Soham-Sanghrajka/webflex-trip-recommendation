package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.dto.RecommendationResponse;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Test
    void testGetRecommendations_WhenUsersExist() {
        String destination = "JPN";

        // Mock data
        List<Long> userIds = List.of(1L, 2L);
        List<Trip> trips = List.of(
                new Trip(1L, new User(1L, "a@example.com"), "GRM", LocalDateTime.now()),
                new Trip(2L, new User(2L, "b@example.com"), "FRA", LocalDateTime.now()),
                new Trip(3L, new User(1L, "a@example.com"), "FRA", LocalDateTime.now())
        );

        when(tripRepository.findUsersByDestinationCountry(destination)).thenReturn(userIds);
        when(tripRepository.findTripsByUsersExcludingCountry(userIds, destination)).thenReturn(trips);

        RecommendationResponse response = recommendationService.getRecommendations(destination);

        // FRA have more visitor so high recommendation
        assertThat(response.getRecommendations().get(0).getCountry()).isEqualTo("FRA");
    }

    @Test
    void testGetRecommendations_NoUsers() {
        String destination = "BRZ";

        when(tripRepository.findUsersByDestinationCountry(destination)).thenReturn(Collections.emptyList());

        RecommendationResponse response = recommendationService.getRecommendations(destination);

        assertTrue(response.getRecommendations().isEmpty());
    }
}
