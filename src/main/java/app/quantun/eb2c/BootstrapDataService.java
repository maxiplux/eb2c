package app.quantun.b2b;


import app.quantun.b2b.model.entity.core.Role;
import app.quantun.b2b.model.entity.core.User;
import app.quantun.b2b.repository.RoleRepository;
import app.quantun.b2b.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BootstrapDataService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;




    @Override
    public void run(String... args) {
        // Create roles if they don't exist
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");
        // Create default users
        createDefaultUser("admin", "ADMIN");
        createDefaultUser("user", "USER");






    }

    private void createDefaultUser(String username, String roleName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(username+"@example.com")
                    .password(passwordEncoder.encode(username))
                    .roles(Set.of(roleRepository.findByName(roleName).get()))
                    .build();
            userRepository.save(user);
            log.info("Created default user: {}", username);
        }
    }


    private void createRoleIfNotFound(String name) {
        roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder().name(name).build();
            return roleRepository.save(role);
        });
    }
}