package com.boraver.teamgenerator.security;

import com.boraver.teamgenerator.common.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();

    return "OPTIONS".equalsIgnoreCase(request.getMethod())
            || uri.startsWith("/auth/")
            || uri.startsWith("/v3/api-docs/")
            || uri.startsWith("/swagger-ui/")
            || "/swagger-ui.html".equals(uri)
            || uri.matches("^/championships/[^/]+/stream$");
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);

    try {
      var claims = jwtService.parse(token).getBody();

      UUID userId = UUID.fromString(claims.getSubject());
      String role = claims.get("role", String.class);
      String tenantId = claims.get("tenant_id", String.class);

      TenantContext.setTenantId(tenantId);

      var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

      Authentication auth = new UsernamePasswordAuthenticationToken(
              userId,
              null,
              authorities
      );

      SecurityContextHolder.getContext().setAuthentication(auth);

      // Token válido - continua a requisição
      filterChain.doFilter(request, response);


    } catch (Exception e) {
      // Token inválido ou expirado - retorna 401
      SecurityContextHolder.clearContext();

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\": \"Token inválido ou expirado\"}");
      // Não chama filterChain.doFilter() - interrompe a requisição
    } finally {
      TenantContext.clear();
    }
  }
}