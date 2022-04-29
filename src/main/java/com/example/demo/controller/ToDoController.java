package com.example.demo.controller;

import com.example.demo.entity.Task;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class ToDoController {

    @Autowired
    private TaskRepository taskRepository;


    @PostMapping("/")
    public Task addTask(@RequestBody Task task) {
        taskRepository.save(task);
        return task;
    }

    @GetMapping("/")
    public Page<Task> getAllTasks(@RequestParam(name = "page", defaultValue = "0") int page) {
        return taskRepository.findAll(PageRequest.of(page, 5));
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable(name = "id") int id) {
        Task task = null;
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isPresent()) {
            task = optional.get();
            return task;
        }
        throw new NotFoundException();
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable(name = "id") int id, @RequestBody Task newTask) {
        if (taskRepository.existsById(id)) {
            newTask.setId(id);
            taskRepository.save(newTask);
            return newTask;
        }
        throw new NotFoundException();
    }

    @PostMapping("/{id}/{action}")
    public Task onAction(@PathVariable(name = "id") int id, @PathVariable(name = "action") String action) {
        Task task = null;
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isPresent()) {
            task = optional.get();
        } else {
            throw new NotFoundException();
        }
        switch (action) {
            case "check":
                task.setCompleted(true);
                break;

            case "uncheck":
                task.setCompleted(false);
                break;

            default:
                throw new BadRequestException();
        }
        return updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable(name = "id") int id) {
        Task task = null;
        Optional<Task> optional = taskRepository.findById(id);
        if (optional.isPresent()) {
            task = optional.get();
        } else {
            throw new NotFoundException();
        }
        taskRepository.delete(task);
    }

}