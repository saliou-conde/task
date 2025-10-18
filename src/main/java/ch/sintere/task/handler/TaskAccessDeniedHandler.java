package ch.sintere.task.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class TaskAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
          throws IOException {

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json");

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You do not have enough permission.");
    problemDetail.setTitle(HttpStatus.FORBIDDEN.getReasonPhrase());
    problemDetail.setProperty("timestamp", Instant.now());

    response.getWriter().write(problemDetail.toString());
  }
}
