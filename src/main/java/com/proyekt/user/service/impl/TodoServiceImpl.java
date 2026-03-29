package com.proyekt.user.service.impl;


/*
1-ci userde gedib useri tapirsan (username) userin Repositorysinde
2-ci hemin userin id sini yoluyursan todonun repositorysine DB den ceksin
*
*
*
*
* */

import com.proyekt.user.dto.DtoImageDelete;
import com.proyekt.user.dto.DtoTodo;
import com.proyekt.user.dto.TodoRequest;
import com.proyekt.user.dto.TodoUpdateRequest;
import com.proyekt.user.exception.BaseException;
import com.proyekt.user.exception.ErrorMessage;
import com.proyekt.user.exception.MessageType;
import com.proyekt.user.model.Todo;
import com.proyekt.user.model.User;
import com.proyekt.user.repository.RepositoryTodo;
import com.proyekt.user.repository.RepositoryUser;
import com.proyekt.user.service.ITodoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements ITodoService {


    @Autowired
    private RepositoryTodo repositoryTodo;

    @Autowired
    private RepositoryUser repositoryUser;


    @Override
    public List<DtoTodo> allListTodo(String userName) {

        List<DtoTodo> dtoTodos = new ArrayList<>();

        Optional<User> userDB = repositoryUser.findByUserName(userName);

        if (userDB.isEmpty()) {

            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, null));
        }

        User user = userDB.get();

        List<Todo> todoList = repositoryTodo.findByUserId(user.getId());

        if (todoList != null) {
            for (Todo todo : todoList) {

                DtoTodo dtoTodo = new DtoTodo();

                dtoTodo.setId(todo.getId());
                dtoTodo.setTitle(todo.getTitle());
                dtoTodo.setUserId(todo.getUser().getId());
                dtoTodo.setCompleted(todo.isCompleted());
                dtoTodo.setImageTodoUrl(todo.getTodoImageUrl());

                dtoTodos.add(dtoTodo);

            }
        }

        return dtoTodos;
    }


    @Override
    public DtoTodo saveTodo(TodoRequest todoRequest, String userName) {

        DtoTodo dtoTodo = new DtoTodo();

        Optional<User> user = repositoryUser.findByUserName(userName);

        Todo todo = new Todo();

        todo.setUser(user.get());
        todo.setCompleted(todoRequest.isCompleted());
        todo.setTitle(todoRequest.getTitle());
        todo.setTodoImageUrl(todoRequest.getTodoImageUrl());

        Todo tododb = repositoryTodo.save(todo);


        dtoTodo.setId(tododb.getId());
        dtoTodo.setCompleted(tododb.isCompleted());
        dtoTodo.setTitle(tododb.getTitle());
        dtoTodo.setUserId(tododb.getUser().getId());
        dtoTodo.setImageTodoUrl(tododb.getTodoImageUrl());

        return dtoTodo;
    }

    @Override
    public DtoTodo updateTodo(Long id, TodoRequest todoRequest, String userName) {

        DtoTodo dtoTodo = new DtoTodo();

        Todo todo = new Todo();

        Optional<User> userID = repositoryUser.findByUserName(userName);

        if (userID.isEmpty()) {
            System.out.println("user tapilamdi");
        }

        Optional<Todo> todoOptional = repositoryTodo.findByIdAndUserId(id, userID.get().getId());


        Todo todoDB = todoOptional.get();

        todoDB.setTitle(todoRequest.getTitle());
        todoDB.setCompleted(todoRequest.isCompleted());

        Todo todo1 = repositoryTodo.save(todoDB);

        dtoTodo.setUserId(userID.get().getId());
        dtoTodo.setTitle(todo1.getTitle());
        dtoTodo.setCompleted(todo1.isCompleted());


        return dtoTodo;
    }

    @Transactional
    @Override
    public void deleteTodo(Long id, String userName) {

        Optional<User> userID = repositoryUser.findByUserName(userName);

        if (userID.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, id.toString()));
        }

        repositoryTodo.deleteByIdAndUserId(id, userID.get().getId());

    }

    @Override
    public void updateTodoImage(Long userId, String imageUrl, Long id) {

        Optional<Todo> todoOptional = repositoryTodo.findByIdAndUserId(id, userId);

        Todo todoImage = todoOptional.get();

        todoImage.setTodoImageUrl(imageUrl);

        repositoryTodo.save(todoImage);

    }

    @Override // Get
    public DtoTodo findByIdTodo(Long id, String userName) {

        DtoTodo dtoTodo = new DtoTodo();

        Optional<User> use = repositoryUser.findByUserName(userName);

        User userDB = use.get();

        Optional<Todo> todoDB = repositoryTodo.findByIdAndUserId(id, userDB.getId());

        dtoTodo.setId(todoDB.get().getId());
        dtoTodo.setTitle(todoDB.get().getTitle());
        dtoTodo.setImageTodoUrl(todoDB.get().getTodoImageUrl());
        dtoTodo.setUserId(todoDB.get().getUser().getId());
        dtoTodo.setCompleted(todoDB.get().isCompleted());

        return dtoTodo;
    }



    @Override // patch
    public TodoUpdateRequest updatePost(Long id, TodoUpdateRequest todoUpdateRequest, String userName) {

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();

        Optional<User> user = repositoryUser.findByUserName(userName);

        Optional<Todo> todoDB = repositoryTodo.findByIdAndUserId(id, user.get().getId());

        Todo todoDb2 = todoDB.get();

        if (todoUpdateRequest.getCompleted() != null) {

            todoDb2.setCompleted(todoUpdateRequest.getCompleted());
        }
        if(todoUpdateRequest.getTitle() != null){

            todoDb2.setTitle(todoUpdateRequest.getTitle());

        }
        if (todoUpdateRequest.getImage() != null){

            todoDb2.setTodoImageUrl(todoUpdateRequest.getImage());
        }
        Todo todoSave = repositoryTodo.save(todoDb2);

        updateRequest.setCompleted(todoSave.isCompleted());
        updateRequest.setTitle(todoSave.getTitle());
        updateRequest.setImage(todoSave.getTodoImageUrl());
        updateRequest.setId(todoSave.getId());

        return updateRequest;


    }
}
