package org.sto.resource;

import org.sto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public String getUser() {
        return "user request";
    }

}
