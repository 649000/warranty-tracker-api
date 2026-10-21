package com.nazri.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNull(user.getFirebaseUid());
        assertNull(user.getEmail());
        assertNull(user.getDisplayName());
    }

    @Test
    void testConstructorWithFirebaseUidAndEmail() {
        User user = new User("uid-123", "test@example.com");
        assertEquals("uid-123", user.getFirebaseUid());
        assertEquals("test@example.com", user.getEmail());
        assertNull(user.getDisplayName());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testConstructorWithAllFields() {
        User user = new User("uid-123", "test@example.com", "John Doe");
        assertEquals("uid-123", user.getFirebaseUid());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John Doe", user.getDisplayName());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.id = 42L;
        user.setFirebaseUid("uid-456");
        user.setEmail("user@domain.com");
        user.setDisplayName("Jane Doe");

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setUserProducts(new ArrayList<>());

        assertEquals(42L, user.id);
        assertEquals("uid-456", user.getFirebaseUid());
        assertEquals("user@domain.com", user.getEmail());
        assertEquals("Jane Doe", user.getDisplayName());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertNotNull(user.getUserProducts());
        assertTrue(user.toString().contains("Jane Doe"));
    }
}
