package com.ureca.fitlog.controller;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/create")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDTO requestDto) {
        return ResponseEntity.ok(todoService.createTodo(requestDto));
    }
}
