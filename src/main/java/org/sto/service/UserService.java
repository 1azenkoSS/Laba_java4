package org.sto.service;

import org.sto.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById (final Long id);
    
}
