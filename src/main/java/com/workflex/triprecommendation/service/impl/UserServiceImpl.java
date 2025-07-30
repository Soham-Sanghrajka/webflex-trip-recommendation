package com.workflex.triprecommendation.service.impl;

import com.workflex.triprecommendation.exception.DuplicateResourceException;
import com.workflex.triprecommendation.exception.ResourceNotFoundException;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.UserRepository;
import com.workflex.triprecommendation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public User createUser(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            log.error("User already exist for given email: {}",user.getEmail());
            throw new DuplicateResourceException("User already exist for given email: "+user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id : "+userId);
        }
        userRepository.deleteById(userId);
    }
}
