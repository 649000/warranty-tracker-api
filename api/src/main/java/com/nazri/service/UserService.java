package com.nazri.service;

import com.nazri.model.User;
import com.nazri.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    
    @Inject
    UserRepository userRepository;
    
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
     * @param photoUrl the user's photo URL
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
     * Update user profile information
     * @param firebaseUid the Firebase UID of the user
     * @param displayName the new display name
     * @param photoUrl the new photo URL
     * @return the updated user or empty optional if user not found
     */
    @Transactional
    public Optional<User> updateUserProfile(String firebaseUid, String displayName, String photoUrl) {
        Optional<User> userOptional = userRepository.findByFirebaseUid(firebaseUid);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDisplayName(displayName);
            user.setPhotoUrl(photoUrl);
            userRepository.updateUser(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
