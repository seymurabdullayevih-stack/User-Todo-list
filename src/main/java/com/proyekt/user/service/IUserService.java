package com.proyekt.user.service;

import com.proyekt.user.dto.DtoUser;

public interface IUserService {


    public DtoUser allList(String id,String userName);

    public void updateProfileImage(Long userId, String imageUrl);
}
