package com.nazri.service;

import com.nazri.model.User;
import com.nazri.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("uid-123", "test@example.com", "Test User");
        sampleUser.id = 1L;
    }

    @Test
    void testFindByFirebaseUid_Found() {
        when(userRepository.findByFirebaseUid("uid-123")).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.findByFirebaseUid("uid-123");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByFirebaseUid("uid-123");
    }

    @Test
    void testFindByFirebaseUid_NotFound() {
        when(userRepository.findByFirebaseUid("uid-999")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByFirebaseUid("uid-999");

        assertFalse(result.isPresent());
        verify(userRepository).findByFirebaseUid("uid-999");
    }

    @Test
    void testFindByEmail_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("uid-123", result.get().getFirebaseUid());
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByFirebaseUid_True() {
        when(userRepository.existsByFirebaseUid("uid-123")).thenReturn(true);

        assertTrue(userService.existsByFirebaseUid("uid-123"));
    }

    @Test
    void testExistsByFirebaseUid_False() {
        when(userRepository.existsByFirebaseUid("uid-999")).thenReturn(false);

        assertFalse(userService.existsByFirebaseUid("uid-999"));
    }

    @Test
    void testCreateUser() {
        when(userRepository.createUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("uid-new", "new@example.com", "New User");

        assertNotNull(created);
        assertEquals("uid-new", created.getFirebaseUid());
        assertEquals("new@example.com", created.getEmail());
        assertEquals("New User", created.getDisplayName());
        verify(userRepository).createUser(any(User.class));
    }

    @Test
    void testUpdateUser() {
        when(userRepository.updateUser(sampleUser)).thenReturn(sampleUser);

        User updated = userService.updateUser(sampleUser);

        assertNotNull(updated);
        assertEquals(sampleUser.id, updated.id);
        verify(userRepository).updateUser(sampleUser);
    }

    @Test
    void testGetAllUsers() {
        List<User> list = Arrays.asList(sampleUser, new User("uid-2", "user2@example.com"));
        when(userRepository.listAll()).thenReturn(list);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).listAll();
    }

    @Test
    void testFindUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(sampleUser);

        Optional<User> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id);
    }

    @Test
    void testFindUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(null);

        Optional<User> result = userService.findUserById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteUserById_Success() {
        when(userRepository.deleteById(1L)).thenReturn(true);

        boolean deleted = userService.deleteUserById(1L);

        assertTrue(deleted);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserById_NotFound() {
        when(userRepository.deleteById(999L)).thenReturn(false);

        boolean deleted = userService.deleteUserById(999L);

        assertFalse(deleted);
    }

    @Test
    void testUpdateUserById_Found_BothFieldsUpdated() {
        when(userRepository.findById(1L)).thenReturn(sampleUser);
        when(userRepository.updateUser(any(User.class))).thenReturn(sampleUser);

        Optional<User> result = userService.updateUserById(1L, "updated@example.com", "Updated Name");

        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("Updated Name", result.get().getDisplayName());
        verify(userRepository).updateUser(sampleUser);
    }

    @Test
    void testUpdateUserById_Found_NullFieldsNotOverwriting() {
        when(userRepository.findById(1L)).thenReturn(sampleUser);
        when(userRepository.updateUser(any(User.class))).thenReturn(sampleUser);

        Optional<User> result = userService.updateUserById(1L, null, null);

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("Test User", result.get().getDisplayName());
    }

    @Test
    void testUpdateUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(null);

        Optional<User> result = userService.updateUserById(999L, "new@example.com", "New");

        assertFalse(result.isPresent());
        verify(userRepository, never()).updateUser(any(User.class));
    }
}
