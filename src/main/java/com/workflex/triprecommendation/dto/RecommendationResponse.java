package com.workflex.triprecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private String sourceCountry;
    private List<CountryRecommendation> recommendations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryRecommendation {
        private String country;
        private double similarity;
        private int visitCount;
    }
}
