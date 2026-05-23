package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.dto.request.LoginRequest;
import pe.edu.utec.nutritrack.dto.request.RegisterRequest;
import pe.edu.utec.nutritrack.dto.request.RefreshTokenRequest;
import pe.edu.utec.nutritrack.dto.response.LoginResponse;
import pe.edu.utec.nutritrack.dto.response.RegisterResponse;
import pe.edu.utec.nutritrack.exception.InvalidCredentialsException;
import pe.edu.utec.nutritrack.exception.UserAlreadyExistsException;
import pe.edu.utec.nutritrack.mapper.UserMapper;
import pe.edu.utec.nutritrack.model.Role;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.UserRepository;
import pe.edu.utec.nutritrack.security.JwtService;
import pe.edu.utec.nutritrack.event.UserRegisteredEvent;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está registrado en la base de datos.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado en la base de datos.");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        if (request.getUsername().toLowerCase().contains("admin")) {
            roles.add(Role.ROLE_ADMIN);
        }
        if (request.getUsername().toLowerCase().contains("manager")) {
            roles.add(Role.ROLE_MANAGER);
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Publish event for sending welcome email async
        eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser));

        return userMapper.toResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Nombre de usuario o contraseña incorrectos en el login.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600000L) // 1 hour
                .tokenType("Bearer")
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Token de actualización inválido.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidCredentialsException("Token de actualización inválido o expirado.");
        }

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(3600000L) // 1 hour
                .tokenType("Bearer")
                .build();
    }
}
