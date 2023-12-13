package org.sto.service;

import org.sto.entity.Car;

import java.util.Optional;

public interface CarService {
    Car findById(final Long id);
    String save(final Car car);
    String delete(final Long id);
    String update(final Long id, final Car car);
}
