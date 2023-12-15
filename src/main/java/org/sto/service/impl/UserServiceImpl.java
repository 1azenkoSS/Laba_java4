package org.sto.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sto.entity.User;
import org.sto.repository.UserRepository;
import org.sto.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findById(final Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(final @Valid User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(final Long id, final @Valid User user) {
        User savedUser = findById(id);
        savedUser.setId(user.getId());
        savedUser.setFirstName(user.getFirstName());
        savedUser.setLastName(user.getLastName());
        savedUser.setPhoneNumber(user.getPhoneNumber());
    }
}