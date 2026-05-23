package pe.edu.utec.nutritrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.utec.nutritrack.dto.request.LoginRequest;
import pe.edu.utec.nutritrack.dto.request.RegisterRequest;
import pe.edu.utec.nutritrack.dto.request.RefreshTokenRequest;
import pe.edu.utec.nutritrack.dto.response.LoginResponse;
import pe.edu.utec.nutritrack.dto.response.RegisterResponse;
import pe.edu.utec.nutritrack.exception.InvalidCredentialsException;
import pe.edu.utec.nutritrack.exception.UserAlreadyExistsException;
import pe.edu.utec.nutritrack.mapper.UserMapper;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.UserRepository;
import pe.edu.utec.nutritrack.security.JwtService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("new.user")
                .email("new@utec.edu.pe")
                .password("plain_pass")
                .build();

        User userEntity = User.builder()
                .username("new.user")
                .email("new@utec.edu.pe")
                .build();

        RegisterResponse responseMock = RegisterResponse.builder()
                .id(1L)
                .username("new.user")
                .email("new@utec.edu.pe")
                .build();

        when(userRepository.existsByUsername("new.user")).thenReturn(false);
        when(userRepository.existsByEmail("new@utec.edu.pe")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("plain_pass")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(responseMock);

        // When
        RegisterResponse result = authService.register(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("new.user");
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void register_UsernameExists_ThrowsException() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("existing.user")
                .build();

        when(userRepository.existsByUsername("existing.user")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("ya está registrado");
    }

    @Test
    void register_EmailExists_ThrowsException() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("new.user")
                .email("existing@utec.edu.pe")
                .build();

        when(userRepository.existsByUsername("new.user")).thenReturn(false);
        when(userRepository.existsByEmail("existing@utec.edu.pe")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("ya está registrado");
    }

    @Test
    void login_Success() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("fitness.pro")
                .password("correct_pass")
                .build();

        UserDetails userDetailsMock = mock(UserDetails.class);

        when(userDetailsService.loadUserByUsername("fitness.pro")).thenReturn(userDetailsMock);
        when(jwtService.generateToken(userDetailsMock)).thenReturn("access_token_123");
        when(jwtService.generateRefreshToken(userDetailsMock)).thenReturn("refresh_token_123");

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token_123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token_123");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("fitness.pro")
                .password("wrong_pass")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad Credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("incorrectos");
    }

    @Test
    void refresh_Success() {
        // Given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid_refresh_token")
                .build();

        UserDetails userDetailsMock = mock(UserDetails.class);

        when(jwtService.extractUsername("valid_refresh_token")).thenReturn("fitness.pro");
        when(userDetailsService.loadUserByUsername("fitness.pro")).thenReturn(userDetailsMock);
        when(jwtService.isTokenValid("valid_refresh_token", userDetailsMock)).thenReturn(true);
        when(jwtService.generateToken(userDetailsMock)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(userDetailsMock)).thenReturn("new_refresh_token");

        // When
        LoginResponse response = authService.refresh(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new_access_token");
        assertThat(response.getRefreshToken()).isEqualTo("new_refresh_token");
    }

    @Test
    void refresh_InvalidToken_ThrowsException() {
        // Given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid_refresh_token")
                .build();

        when(jwtService.extractUsername("invalid_refresh_token")).thenThrow(new RuntimeException("Invalid Signature"));

        // When & Then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Token de actualización inválido");
    }
}
