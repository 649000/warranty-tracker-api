package com.nazri.repository;

import com.nazri.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Test
    void testFindByFirebaseUid() {
        UserRepository repo = spy(new UserRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        User user = new User("uid-1", "user@example.com");
        when(query.firstResultOptional()).thenReturn(Optional.of(user));
        doReturn(query).when(repo).find(eq("firebaseUid"), any(Object[].class));

        Optional<User> result = repo.findByFirebaseUid("uid-1");
        assertTrue(result.isPresent());
        assertEquals("uid-1", result.get().getFirebaseUid());
    }

    @Test
    void testFindByEmail() {
        UserRepository repo = spy(new UserRepository());
        PanacheQuery query = mock(PanacheQuery.class);
        User user = new User("uid-1", "user@example.com");
        when(query.firstResultOptional()).thenReturn(Optional.of(user));
        doReturn(query).when(repo).find(eq("email"), any(Object[].class));

        Optional<User> result = repo.findByEmail("user@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void testExistsByFirebaseUid() {
        UserRepository repo = spy(new UserRepository());
        doReturn(1L).when(repo).count(eq("firebaseUid"), any(Object[].class));
        assertTrue(repo.existsByFirebaseUid("uid-1"));

        doReturn(0L).when(repo).count(eq("firebaseUid"), any(Object[].class));
        assertFalse(repo.existsByFirebaseUid("uid-2"));
    }

    @Test
    void testCreateUser() {
        UserRepository repo = spy(new UserRepository());
        doNothing().when(repo).persistAndFlush(any());

        User user = new User();
        User created = repo.createUser(user);

        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(repo).persistAndFlush(user);
    }

    @Test
    void testUpdateUser() {
        UserRepository repo = spy(new UserRepository());
        EntityManager em = mock(EntityManager.class);
        doReturn(em).when(repo).getEntityManager();

        User user = new User();
        User updated = repo.updateUser(user);

        assertNotNull(updated.getUpdatedAt());
        verify(em).merge(user);
    }
}
