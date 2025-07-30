package com.workflex.triprecommendation.service;

import com.workflex.triprecommendation.model.User;

/**
 * The interface User service.
 */
public interface UserService {
    /**
     * Create user user.
     *
     * @param user the user
     * @return the user
     */
    User createUser(User user);

    /**
     * Delete user user.
     *
     * @param userId the user id
     */
    void deleteUser(Long userId);
}
