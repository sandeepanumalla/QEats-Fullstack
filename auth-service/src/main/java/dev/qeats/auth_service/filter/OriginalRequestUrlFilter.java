package dev.qeats.auth_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OriginalRequestUrlFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        logger.info(" original request url: " + request.getHeader("ORIGINAL_REQUEST_URL"));

        if(request.getHeader("ORIGINAL_REQUEST_URL") != null) {
            session.setAttribute("ORIGINAL_REQUEST_URL", request.getHeader("ORIGINAL_REQUEST_URL"));
        }
        // Save the original request URL if not already saved and if not a login or OAuth2 request
        if (session.getAttribute("ORIGINAL_REQUEST_URL") == null &&
                !request.getRequestURI().contains("/login") &&
                !request.getRequestURI().contains("/oauth2")) {
            session.setAttribute("ORIGINAL_REQUEST_URL", request.getRequestURI());
        }


        filterChain.doFilter(request, response);
    }
}
