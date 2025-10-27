package com.nazri.service;

import com.nazri.model.User;
import com.nazri.repository.UserRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    
    @Inject
    UserRepository userRepository;
    
    /**
     * Find a user by their Firebase UID (async version)
     * @param firebaseUid the Firebase UID
     * @return Uni containing Optional with the user if found
     */
    public Uni<Optional<User>> findByFirebaseUidAsync(String firebaseUid) {
        return userRepository.findByFirebaseUidAsync(firebaseUid);
    }
    
    /**
     * Find a user by their Firebase UID
     * @param firebaseUid the Firebase UID
     * @return Optional containing the user if found
     */
    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
    
    /**
     * Find a user by their email
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Check if a user exists by their Firebase UID
     * @param firebaseUid the Firebase UID
     * @return true if user exists, false otherwise
     */
    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }
    
    /**
     * Create a new user
     * @param firebaseUid the Firebase UID
     * @param email the user's email
     * @param displayName the user's display name
     * @return the created user
     */
    @Transactional
    public User createUser(String firebaseUid, String email, String displayName) {
        User user = new User(firebaseUid, email, displayName);
        return userRepository.createUser(user);
    }
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     */
    @Transactional
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }
    
    /**
     * Get all users (admin only)
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.listAll();
    }
    
    /**
     * Find user by ID (admin only)
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }
    
    /**
     * Delete user by ID (admin only)
     * @param id the user ID
     * @return true if user was deleted, false if not found
     */
    @Transactional
    public boolean deleteUserById(Long id) {
        return userRepository.deleteById(id);
    }
    
    /**
     * Update user by ID (admin only)
     * @param id the user ID
     * @param email the new email
     * @param displayName the new display name
     * @return Optional containing the updated user if found
     */
    @Transactional
    public Optional<User> updateUserById(Long id, String email, String displayName) {
        Optional<User> userOptional = findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (email != null) {
                user.setEmail(email);
            }
            if (displayName != null) {
                user.setDisplayName(displayName);
            }
            userRepository.updateUser(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
