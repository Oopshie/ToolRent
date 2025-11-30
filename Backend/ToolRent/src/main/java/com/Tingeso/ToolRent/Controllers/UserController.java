package com.Tingeso.ToolRent.Controllers;

import com.Tingeso.ToolRent.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/User")
@CrossOrigin

public class UserController {
    @Autowired
    UserService userService;
    


}
