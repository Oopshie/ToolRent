package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.UserEntity;
import com.Tingeso.ToolRent.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;



}



