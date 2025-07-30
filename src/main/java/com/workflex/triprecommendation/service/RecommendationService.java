package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.dto.RecommendationResponse;

/**
 * The interface Recommendation service.
 */
public interface RecommendationService {

    /**
     * Gets recommendations.
     *
     * @param destinationCountry the destination country
     * @return the recommendations
     */
    RecommendationResponse getRecommendations(String destinationCountry);
}
