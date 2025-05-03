/**
 * Controller for handling authentication-related HTTP requests.
 * <p>
 * This controller provides endpoints for user registration, authentication, and token refresh
 * operations. It serves as the entry point for all authentication-related API calls in the
 * WhereMyBuckGoes application.
 *
 * @author Kartikey Choudhary (kartikey31choudhary@gmail.com)
 * @see AuthenticationService The service class that implements the authentication logic
 */

package kc.wheremybuckgoes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kc.wheremybuckgoes.modal.AuthenticationModifyRequest;
import kc.wheremybuckgoes.modal.User;
import kc.wheremybuckgoes.request.AuthenticationRequest;
import kc.wheremybuckgoes.request.RegisterRequest;
import kc.wheremybuckgoes.response.AuthenticationResponse;
import kc.wheremybuckgoes.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    /**
     * Service that handles authentication business logic.
     * Injected via constructor using Lombok's @RequiredArgsConstructor.
     */
    private final AuthenticationService authenticationService;


    /**
     * Registers a new user in the system.
     * <p>
     * This endpoint accepts user registration details and attempts to create a new user account.
     * If registration is successful, it returns authentication tokens for immediate login.
     * If registration fails (e.g., email already exists or registration is disabled),
     * it returns an appropriate error response.
     *
     * @param request The registration request containing user details (first name, last name, email, password)
     * @return ResponseEntity containing:
     *         - 200 OK with authentication tokens if registration is successful
     *         - 400 Bad Request with error details if registration fails
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){  AuthenticationResponse ar = authenticationService.register(request);
        if(ar.getAccessToken() == null){
            return ResponseEntity.badRequest().body(ar);
        }
        return ResponseEntity.ok(ar);
    }

    /**
     * Authenticates a user with email and password.
     * <p>
     * This endpoint validates user credentials and, if successful, returns authentication tokens
     * that can be used for subsequent API calls requiring authentication.
     *
     * @param request The authentication request containing user credentials (email, password)
     * @return ResponseEntity containing:
     *         - 200 OK with authentication tokens if authentication is successful
     *         - 401 Unauthorized (handled by Spring Security) if authentication fails
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     * <p>
     * This endpoint allows clients to obtain a new access token without requiring the user
     * to re-authenticate with credentials. The refresh token must be included in the
     * Authorization header of the request.
     *
     * @param request The HTTP request containing the refresh token in the Authorization header
     * @param response The HTTP response (not directly used in the current implementation)
     * @return ResponseEntity containing:
     *         - 200 OK with new access token and existing refresh token if successful
     *         - 400 Bad Request if the refresh token is missing, invalid, or expired
     * @throws IOException If an I/O error occurs during processing
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return authenticationService.refreshToken(request, response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @RequestBody AuthenticationModifyRequest request
    ){
        if(request.getNewPassword() == null || request.getNewPassword().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        authenticationService.revokeAllUserTokens(user);
        return ResponseEntity.ok().build();
    }
}