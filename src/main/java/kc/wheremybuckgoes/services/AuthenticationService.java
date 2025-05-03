package kc.wheremybuckgoes.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kc.wheremybuckgoes.constants.ApplicationConstant;
import kc.wheremybuckgoes.modal.AuthenticationModifyRequest;
import kc.wheremybuckgoes.modal.Token;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.repositories.TokenRepository;
import kc.wheremybuckgoes.repositories.UserRepository;
import kc.wheremybuckgoes.request.AuthenticationRequest;
import kc.wheremybuckgoes.request.RegisterRequest;
import kc.wheremybuckgoes.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /**
     * Dependencies for the AuthenticationService class.
     * <p>
     * This service manages user authentication, registration, and token handling,
     * requiring several dependencies to perform its functions:
     *
     * @field userRepo Repository interface for user entity operations.
     *        Provides methods to create, retrieve, and update user records in the database.
     *        Used for user registration and lookup during authentication processes.
     *
     * @field tokenRepository Repository interface for token entity operations.
     *        Handles the persistence of authentication tokens, including creation,
     *        retrieval, and status updates (revocation/expiration).
     *
     * @field passwordEncoder Spring Security encoder for password hashing.
     *        Provides secure one-way encryption of passwords during registration
     *        and verification during authentication.
     *
     * @field jwtService Service for JWT (JSON Web Token) operations.
     *        Handles token generation, validation, and extraction of claims
     *        for both access and refresh tokens.
     *
     * @field authenticationManager Spring Security component for authentication.
     *        Validates user credentials against stored user details and
     *        handles authentication exceptions.
     *
     * @field isRegistrationEnabled Configuration flag from application properties.
     *        Controls whether new user registration is permitted in the current
     *        environment. Injected from the 'application.register' property.
     */

    private final UserRepository userRepo;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    @Value("${application.register}")
    private boolean isRegistrationEnabled;


    /**
     * Registers a new user in the system.
     * <p>
     * This method handles the user registration process by:
     * 1. Checking if registration is currently enabled in the system
     * 2. Verifying that the email is not already registered
     * 3. Creating a new user with encoded password
     * 4. Generating authentication tokens
     * 5. Saving the user and token information
     * <p>
     * The method implements security best practices including password encoding
     * and JWT token generation for secure authentication.
     *
     * @param request The registration request containing user details:
     *                - firstname: User's first name
     *                - lastname: User's last name
     *                - email: User's email address (used as unique identifier)
     *                - password: User's password (will be encoded before storage)
     *
     * @return AuthenticationResponse object containing:
     *         - accessToken: JWT token for API access
     *         - refreshToken: Token to obtain new access tokens
     *         - user: DTO with user information
     *         - code: HTTP status code indicating result
     *         - message: Description of any error (if applicable)
     *
     * @apiNote Returns error responses with appropriate HTTP status codes:
     *         - 405 METHOD_NOT_ALLOWED: If registration is disabled
     *         - 409 CONFLICT: If the email is already registered
     *         - 200 OK: If registration is successful
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if(!isRegistrationEnabled) {return AuthenticationResponse.builder().accessToken(null).accessToken(null).message("Registration of new members is not allowed.").code(HttpStatus.METHOD_NOT_ALLOWED).build();}
        User found = userRepo.findByEmail(request.getEmail()).orElse(null);
        if(found != null){
            return AuthenticationResponse.builder().accessToken(null).accessToken(null).message("User already exists").code(HttpStatus.CONFLICT).build();
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ApplicationConstant.Role.USER)
                .build();
        var savedUser = userRepo.save(user);
        var jwtToken = jwtService.generateToken(new HashMap<>(), user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).user(user.convertToDTO()).code(HttpStatus.OK).build();
    }

    /**
     * Authenticates a user and generates access tokens.
     * <p>
     * This method handles the user authentication process by:
     * 1. Validating user credentials against the authentication manager
     * 2. Retrieving the user from the database if credentials are valid
     * 3. Generating a new JWT access token and refresh token
     * 4. Revoking any existing tokens for the user
     * 5. Saving the newly generated token
     * <p>
     * The method implements secure authentication practices including:
     * - Delegating credential validation to Spring Security's authentication manager
     * - Token-based authentication with JWT
     * - Token revocation for security
     *
     * @param request The authentication request containing:
     *                - email: User's email address (username)
     *                - password: User's password for verification
     *
     * @return AuthenticationResponse object containing:
     *         - accessToken: JWT token for API access
     *         - refreshToken: Token to obtain new access tokens
     *         - user: DTO with user information
     *         - code: HTTP status code (200 OK for successful authentication)
     *
     * @apiNote This method revokes all existing tokens for a user upon successful
     *          authentication as a security measure to prevent token misuse.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(new HashMap<>(), user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).user(user.convertToDTO()).code(HttpStatus.OK).build();
    }


    /**
     * Saves a new authentication token for a user.
     * <p>
     * This method creates and persists a token entity in the database that associates
     * a JWT token with a specific user. The token is stored with metadata including
     * its type, expiration status, and revocation status.
     *
     * @param user The user entity to associate with the token. This establishes
     *             ownership of the token and enables user-specific token management.
     *
     * @param jwtToken The JWT token string value generated during authentication.
     *                 This token will be used for subsequent API authorization.
     *
     * @implNote The token is created with the following default properties:
     *           - Token type: BEARER (as defined in ApplicationConstant.TokenType)
     *           - expired: false (token is initially valid)
     *           - revoked: false (token has not been manually invalidated)
     *
     * @see Token The entity model representing the token in the database
     * @see TokenRepository The repository interface for token persistence
     * @see ApplicationConstant.TokenType Enumeration of supported token types
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .tokenType(ApplicationConstant.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revokes all valid tokens for a specific user.
     * <p>
     * This method is responsible for invalidating all active authentication tokens
     * associated with a user. It's typically called during security-sensitive operations
     * such as password changes, account lockouts, or when a user logs out from all devices.
     * <p>
     * The method performs the following steps:
     * 1. Retrieves all valid (non-expired, non-revoked) tokens for the specified user
     * 2. If no valid tokens exist, returns immediately
     * 3. Marks each token as both expired and revoked
     * 4. Persists the updated token statuses to the database in a batch operation
     *
     * @param user The user entity whose tokens should be revoked
     *
     * @implNote This method uses batch saving for efficiency when multiple tokens
     *           need to be revoked. Both the expired and revoked flags are set to
     *           ensure the tokens cannot be reused under any circumstances.
     *
     * @see Token The entity model representing tokens in the database
     * @see TokenRepository#findAllValidTokenByUser Method to retrieve valid tokens
     * @see TokenRepository#saveAll Method for batch saving token updates
     */
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    /**
     * Refreshes the user's access token using a valid refresh token.
     * <p>
     * This method handles the token refresh flow, allowing clients to obtain a new
     * access token without requiring the user to re-authenticate with credentials.
     * The process follows these steps:
     * <p>
     * 1. Extracts the refresh token from the Authorization header
     * 2. Validates the refresh token format and presence
     * 3. Extracts the user email from the refresh token
     * 4. Retrieves the user from the database
     * 5. Verifies the refresh token's validity for the user
     * 6. Generates a new access token
     * 7. Revokes all existing tokens for security
     * 8. Saves the new access token
     * 9. Returns the new access token along with the existing refresh token
     *
     * @param request The HTTP request containing the Authorization header with the refresh token
     * @param response The HTTP response (not directly used in the current implementation)
     *
     * @return ResponseEntity containing:
     *         - On success (200 OK): AuthenticationResponse with new access token and existing refresh token
     *         - On failure (400 Bad Request): When the token is missing, invalid, or expired
     *
     * @throws IOException If an I/O error occurs during processing
     *
     * @apiNote The method maintains security by:
     *          - Requiring the refresh token to be sent in the Authorization header with Bearer prefix
     *          - Validating the token's signature and expiration
     *          - Revoking all existing tokens when issuing a new one
     */
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.getUserEmailFromJWT(refreshToken);
        if (userEmail != null) {
            var user = userRepo.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(new HashMap<>(),user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                return ResponseEntity.ok().body(authResponse);
            }
        }
        return ResponseEntity.badRequest().build();
    }


    public AuthenticationResponse changePassword(AuthenticationModifyRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepo.findByEmail(request.getEmail()).orElse(null);
        if(user != null){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepo.save(user);
            request.setPassword(request.getNewPassword());
            AuthenticationResponse response = this.authenticate(request);
            response.setMessage("Password changed successfully.");
            return response;
        }
        return AuthenticationResponse.builder().accessToken(null).accessToken(null).message("Username & Password combo is invalid.").code(HttpStatus.BAD_REQUEST).user(null).build();
    }



}