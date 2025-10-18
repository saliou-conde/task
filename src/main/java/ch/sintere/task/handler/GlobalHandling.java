package ch.sintere.task.handler;

import ch.sintere.task.exception.TaskAlreadyExistException;
import ch.sintere.task.exception.TaskDueDateInvalidException;
import ch.sintere.task.exception.TaskNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalHandling {

    private static final String TASK_NOT_FOUND  = "Task not found in the database";
    private static final String TASK_STATUS_CAN_NOT_BE_UPDATED  = "Task status can not be updated";
    private static final String TASK_ALREADY_EXISTS = "Task already exists in the database";

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTaskNotFoundException(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());
        return createResponseEntityWithProblemDetail( ex.getMessage(), NOT_FOUND, TASK_NOT_FOUND);
    }

   @ExceptionHandler(TaskDueDateInvalidException.class)
    public ResponseEntity<ProblemDetail> handleTaskDueDateException(TaskDueDateInvalidException ex) {
        log.warn("Status can not be updated: {}", ex.getMessage());
        return createResponseEntityWithProblemDetail( ex.getMessage(), BAD_REQUEST, TASK_STATUS_CAN_NOT_BE_UPDATED);
    }

    @ExceptionHandler(TaskAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> handleTaskAlreadyExistException(TaskAlreadyExistException ex) {
        log.warn("Task already exists: {}", ex.getMessage());
        return createResponseEntityWithProblemDetail( ex.getMessage(), CONFLICT, TASK_ALREADY_EXISTS);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        var name = ex.getName();
        var value = ex.getValue();
        var parameter = ex.getParameter().getParameterName();
        log.warn("Bad Request with name or value: {} {}", name, value);
        return createResponseEntityWithProblemDetail( parameter, BAD_REQUEST, format("Bad Request: %s = %s", name, value));
    }

    private ResponseEntity<ProblemDetail> createResponseEntityWithProblemDetail(String message, HttpStatus status, String description) {
        ProblemDetail problemDetail = createProblemDetail(status, message, description);
        return new ResponseEntity<>(problemDetail, status);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String message, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
    
}
