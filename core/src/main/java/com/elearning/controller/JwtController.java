package com.elearning.controller;

import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.auth.AuthResponse;
import com.elearning.reprositories.IUserRepository;
import com.elearning.security.SecurityUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elearning.utils.Constants.*;

@Service
@Slf4j
public class JwtController {
    @Autowired
    private RefreshTokenController refreshTokenController;
    @Autowired
    private JwtController jwtController;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    public String extractUsername(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }
    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }
    public String generateToken(SecurityUserDetail userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, SecurityUserDetail userDetails){
        extraClaims.put("uId", userDetails.getId());
        extraClaims.put("fullName", userDetails.getFullName());
        extraClaims.put("roles", userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return buildToken(extraClaims, userDetails, ACCESS_TOKEN_EXPIRE_TIME_MILLIS);
    }
    public String generateRefreshToken(SecurityUserDetail userDetails){
        String token = buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRE_TIME_MILLIS);
        String userId = userRepository.findByEmail(userDetails.getUsername()).getId();
        token = refreshTokenController.create(token, userId);
        System.out.println("---------"+token);
        return token;
    }
    public String buildToken(Map<String, Object> extraClaims, SecurityUserDetail userDetails, long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isValidToken(String token, SecurityUserDetail userDetails){
        final String username = extractUsername(token);
        return (boolean) (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public AuthResponse refreshToken(String token) throws ServiceException {
        var storedToken = refreshTokenController.findById(token);
        if(storedToken.isEmpty()){
            throw new ServiceException("Không có quyền truy cập 1");
        }
        if(storedToken.get().getIsDeleted()){
            refreshTokenController.deleteRefreshTokenBranch(storedToken.get().getId());
            throw new ServiceException("Không có quyền truy cập 2");
        }
        if(storedToken.get().getExpiredAt().before(new Date())){
            refreshTokenController.deleteRefreshTokenBranch(storedToken.get().getId());
            throw new ServiceException("Không có quyền truy cập 3");
        }
        storedToken.get().setIsDeleted(true);
        refreshTokenController.save(storedToken.get());

        var user = userRepository.findById(storedToken.get().getUserId());

        SecurityUserDetail userDetail = (SecurityUserDetail) userDetailsService.loadUserByUsername(user.get().getEmail());
        var jwtToken = jwtController.generateToken(userDetail);
        var refreshToken = jwtController.generateRefreshToken(userDetail);

        var savedRefreshToken = refreshTokenController.findById(refreshToken).get();
        if(storedToken.get().getCreatedFrom() == null){
            savedRefreshToken.setCreatedFrom(storedToken.get().getId());
        }else{
            savedRefreshToken.setCreatedFrom(storedToken.get().getCreatedFrom());
        }
        refreshTokenController.save(savedRefreshToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(Cookie cookie) throws ServiceException{
        if(cookie == null){
            throw new ServiceException("Không có quyền truy cập 4");
        }
        return refreshToken(cookie.getValue());
    }
    private Claims extractAllClaims(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build().parseClaimsJws(jwt)
                .getBody();
    }
    private Key getSignInKey(){
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

}
