package uz.aim.zerikdim5.configs.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.aim.zerikdim5.services.auth.AuthService;
import uz.aim.zerikdim5.services.jwt.AccessTokenService;
import uz.aim.zerikdim5.services.jwt.TokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * @author "Otajonov Dilshodbek
 * @since 8/20/22 11:04 AM (Saturday)
 * Project_Blueprint/IntelliJ IDEA
 */

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private AuthService userService;
    @Qualifier("accessTokenService")
    @Autowired
    TokenService tokenService;
    private final static List<String> WHITE_LIST = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui.*",
            "/api-docs.*",
            "/api/auth/refreshToken",
            "/api/auth/activate"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (!isOpenUrl.apply(requestURI)) {
            try {
                String token = parseJwt(request);
                if (tokenService.isValid(token)) {
                    String email = tokenService.getSubject(token);
                    UserDetails userDetails = userService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String prefix = "Bearer ";
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(prefix)) {
            return headerAuth.substring(prefix.length());
        }
        return null;
    }

    private final static Function<String, Boolean> isOpenUrl = (url) -> WHITE_LIST.stream().anyMatch(url::matches);

}
