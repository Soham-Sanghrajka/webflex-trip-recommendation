package com.workflex.triprecommendation.service.impl;

import com.workflex.triprecommendation.dto.RecommendationResponse;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final TripRepository tripRepository;

    public RecommendationResponse getRecommendations(String destinationCountry) {
        log.info("Getting recommendations for destination: {}", destinationCountry);

        // Get all users who visited the given destination
        List<Long> usersWhoVisitedDestination = tripRepository.findUsersByDestinationCountry(destinationCountry);

        if (usersWhoVisitedDestination.isEmpty()) {
            log.warn("No users found who visited destination: {}", destinationCountry);
            return new RecommendationResponse(destinationCountry, new ArrayList<>());
        }

        log.debug("Found {} users who visited {}", usersWhoVisitedDestination.size(), destinationCountry);

        // Get all trips by these users (excluding the source destination)
        List<Trip> relatedTrips = tripRepository.findTripsByUsersExcludingCountry(
                usersWhoVisitedDestination, destinationCountry);

        log.debug("Found {} related trips for recommendation calculation", relatedTrips.size());

        // Build user-country matrix for cosine similarity calculation
        Map<String, Map<Long, Integer>> countryUserMatrix = buildCountryUserMatrix(relatedTrips);
        Map<Long, Integer> sourceCountryVector = buildUserVector(usersWhoVisitedDestination);

        // Calculate cosine similarities
        List<RecommendationResponse.CountryRecommendation> recommendations =
                calculateCosineSimilarities(sourceCountryVector, countryUserMatrix);

        log.info("Generated {} recommendations for destination: {}", recommendations.size(), destinationCountry);
        return new RecommendationResponse(destinationCountry, recommendations);
    }

    private Map<String, Map<Long, Integer>> buildCountryUserMatrix(List<Trip> trips) {
        Map<String, Map<Long, Integer>> countryUserMatrix = new HashMap<>();

        for (Trip trip : trips) {
            String country = trip.getDestinationCountry();
            Long userId = trip.getUser().getId();

            countryUserMatrix.computeIfAbsent(country, k -> new HashMap<>())
                    .merge(userId, 1, Integer::sum);
        }

        log.debug("Built country-user matrix with {} countries", countryUserMatrix.size());
        return countryUserMatrix;
    }

    private Map<Long, Integer> buildUserVector(List<Long> users) {
        Map<Long, Integer> userVector = new HashMap<>();
        for (Long userId : users) {
            userVector.put(userId, 1);
        }
        return userVector;
    }

    private List<RecommendationResponse.CountryRecommendation> calculateCosineSimilarities(
            Map<Long, Integer> sourceVector, Map<String, Map<Long, Integer>> countryUserMatrix) {

        List<RecommendationResponse.CountryRecommendation> recommendations = new ArrayList<>();

        for (Map.Entry<String, Map<Long, Integer>> entry : countryUserMatrix.entrySet()) {
            String country = entry.getKey();
            Map<Long, Integer> targetVector = entry.getValue();

            double similarity = calculateCosineSimilarity(sourceVector, targetVector);
            int visitCount = targetVector.values().stream().mapToInt(Integer::intValue).sum();

            if (similarity > 0) {
                recommendations.add(new RecommendationResponse.CountryRecommendation(
                        country, similarity, visitCount));
                log.debug("Calculated similarity for {}: {}", country, similarity);
            }
        }

        // Sort by similarity (descending) and then by visit count (descending)
        recommendations.sort((a, b) -> {
            int similarityCompare = Double.compare(b.getSimilarity(), a.getSimilarity());
            if (similarityCompare != 0) {
                return similarityCompare;
            }
            return Integer.compare(b.getVisitCount(), a.getVisitCount());
        });

        return recommendations;
    }

    private double calculateCosineSimilarity(Map<Long, Integer> vectorA, Map<Long, Integer> vectorB) {
        Set<Long> commonUsers = new HashSet<>(vectorA.keySet());
        commonUsers.retainAll(vectorB.keySet());

        if (commonUsers.isEmpty()) {
            return 0.0;
        }

        // Calculate dot product
        double dotProduct = 0.0;
        for (Long userId : commonUsers) {
            dotProduct += vectorA.get(userId) * vectorB.get(userId);
        }

        // Calculate magnitudes
        double magnitudeA = Math.sqrt(vectorA.values().stream()
                .mapToDouble(val -> val * val).sum());
        double magnitudeB = Math.sqrt(vectorB.values().stream()
                .mapToDouble(val -> val * val).sum());

        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            return 0.0;
        }

        double similarity = dotProduct / (magnitudeA * magnitudeB);
        log.trace("Cosine similarity calculated: dotProduct={}, magnitudeA={}, magnitudeB={}, similarity={}",
                dotProduct, magnitudeA, magnitudeB, similarity);

        return similarity;
    }
}
