package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.model.Role;
import pe.edu.utec.nutritrack.model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        // Given
        User user = User.builder()
                .username("test.user")
                .email("test.user@utec.edu.pe")
                .password("encoded_password")
                .createdAt(LocalDateTime.now())
                .roles(Set.of(Role.ROLE_USER))
                .build();

        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("test.user");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test.user@utec.edu.pe");
    }

    @Test
    void shouldReturnTrueWhenUsernameOrEmailExists() {
        // Given
        User user = User.builder()
                .username("fitness.pro")
                .email("pro@utec.edu.pe")
                .password("encoded_password")
                .createdAt(LocalDateTime.now())
                .roles(Set.of(Role.ROLE_USER))
                .build();

        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByUsername("fitness.pro")).isTrue();
        assertThat(userRepository.existsByEmail("pro@utec.edu.pe")).isTrue();
        assertThat(userRepository.existsByUsername("unknown")).isFalse();
    }
}
