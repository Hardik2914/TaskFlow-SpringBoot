package com.taskflow.taskflow.controller;

import com.taskflow.taskflow.*;
import com.taskflow.taskflow.dto.TaskDTO;
import com.taskflow.taskflow.model.Task;
import com.taskflow.taskflow.model.User;
import com.taskflow.taskflow.repo.TaskRepository;
import com.taskflow.taskflow.repo.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
   private TaskRepository taskRepo;

    @Autowired
    private UserRepository userRepo;


//    @PostMapping("/add")
//    public String addTask(@RequestBody Task task) {
//        Long userId=SessionUser.getCurrentUserId();
//        Optional<User> optionalUser = userRepo.findById(userId.intValue());
//        if(optionalUser.isPresent()){
//            task.setUser(optionalUser.get());
//            taskRepo.save(task);
//            return "Task added successfully";
//        }
//        else {
//            return "User not found";
//        }
//    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody @Valid TaskDTO taskDTO){
        Long Userid= SessionUser.getCurrentUserId();
        Optional<User> optionalUser=userRepo.findById(Userid.intValue());
        if(optionalUser.isPresent()){
            Task task=new Task();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setCompleted(taskDTO.isComplete());
            task.setUser(optionalUser.get());
            taskRepo.save(task);
            return ResponseEntity.ok("Task created sucessfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

//    @GetMapping("/mytasks")
//    public List<Task> getMyTasks() {
//        Long userId=SessionUser.getCurrentUserId();
//        if(userId==null){
//            return List.of();
//        }
//        Optional<User> optionalUser = userRepo.findById(userId.intValue());
//        if(optionalUser.isPresent()){
//            return taskRepo.findByUser(optionalUser.get());
//        }
//        else {
//            return List.of();
//        }
//    }

    @GetMapping
    public List<TaskDTO> getTask(){
        Long Userid=SessionUser.getCurrentUserId();
        Optional<User> optionalUser=userRepo.findById(Userid.intValue());
        if(optionalUser.isPresent()){
            List<Task> tasks=taskRepo.findByUser(optionalUser.get());
            return tasks.stream().map(task->{
                TaskDTO taskDTO=new TaskDTO();
                taskDTO.setTitle(task.getTitle());
                taskDTO.setDescription(task.getDescription());
                taskDTO.setComplete(task.isCompleted());
                return taskDTO;
            }).toList();
        }
        return null;
    }

    @DeleteMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable("taskId") Long taskId) {
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return "User not Loggedin";
        }
        Optional<Task> optionalTask=taskRepo.findById(taskId);

        if(optionalTask.isEmpty()){
            return "The task list is empty";
        }
        Task task=optionalTask.get();
        if(task.getUser().getId()!=userId){
            return "Invalid!! you cannot delete any other user task";
        }
        taskRepo.delete(task);
        return "Task deleted successfully";

    }


    @PutMapping("/complete/{id}")
    public String completeTask(@PathVariable("id") Long taskId) {
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return "User not Loggedin";
        }
        Optional<Task> optionalTask=taskRepo.findById(taskId);
        if(optionalTask.isPresent()){
            Task task=optionalTask.get();
            task.setCompleted(true);
            taskRepo.save(task);
            return "Task completed successfully";
        }
        return "Task not found";
    }


    @GetMapping("/pagable")
    public Page<Task> getCompletedTasks(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        @RequestParam(required = false) Boolean complete) {
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return Page.empty();
        }
        Optional<User> optionalUser = userRepo.findById(userId.intValue());
        if(optionalUser.isPresent()){
            Pageable pageable = (Pageable) PageRequest.of(page, size);
            return taskRepo.findByUserAndCompleted(optionalUser.get(),(boolean)complete, (org.springframework.data.domain.Pageable) pageable);
        }
        else  {
            return Page.empty();
        }
    }

//    @GetMapping("/returnIncompleate")
//    public Page<Task> getIncompleteTasks(@RequestParam(defaultValue = "0") int page,
//                                         @RequestParam(defaultValue = "5") int size,
//                                         @RequestParam(required = false) Boolean complete) {
//        Long userId=SessionUser.getCurrentUserId();
//        if(userId==null){
//            return Page.empty();
//        }
//        Optional<User> optionalUser = userRepo.findById(userId.intValue());
//        if(optionalUser.isPresent()){
//            Pageable pageable = (Pageable) PageRequest.of(page, size);
//            return taskRepo.findByUserAndCompleted(optionalUser.get(),false,pageable);
//        }
//        else {
//            return Page.empty();
//        }
//    }


    @PutMapping("/update/{id}")
    public String updateTask(@PathVariable("id") Long taskId, @RequestBody Task Updatedtask) {
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return "User not Loggedin";
        }
        Optional<Task> optionalTask=taskRepo.findById(taskId);
        if(optionalTask.isEmpty()){
            return "The task list is empty";
        }
        Task existTask=optionalTask.get();
        if(existTask.getUser().getId()!=userId.intValue()){
            return "unauthorized";
        }
        existTask.setTitle(Updatedtask.getTitle());
        existTask.setDescription(Updatedtask.getDescription());
        existTask.setCompleted(Updatedtask.isCompleted());
        taskRepo.save(existTask);
        return "Task updated successfully";
    }

    @GetMapping("/search")
    public Page<Task> searchTask(@RequestParam String Keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size){
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return Page.empty();
        }
        Optional<User> optionalUser = userRepo.findById(userId.intValue());
        if(optionalUser.isEmpty()){
            return Page.empty();
        }
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        return taskRepo.searchTaskByKeyword(optionalUser.get(), Keyword, pageable);

    }


    @GetMapping(value = "/export/csv", produces = "text/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        Long userId=SessionUser.getCurrentUserId();
        if(userId==null){
            return;
        }
        Optional<User> optionalUser = userRepo.findById(userId.intValue());
        if(optionalUser.isEmpty()){
            return;
        }
        List<Task> task=taskRepo.findByUser(optionalUser.get());
        response.setHeader("Content-Disposition", "attachment; filename=task.csv");
        Writer writer = response.getWriter();
        for(Task t:task){
            writer.write(t.getTitle()+"\t"+t.getDescription()+"\t"+String.valueOf(t.isCompleted())+"\n");
        }
        writer.flush();
        writer.close();

    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=task.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        document.add(new Paragraph("Task List"));
        Long userId=SessionUser.getCurrentUserId();
        List<Task> task=taskRepo.findByUser(userRepo.findById(userId.intValue()).get());
        for(Task t:task){
            document.add(new Paragraph("Title: "+t.getTitle()));
            document.add(new Paragraph("Description: "+t.getDescription()));
            document.add(new Paragraph("Status: "+t.isCompleted()));
            document.add(new Paragraph("----------------------"));
        }
        document.close();
    }


    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestParam Long userId) {
        List<Task> tasks=taskRepo.findByUserId(userId);
        long complated=tasks.stream().filter(task->task.isCompleted()).count();
        long pending=tasks.stream().filter(task->!task.isCompleted()).count();

        //response map is being created
        Map<String,Object> map=new HashMap<>();
        map.put("Total",tasks.size());
        map.put("complated",complated);
        map.put("pending",pending);

        return ResponseEntity.ok(map);
    }
}


