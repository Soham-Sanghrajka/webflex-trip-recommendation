package com.workflex.triprecommendation.controller;

import com.workflex.triprecommendation.dto.TripBookingRequest;
import com.workflex.triprecommendation.dto.TripBookingResponse;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkflexControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setup() {
        tripRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testBookTrip_success() throws Exception {
        User user = new User(null, "john@example.com");
        user = userRepository.save(user);
        String country = "GRM";
        TripBookingRequest tripBookingRequest = new TripBookingRequest(user.getEmail(), country);
        ResponseEntity<TripBookingResponse> response = testRestTemplate.postForEntity("/workflex/trip", tripBookingRequest, TripBookingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getUser()).isEqualTo(user);
        assertThat(response.getBody().getDestinationCountry()).isEqualTo(country);
    }

    @Test
    void testGetRecommendations_InvalidUser() throws Exception {
        User user = new User(null, "john@example.com");
        user = userRepository.save(user);
        String country = "GRM";
        TripBookingRequest tripBookingRequest = new TripBookingRequest("max@example.com", country);
        ResponseEntity<TripBookingResponse> response = testRestTemplate.postForEntity("/workflex/trip", tripBookingRequest, TripBookingResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
