package com.proyekt.user.service.impl;


/*
1-ci userde gedib useri tapirsan (username) userin Repositorysinde
2-ci hemin userin id sini yoluyursan todonun repositorysine DB den ceksin
*
*
*
*
* */

import com.proyekt.user.dto.DtoTodo;
import com.proyekt.user.dto.TodoRequest;
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

        if (userDB.isEmpty()){

            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST,null));
        }

        User user = userDB.get();

        List<Todo> todoList = repositoryTodo.findByUserId(user.getId());

        if (todoList != null){
            for (Todo todo : todoList){

                DtoTodo dtoTodo = new DtoTodo();

                dtoTodo.setId(todo.getId());
                dtoTodo.setTitle(todo.getTitle());
                dtoTodo.setUserId(todo.getUser().getId());
                dtoTodo.setCompleted(todo.isCompleted());

                dtoTodos.add(dtoTodo);

            }
        }

        return dtoTodos;
    }




    @Override
    public DtoTodo saveTodo(TodoRequest todoRequest, String userName) {

        DtoTodo dtoTodo = new DtoTodo();

       Optional <User> user = repositoryUser.findByUserName(userName);

       Todo todo = new Todo();

       todo.setUser(user.get());
       todo.setCompleted(todoRequest.isCompleted());
       todo.setTitle(todoRequest.getTitle());

       Todo tododb = repositoryTodo.save(todo);


       dtoTodo.setId(tododb.getId());
       dtoTodo.setCompleted(tododb.isCompleted());
       dtoTodo.setTitle(tododb.getTitle());
       dtoTodo.setUserId(tododb.getUser().getId());

        return dtoTodo;
    }

    @Override
    public DtoTodo updateTodo(Long id, TodoRequest todoRequest, String userName) {

        DtoTodo dtoTodo = new DtoTodo();

        Todo todo = new Todo();

        Optional<User> userID = repositoryUser.findByUserName(userName);

        if (userID.isEmpty()){
            System.out.println("user tapilamdi");
        }

       Optional<Todo> todoOptional = repositoryTodo.findByIdAndUserId(id,userID.get().getId());


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
    public void deleteTodo(Long id, String userName){

       Optional<User> userID = repositoryUser.findByUserName(userName);

       if (userID.isEmpty()){
           throw new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND,id.toString()));
       }

       repositoryTodo.deleteByIdAndUserId(id, userID.get().getId());

    }

}
