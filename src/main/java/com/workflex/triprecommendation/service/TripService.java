package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.dto.TripBookingRequest;
import com.workflex.triprecommendation.dto.TripBookingResponse;

public interface TripService {

    TripBookingResponse bookTrip(TripBookingRequest request);
}
