package br.com.develfoodspringweb.develfoodspringweb.security;

import br.com.develfoodspringweb.develfoodspringweb.models.User;
import br.com.develfoodspringweb.develfoodspringweb.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationTokenFilter extends OncePerRequestFilter {
    //AUTENTICAÇÃO VIA TOKEN, CLASSE IRÁ INTERCEPTAR A REQUISIÇÃO DO TOKEN PARA LIBERAR ACESSO A TAL USUÁRIO
    //REALIZAR ALGO RESTRITO NO SISTEMA.


    private TokenServ tokenServs;
    private UserRepository repository;

    public AuthenticationTokenFilter(TokenServ tokenServs, UserRepository repository) { //precisa de construtor pois classe filter nao faz injeção
        this.tokenServs = tokenServs;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = recoveryToken(request);
        boolean valid = tokenServs.isTokenValid(token);
        if(valid) {
            autenticarCliente(token);
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarCliente(String token) {
        Long idUser = tokenServs.getIdUser(token);
        User user = repository.findById(idUser).get();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recoveryToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7, token.length());
    }


}
