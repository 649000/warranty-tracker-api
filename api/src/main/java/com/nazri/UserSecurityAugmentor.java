package com.nazri;

import com.nazri.service.UserService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserSecurityAugmentor implements SecurityIdentityAugmentor {

    @Inject
    UserService userService;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        String firebaseUid = identity.getPrincipal().getName();

        // Use the async method from userService
        return userService.findByFirebaseUidAsync(firebaseUid)
                .map(userOptional -> {
                    if (userOptional.isPresent()) {
                        // Convert to a QuarkusSecurityIdentity.Builder
                        QuarkusSecurityIdentity.Builder builder;
                        if (identity instanceof QuarkusSecurityIdentity qsi) {
                            builder = QuarkusSecurityIdentity.builder(qsi);
                        } else {
                            builder = QuarkusSecurityIdentity.builder(identity);
                        }

                        // Add our custom user attribute
                        builder.addAttribute("user", userOptional.get());

                        // Build new identity
                        return builder.build();
                    } else {
                        // User not found, return original identity
                        return identity;
                    }
                });
    }
}
