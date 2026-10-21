package com.nazri.security;

import com.nazri.UserSecurityAugmentor;
import com.nazri.model.User;
import com.nazri.service.UserService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSecurityAugmentorTest {

    @Mock
    UserService userService;

    @Mock
    AuthenticationRequestContext authContext;

    @InjectMocks
    UserSecurityAugmentor augmentor;

    @BeforeEach
    void setUp() {
        lenient().when(authContext.runBlocking(any())).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return Uni.createFrom().item(supplier.get());
        });
    }

    @Test
    void testAugment_AnonymousIdentity() {
        SecurityIdentity anonymousIdentity = QuarkusSecurityIdentity.builder()
                .setAnonymous(true)
                .build();

        Uni<SecurityIdentity> resultUni = augmentor.augment(anonymousIdentity, authContext);
        SecurityIdentity result = resultUni.await().indefinitely();

        assertSame(anonymousIdentity, result);
        verifyNoInteractions(authContext);
        verifyNoInteractions(userService);
    }

    @Test
    void testAugment_AuthenticatedUserFound() {
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal("firebase-123"))
                .build();

        User user = new User("firebase-123", "user@example.com", "John");
        user.id = 10L;

        when(userService.findByFirebaseUid("firebase-123")).thenReturn(Optional.of(user));

        Uni<SecurityIdentity> resultUni = augmentor.augment(identity, authContext);
        SecurityIdentity result = resultUni.await().indefinitely();

        assertNotNull(result);
        assertEquals("firebase-123", result.getPrincipal().getName());
        assertEquals(user, result.getAttribute("user"));
    }

    @Test
    void testAugment_AuthenticatedUserNotFound() {
        SecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal("firebase-notfound"))
                .build();

        when(userService.findByFirebaseUid("firebase-notfound")).thenReturn(Optional.empty());

        Uni<SecurityIdentity> resultUni = augmentor.augment(identity, authContext);
        SecurityIdentity result = resultUni.await().indefinitely();

        assertSame(identity, result);
        assertNull(result.getAttribute("user"));
    }
}
