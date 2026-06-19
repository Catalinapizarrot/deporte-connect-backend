package com.deporteconnect.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * Soluciona el EOFException del cliente Android.
 *
 * Problema: Tomcat responde el feed con "Transfer-Encoding: chunked"
 * (unknown-length body). El stream chunked llega mal cerrado al emulador
 * y OkHttp lanza EOFException.
 *
 * Solucion: este filtro BUFFEA la respuesta completa en memoria y luego
 * la escribe de una sola vez con un Content-Length fijo. Al tener
 * Content-Length, Tomcat NO usa chunked -> desaparece la causa del error.
 *
 * Es transparente: no cambia el contenido, solo COMO se transmite.
 */
@Configuration
public class ContentLengthConfig {

    @Bean
    public FilterRegistrationBean<Filter> fixedLengthFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FixedLengthFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    static class FixedLengthFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            ContentCachingResponseWrapper wrapper =
                    new ContentCachingResponseWrapper(httpResponse);

            chain.doFilter(request, wrapper);

            byte[] body = wrapper.getContentAsByteArray();
            // Fijar el largo exacto -> Tomcat usa Content-Length, no chunked
            wrapper.setContentLength(body.length);
            wrapper.copyBodyToResponse();
        }
    }
}
