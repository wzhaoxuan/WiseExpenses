package com.wise.expenses_tracker.security;

// Standard Java imports for security, dates, and functional programming
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.wise.expenses_tracker.security.interfaces.JwtService;

import io.jsonwebtoken.Claims;          
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;      
import io.jsonwebtoken.security.Keys;    

/**
 * JWT Service Implementation
 * 
 * This service handles all JWT (JSON Web Token) operations for the application.
 * JWTs are used for stateless authentication - once a user logs in, they receive
 * a token that proves their identity for subsequent requests.
 * 
 * Key Responsibilities:
 * 1. Generate JWT tokens for authenticated users
 * 2. Extract user information from tokens
 * 3. Validate token authenticity and expiration
 * 4. Parse token claims and metadata
 * 
 * Security Features:
 * - Uses HMAC-SHA256 algorithm for token signing
 * - 10-hour token expiration for security
 * - Cryptographic signature validation
 * - Username verification against user details
 * 
 */
@Service // Marks this as a Spring service component for dependency injection
public class JwtServiceImpl implements JwtService {

    /**
     * Secret key for signing and validating JWT tokens.
     * 
     * IMPORTANT SECURITY NOTE:
     * - This should be stored in environment variables in production
     * - Must be at least 256 bits (32 bytes) for HS256 algorithm
     * - This key is used to create digital signatures that prove token authenticity
     * - Anyone with this key can create valid tokens, so keep it secret!
     * 
     * Current key: 64-character hex string = 256 bits (secure for HS256)
     */
    private static final String SECRET_KEY = "63db3edac6e43e9f1bb6420b846c10dddeeaac957cc5bbf7a66a3abe116b6e61";

    /**
     * Extracts the username (subject) from a JWT token.
     * 
     * The 'subject' (sub) claim in JWT typically contains the user identifier.
     * In our case, this is the username that uniquely identifies the user.
     * 
     * Process:
     * 1. Parse the token to extract all claims
     * 2. Get the 'subject' claim which contains the username
     * 3. Return the username string
     *
     * @param token the JWT token from which to extract the username
     * @return the username extracted from the token's subject claim
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    @Override
    public String extractUserName(String token) {
        // Claims::getSubject is a method reference that extracts the 'sub' claim
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any specific claim from a JWT token.
     * 
     * This is a utility method that provides a flexible way to extract
     * different types of claims (subject, expiration, issued date, etc.)
     * using functional programming with method references.
     * 
     * @param token the JWT token to parse
     * @param claimsResolver a function that takes Claims and returns the desired value
     * @param <T> the type of the claim value to be returned
     * @return the extracted claim value of type T
     * @throws io.jsonwebtoken.JwtException if token parsing fails
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // First, extract all claims from the token
        Claims claims = extractAllClaims(token);
        // Then apply the resolver function to get the specific claim we want
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for a user with default claims only.
     * 
     * This is a convenience method that creates a token with just the
     * standard claims (subject, issued at, expiration) without any
     * additional custom claims.
     * 
     * Standard claims included:
     * - sub (subject): user's username
     * - iat (issued at): current timestamp
     * - exp (expiration): current time + 10 hours
     *
     * @param userDetails the user for whom to generate the token
     * @return a signed JWT token string
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        // Call the overloaded method with an empty map for extra claims
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for a user with custom additional claims.
     * 
     * This method creates a complete JWT token with both standard claims
     * and any additional custom claims you want to include.
     * 
     * Token Structure:
     * - Header: Algorithm and token type (automatically added)
     * - Payload: Contains all the claims (data)
     * - Signature: Cryptographic signature to verify authenticity
     * 
     * Claims added:
     * - Custom claims from extraClaims parameter
     * - sub: username from userDetails
     * - iat: current timestamp (when token was issued)
     * - exp: expiration time (10 hours from now)
     *
     * @param extraClaims additional custom claims to include in the token
     * @param userDetails the user for whom the token is generated
     * @return a complete signed JWT token string ready for use
     */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                // Add any extra custom claims first
                .setClaims(extraClaims)
                // Set the subject (username) - this identifies who the token is for
                .setSubject(userDetails.getUsername())
                // Set issued at time to current time
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Set expiration time to 10 hours from now (10 * 60 * 60 * 1000 ms)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // Sign the token with our secret key using HMAC-SHA256 algorithm
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                // Convert the token to its final string representation
                .compact();
    }

    /**
     * Validates whether a JWT token is authentic and belongs to the specified user.
     * 
     * This method performs comprehensive token validation:
     * 1. Verifies the token signature using our secret key
     * 2. Checks that the token hasn't expired
     * 3. Confirms the token belongs to the specified user
     * 
     * Security checks performed:
     * - Signature validation (ensures token wasn't tampered with)
     * - Username matching (ensures token belongs to this user)
     * - Expiration checking (ensures token is still valid)
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to validate the token against
     * @return true if token is valid and belongs to the user, false otherwise
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails){
        // Extract username from the token
        final String username = extractUserName(token);
        // Token is valid if: username matches AND token hasn't expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extracts all claims (payload data) from a JWT token.
     * 
     * This method performs the core JWT parsing operation:
     * 1. Creates a JWT parser with our signing key
     * 2. Parses the token and validates its signature
     * 3. Extracts the claims (payload) from the token
     * 
     * The claims object contains all the data stored in the token:
     * - Standard claims (sub, iat, exp, etc.)
     * - Custom claims that were added during token creation
     * 
     * @param token the JWT token to parse
     * @return Claims object containing all token data
     * @throws io.jsonwebtoken.JwtException if token is invalid, expired, or signature doesn't match
     */
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                // Set the signing key used to verify the token signature
                .setSigningKey(getSignInKey())
                // Build the parser with our configuration
                .build()
                // Parse the token and validate its signature
                .parseClaimsJws(token)
                // Extract the body (claims/payload) from the parsed token
                .getBody();
    }

    /**
     * Creates the cryptographic key used for signing and validating JWT tokens.
     * 
     * This method converts our secret key string into a proper cryptographic
     * key object that can be used with the HMAC-SHA256 algorithm.
     * 
     * Process:
     * 1. Decode the BASE64-encoded secret key string into bytes
     * 2. Create an HMAC key suitable for SHA-256 algorithm
     * 3. Return the key for use in signing/validation operations
     * 
     * Security Note:
     * - The key must be at least 256 bits for HS256 algorithm
     * - This key is used to create unforgeable digital signatures
     *
     * @return a cryptographic Key object for HMAC-SHA256 operations
     */
    private Key getSignInKey() {
        // Decode the hex string secret key into raw bytes
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        // Create an HMAC key suitable for SHA-256 algorithm
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if a JWT token has expired.
     * 
     * This method compares the token's expiration time with the current time
     * to determine if the token is still valid or has expired.
     * 
     * Expiration Logic:
     * - Extracts the 'exp' claim from the token
     * - Compares it with current system time
     * - Returns true if current time is after expiration time
     *
     * @param token the JWT token to check for expiration
     * @return true if the token has expired, false if still valid
     */
    private boolean isTokenExpired(String token) {
        // Get expiration date and check if it's before current time
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     * 
     * This method retrieves the 'exp' (expiration) claim from the token,
     * which indicates when the token will become invalid.
     * 
     * The expiration claim is a standard JWT claim that contains a
     * timestamp indicating when the token expires.
     *
     * @param token the JWT token from which to extract expiration
     * @return Date object representing when the token expires
     */
    private Date extractExpiration(String token) {
        // Claims::getExpiration extracts the 'exp' claim as a Date object
        return extractClaim(token, Claims::getExpiration);
    }
}
