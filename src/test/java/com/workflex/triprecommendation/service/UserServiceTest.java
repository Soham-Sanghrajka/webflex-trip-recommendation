package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.exception.DuplicateResourceException;
import com.workflex.triprecommendation.exception.ResourceNotFoundException;
import com.workflex.triprecommendation.model.User;
import com.workflex.triprecommendation.repository.UserRepository;
import com.workflex.triprecommendation.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_success() {
        User user = new User("john@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser_duplicateEmail() {
        User user = new User("john@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(user));
    }

    @Test
    void testDeleteUser_success() {
        User user = new User(1L, "john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
