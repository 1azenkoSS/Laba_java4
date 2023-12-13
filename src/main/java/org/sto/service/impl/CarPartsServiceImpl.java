package org.sto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sto.entity.CarPart;
import org.sto.repository.CarPartRepository;
import org.sto.service.CarOrderService;
import org.sto.service.CarPartsService;

@Service
@RequiredArgsConstructor
public class CarPartsServiceImpl implements CarPartsService {
    private final CarPartRepository carPartRepository;
    @Override
    public CarPart fingById(final Long id) {
        return carPartRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(final CarPart carPart) {
        carPartRepository.save(carPart);
    }

    @Override
    public void delete(final Long id) {
        carPartRepository.deleteById(id);
    }

    @Override
    public void update(final Long id, final CarPart carPart) {
        CarPart savedCarPart = fingById(id);
        savedCarPart.setId(carPart.getId());
        savedCarPart.setCode(carPart.getCode());
        savedCarPart.setPrice(carPart.getPrice());
        savedCarPart.setTitle(carPart.getTitle());
    }
}
