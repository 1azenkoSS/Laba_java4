package org.sto.service.impl;

import lombok.RequiredArgsConstructor;
import org.sto.entity.User;
import org.sto.repository.UserRepository;
import org.sto.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Override
    public Optional<User> getUserById(final Long id) {
        return userRepository.findById(id);
    }

}
