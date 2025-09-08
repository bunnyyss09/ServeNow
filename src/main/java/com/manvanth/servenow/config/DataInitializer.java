package com.manvanth.servenow.config;

import com.manvanth.servenow.entity.Role;
import com.manvanth.servenow.entity.User;
import com.manvanth.servenow.repository.RoleRepository;
import com.manvanth.servenow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Data initialization component to ensure default admin user exists
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Always ensure admin user exists with correct credentials
        User adminUser = userRepository.findByEmailAndIsActiveTrue("admin@servenow.com").orElse(null);
        
        if (adminUser == null) {
            log.info("Admin user not found, creating default admin user...");
            createDefaultAdminUser();
        } else {
            log.info("Admin user exists, ensuring correct credentials...");
            
            // Always update password to ensure it's correct
            String testPassword = "admin123";
            adminUser.setPassword(passwordEncoder.encode(testPassword));
            
            // Ensure phone number is in correct format
            if (adminUser.getPhoneNumber() != null && adminUser.getPhoneNumber().contains("-")) {
                log.warn("Fixing phone number format...");
                adminUser.setPhoneNumber(adminUser.getPhoneNumber().replace("-", ""));
            } else if (adminUser.getPhoneNumber() == null) {
                adminUser.setPhoneNumber("+15550001");
            }
            
            // Ensure all required fields are set
            adminUser.setIsEmailVerified(true);
            adminUser.setIsPhoneVerified(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialsNonExpired(true);
            adminUser.setEnabled(true);
            adminUser.setIsActive(true);
            
            userRepository.save(adminUser);
            log.info("Admin user credentials updated successfully");
        }
        
        log.info("Data initialization completed");
    }

    private void createDefaultAdminUser() {
        try {
            // Find or create ADMIN role
            Role adminRole = roleRepository.findByNameAndIsActiveTrue("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found in database"));

            // Create admin user
            User adminUser = new User();
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setEmail("admin@servenow.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setPhoneNumber("+15550001");
            adminUser.setAddress("123 Admin Street");
            adminUser.setCity("Admin City");
            adminUser.setState("AC");
            adminUser.setPostalCode("12345");
            adminUser.setCountry("USA");
            adminUser.setIsEmailVerified(true);
            adminUser.setIsPhoneVerified(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialsNonExpired(true);
            adminUser.setEnabled(true);
            adminUser.setIsActive(true);

            // Initialize roles set and add ADMIN role
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            // Save admin user
            User savedAdmin = userRepository.save(adminUser);
            log.info("Default admin user created successfully with ID: {}", savedAdmin.getId());

        } catch (Exception e) {
            log.error("Failed to create default admin user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize default admin user", e);
        }
    }
}