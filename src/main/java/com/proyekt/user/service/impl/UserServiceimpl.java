package com.proyekt.user.service.impl;

import com.proyekt.user.dto.DtoTodo;
import com.proyekt.user.dto.DtoUser;
import com.proyekt.user.dto.TodoRequest;
import com.proyekt.user.model.Todo;
import com.proyekt.user.model.User;
import com.proyekt.user.repository.RepositoryUser;
import com.proyekt.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceimpl implements IUserService {

    @Autowired
    private RepositoryUser repositoryUser;


    @Override
    public DtoUser allList(String id, String userName) {

        DtoUser dtoUser = new DtoUser();

        Optional<User> userDB = repositoryUser.findByUserName(userName);

        if (userDB.isEmpty()){
            System.out.println("bu Username yoxdur");
        }

        dtoUser.setUserName(userDB.get().getUsername());
        dtoUser.setId(userDB.get().getId());

        List<Todo> todoList = userDB.get().getTodos();
        if (todoList != null && !todoList.isEmpty()){

            for (Todo todo : todoList){

                DtoTodo dtoTodo = new DtoTodo();


                dtoTodo.setUserId(dtoTodo.getUserId());
                dtoTodo.setId(todo.getId());
                dtoTodo.setCompleted(todo.isCompleted());
                dtoTodo.setTitle(todo.getTitle());

                dtoUser.getTodos().add(dtoTodo);
            }
        }
        return dtoUser;
    }

    @Override
    public void updateProfileImage(Long userId, String imageUrl) {
        User user = repositoryUser.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tapılmadı"));
        user.setProfileImageUrl(imageUrl);
        repositoryUser.save(user);
    }
}
