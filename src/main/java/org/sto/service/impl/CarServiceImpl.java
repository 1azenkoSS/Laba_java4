package org.sto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sto.entity.Car;
import org.sto.repository.CarRepository;
import org.sto.service.CarService;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    @Override
    public Car findById(final Long id) {
        return carRepository.findById(id).orElseThrow();
    }

    @Override
    public String save(final Car car) {
        carRepository.save(car);
        return "Car added successfully";
    }

    @Override
    public String delete(final Long id) {
        carRepository.deleteById(id);
        return "Car deleted successfilly";
    }

    @Override
    public String update(final Long id, final Car car) {
        Car savedCar = findById(id);
        savedCar.setId(car.getId());
        savedCar.setBrand(car.getBrand());
        savedCar.setYear(car.getYear());
        savedCar.setVin(car.getVin());
        savedCar.setModel(car.getModel());
        carRepository.save(savedCar);
        return "Car updated successfully";
    }

}
