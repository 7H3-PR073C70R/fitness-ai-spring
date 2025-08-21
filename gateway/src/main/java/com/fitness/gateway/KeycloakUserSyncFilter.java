package com.fitness.gateway;

import com.fitness.gateway.user.UserRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("KeycloakUserSyncFilter: Authorization token: {}", token);
        UserRequest userRequest = getuserRequestFromToken(token);

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");

        if(userId == null && userRequest != null) {
            userId = userRequest.getKeycloakId();
        }

       if( userId != null && token != null) {
           String finalUserId = userId;
           return  userService.validateUser(finalUserId)
                   .flatMap(exists -> {
                       if (exists) {
                           log.info("User already exists, Skipping sync.");
                           return Mono.empty();
                       } else {

                           if(userRequest == null) {
                               log.error("Failed to parse user request from token.");
                               return Mono.error(new RuntimeException("Invalid token format"));
                           }
                           return userService.registerUser(userRequest)
                                   .then(Mono.empty());
                       }
                   })
                   .then(Mono.defer(() -> {
                       ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                               .header("X-User-ID", finalUserId)
                               .header("Authorization", token)
                               .build();
                          return chain.filter(exchange.mutate().request(mutatedRequest).build());
                   }));
       }


        return chain.filter(exchange)
                .doOnError(e -> log.error("Error in KeycloakUserSyncFilter: {}", e.getMessage()));
    }

    private UserRequest getuserRequestFromToken(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "");
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            UserRequest request = new UserRequest();
            request.setEmail(claimsSet.getStringClaim("email"));
            request.setFirstName(claimsSet.getStringClaim("given_name"));
            request.setLastName(claimsSet.getStringClaim("family_name"));
            request.setKeycloakId(claimsSet.getStringClaim("sub"));
            request.setPassword("defaultPassword"); // Set a default password or handle it as needed
            return request;
         } catch (Exception e) {
            log.error("Error parsing token: {}", e.getMessage());
            return null;
        }
    }
}
