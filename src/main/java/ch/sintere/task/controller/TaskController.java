package ch.sintere.task.controller;

import ch.sintere.task.dto.PriorityUpdateRequest;
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
public class TaskController {

    private final TaskService taskService;

    @Operation(
            description = "Create New Task",
            summary = "Create New Task",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
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
                    ),
                    @ApiResponse(
                            description = "Conflict",
                            responseCode = "409"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findTaskById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
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

    @Operation(
            description = "Update Task by Id",
            summary = "Update Task by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Conflict",
                            responseCode = "409"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody TaskDto taskDto, @PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.updateTask(taskDto, id));
    }

    @Operation(
            description = "Update only status by Id",
            summary = "Update only status by Id.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "400"
                    )
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable("id") Integer id, @Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateStatus(id, taskDto));
    }

    @Operation(
            description = "Update all Priority",
            summary = "Update all Priority",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "400"
                    )
            }
    )
    @PutMapping("/priority")
    public ResponseEntity<List<TaskDto>> updatePriorityForAll(@RequestBody PriorityUpdateRequest updateRequest) {
        return ResponseEntity.ok(taskService.updatePriorityForAll(updateRequest.oldPriority(), updateRequest.newPriority()));
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
    public ResponseEntity<Void> deleteTaskById(@PathVariable("id") Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
