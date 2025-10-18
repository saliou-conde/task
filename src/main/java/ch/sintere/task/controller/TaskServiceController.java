package ch.sintere.task.controller;

import ch.sintere.task.dto.TaskDto;
import ch.sintere.task.entities.Priority;
import ch.sintere.task.entities.Status;
import ch.sintere.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskServiceController {

    private final TaskService taskService;

    @Operation(
            description = "Create New Task",
            summary = "Create New Task",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<TaskDto> addTask(@Valid @RequestBody TaskDto taskDto) {
        return new ResponseEntity<>(taskService.addTask(taskDto), HttpStatus.CREATED);
    }

    @Operation(
            description = "Find Task by Id",
            summary = "Find Task by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findTaskById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    @Operation(
            description = "Update Task by Id",
            summary = "Update Task by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody TaskDto taskDto, @PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.updateTask(taskDto, id));
    }

    @Operation(
            description = "Delete Task by Id",
            summary = "Remove Task by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTaskById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.deleteTask(id));
    }

    @Operation(
            description = "Get all Tasks by status",
            summary = "Get all the Tasks by status.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/status")
    public ResponseEntity<List<TaskDto>> findByStatus(@Valid @RequestParam("status") Status status) {
        return ResponseEntity.ok(taskService.findByStatus(status));
    }

    @Operation(
            description = "Get all Tasks by priority",
            summary = "Get all the Tasks by priority.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/priority")
    public ResponseEntity<List<TaskDto>> findByPriority(@Valid @RequestParam("priority") Priority priority) {
        return ResponseEntity.ok(taskService.findByPriority(priority));
    }
}
