package com.workflex.triprecommendation.controller;

import com.workflex.triprecommendation.dto.RecommendationResponse;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // delete records that are created for testing if present
        tripRepository.deleteAll();
        userRepository.deleteAll();

        // Setup data for recommendation test
        User user1 = userRepository.save(new User(null, "user1@example.com"));
        User user2 = userRepository.save(new User(null, "user2@example.com"));

        tripRepository.saveAll(List.of(
                new Trip(user1, "JPN"),
                new Trip(user1, "GRM"),
                new Trip(user2, "JPN"),
                new Trip(user2, "FRA")
        ));
    }


    @Test
    void testGetRecommendations_CountryAvailable() throws Exception {
        ResponseEntity<RecommendationResponse> response = restTemplate
                .exchange("/workflex/recommendations?destinationCountry=JPN", HttpMethod.GET, null, RecommendationResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testGetRecommendations_CountryNotAvailable() throws Exception {
        ResponseEntity<RecommendationResponse> response = restTemplate
                .exchange("/workflex/recommendations?destinationCountry=USD", HttpMethod.GET, null, RecommendationResponse.class);

        assertThat(response.getBody().getRecommendations()).isEqualTo(List.of());
    }
}
