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

//    @Override
//    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
//        // Skip if anonymous
//        if (identity.isAnonymous()) {
//            return Uni.createFrom().item(identity);
//        }
//
//        // Extract Firebase UID from JWT (usually the subject)
//        String firebaseUid = identity.getPrincipal().getName();
//
//        // Resolve user from database
//        return userService.findByFirebaseUid(firebaseUid)
//            .map(user -> {
//                if (user != null) {
//                    // Add user to security identity
//                    return identity.withAttribute("user", user);
//                } else {
//                    // User not found in database - return anonymous identity
//                    return identity;
//                }
//            })
//            .orElse(Uni.createFrom().item(identity));
//    }

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        String firebaseUid = identity.getPrincipal().getName();

        return Uni.createFrom().item(() -> {
            return userService.findByFirebaseUid(firebaseUid)
                    .map(user -> {
                        // Convert to a QuarkusSecurityIdentity.Builder
                        QuarkusSecurityIdentity.Builder builder;
                        if (identity instanceof QuarkusSecurityIdentity qsi) {
                            builder = QuarkusSecurityIdentity.builder(qsi);
                        } else {
                            builder = QuarkusSecurityIdentity.builder(identity);
                        }

                        // Add our custom user attribute
                        builder.addAttribute("user", user);

                        // Build new identity
                        return builder.build();
                    })
                    .orElse((QuarkusSecurityIdentity) identity);
        });
    }
}
