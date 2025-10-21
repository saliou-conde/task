package ch.sintere.task.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider") // <-- Very Important, the Name must be exact so
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("Starting getCurrentAuditor() ...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM"); // fallback
        }
        // Keycloak Principal (username)
        String username = authentication.getName();
        log.info("Logged user: {}", username);
        log.info("Finished getCurrentAuditor() successfully");
        return Optional.of(username);
    }
}

