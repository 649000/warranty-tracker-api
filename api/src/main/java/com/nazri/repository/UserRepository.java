package com.nazri.repository;

import com.nazri.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    /**
     * Find a user by their Firebase UID
     * @param firebaseUid the Firebase UID
     * @return Optional containing the user if found
     */
    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return find("firebaseUid", firebaseUid).firstResultOptional();
    }
    
    /**
     * Find a user by their email
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    /**
     * Check if a user exists by their Firebase UID
     * @param firebaseUid the Firebase UID
     * @return true if user exists, false otherwise
     */
    public boolean existsByFirebaseUid(String firebaseUid) {
        return count("firebaseUid", firebaseUid) > 0;
    }
    
    /**
     * Create a new user
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        user.createdAt = java.time.LocalDateTime.now();
        user.updatedAt = java.time.LocalDateTime.now();
        persistAndFlush(user);
        return user;
    }
    
    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        user.updatedAt = java.time.LocalDateTime.now();
        getEntityManager().merge(user);
        return user;
    }
}
