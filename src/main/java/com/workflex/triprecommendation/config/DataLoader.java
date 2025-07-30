package com.workflex.triprecommendation.config;

import com.opencsv.CSVReader;
import com.workflex.triprecommendation.model.Trip;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.TripRepository;
import com.workflex.triprecommendation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Value("${path.trip-tracking.file}")
    private String csvFilePath;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (tripRepository.count() > 0) {
            log.warn("Data already exists in database. Skipping CSV import.");
            return;
        }

        loadTripsFromCsv();
    }

    private void loadTripsFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource(csvFilePath);
            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));

            List<String[]> records = reader.readAll();
            List<Trip> trips = new ArrayList<>();

            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                if (record.length >= 2) {   // Skip header row
                    String country = record[0].trim().toUpperCase();
                    String email = record[1].trim();
                    Optional<User> userOptional = userRepository.findByEmail(email);
                    if (userOptional.isEmpty()) {
                        User user = userRepository.save(new User(email));
                        trips.add(new Trip(user, country));
                    } else {
                        trips.add(new Trip(userOptional.get(), country));
                    }
                }
            }

            tripRepository.saveAll(trips);
            log.info("Loaded {} trips from CSV file.",trips.size());

        } catch (Exception e) {
            log.error("CSV file not found ,",e);
        }
    }

}
