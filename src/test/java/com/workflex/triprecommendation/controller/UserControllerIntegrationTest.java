package com.workflex.triprecommendation.controller;

import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteByEmail("john@example.com"); // Delete used record for testing each time
    }

    @Test
    void testCreateUser_andDeleteUser() {
        User user = new User("john@example.com");

        // Create user
        ResponseEntity<User> response = restTemplate.postForEntity("/workflex/users", user, User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        Long userId = response.getBody().getId();

        // Delete user
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/workflex/users/" + userId, HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteNonExistingUser() {
        ResponseEntity<String> response = restTemplate.exchange("/workflex/users/999", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
