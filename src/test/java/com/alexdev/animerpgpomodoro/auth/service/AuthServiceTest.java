package com.alexdev.animerpgpomodoro.auth.service;

import com.alexdev.animerpgpomodoro.auth.dto.AuthResponse;
import com.alexdev.animerpgpomodoro.auth.dto.LoginRequest;
import com.alexdev.animerpgpomodoro.auth.dto.RegisterRequest;
import com.alexdev.animerpgpomodoro.auth.entity.User;
import com.alexdev.animerpgpomodoro.auth.entity.UserRole;
import com.alexdev.animerpgpomodoro.auth.repository.UserRepository;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import com.alexdev.animerpgpomodoro.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";
    private static final String USERNAME = "testUser";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";
    private static final String JWT_TOKEN = "jwt-token-value";

    private User createUser() {
        return User.builder()
                .id("uuid-123")
                .username(USERNAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .role(UserRole.USER)
                .build();
    }

    @Nested
    class Login {

        @Test
        void shouldReturnAuthResponseWhenCredentialsAreValid() {
            var request = new LoginRequest(EMAIL, PASSWORD);
            var user = createUser();

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(EMAIL)).thenReturn(JWT_TOKEN);

            AuthResponse response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(JWT_TOKEN);
            verify(userRepository).findByEmail(EMAIL);
            verify(passwordEncoder).matches(PASSWORD, user.getPassword());
            verify(jwtService).generateToken(EMAIL);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            var request = new LoginRequest(EMAIL, PASSWORD);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Invalid credentials");

            verify(userRepository).findByEmail(EMAIL);
            verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenPasswordDoesNotMatch() {
            var request = new LoginRequest(EMAIL, "wrong-password");
            var user = createUser();

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Invalid credentials");

            verify(userRepository).findByEmail(EMAIL);
            verify(passwordEncoder).matches("wrong-password", user.getPassword());
            verifyNoInteractions(jwtService);
        }
    }

    @Nested
    class Register {

        @Test
        void shouldSaveUserWhenEmailAndUsernameAreUnique() {
            var request = new RegisterRequest(USERNAME, EMAIL, PASSWORD);

            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);

            authService.register(request);

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository).existsByUsername(USERNAME);
            verify(passwordEncoder).encode(PASSWORD);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getUsername()).isEqualTo(USERNAME);
            assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
            assertThat(savedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
            assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
            assertThat(savedUser.getCreatedAt()).isNotNull();
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenEmailAlreadyExists() {
            var request = new RegisterRequest(USERNAME, EMAIL, PASSWORD);

            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Email already in use");

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository, never()).existsByUsername(any());
            verify(userRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenUsernameAlreadyExists() {
            var request = new RegisterRequest(USERNAME, EMAIL, PASSWORD);

            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Username already in use");

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository).existsByUsername(USERNAME);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }
}
