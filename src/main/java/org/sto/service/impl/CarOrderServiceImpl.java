package org.sto.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sto.dto.CarOrderDTO;
import org.sto.dto.CarPartDTO;
import org.sto.entity.CarOrder;
import org.sto.entity.CarPart;
import org.sto.entity.enumerable.Status;
import org.sto.exception.BadRequestException;
import org.sto.repository.CarOrderRepository;
import org.sto.service.CarOrderService;
import org.springframework.stereotype.Service;
import org.sto.service.CarService;
import org.sto.service.UserService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CarOrderServiceImpl implements CarOrderService {
    private final CarOrderRepository carOrderRepository;
    private final CarService carService;
    private final UserService userService;
    @Override
    public void addCarOrder(final @Valid CarOrderDTO carOrderDTO) {
        CarOrder carOrder = carOrderDTOToEntity(carOrderDTO);
        carService.save(carOrder.getCar());
        userService.save(carOrder.getUser());
        carOrderRepository.save(carOrder);
    }

    @Override
    public List<CarOrder> getAllOrders() {
        List<CarOrder> carOrders = carOrderRepository.findAll().stream().sorted(Comparator.comparing(CarOrder::getStatus))
                .toList();
        if (carOrders.isEmpty()) {
            throw new BadRequestException("Order list is empty");
        }
        return carOrders;
    }

    @Override
    public CarOrder findById(final Long id) {
        return carOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Order with this id does not exist"));

    }

    @Override
    public List<CarOrder> displayCarsByProjectStatus(final Status status) {
        List<CarOrder> carOrders = carOrderRepository.findAllByStatus(status)
                .stream()
                .toList();
        if (carOrders.isEmpty()) {
            throw new BadRequestException("No car orders found with status: " + status);
        }
        return carOrders;
    }

    @Override
    public void updateOrderStatus(final Long orderId, final Status status) {
        final CarOrder savedCarOrder = findById(orderId);
            savedCarOrder.setStatus(status);
        savedCarOrder.setStatus(status);
        carOrderRepository.save(savedCarOrder);
    }

    @Override
    public void addCarPart(final Long orderId, final @Valid CarPartDTO carPartDTO) {
        final CarOrder savedCarOrder = findById(orderId);
        CarPart carPart = carPartsDTOToEntity(carPartDTO);
        savedCarOrder.getCarParts().add(carPart);
        carOrderRepository.save(savedCarOrder);
    }

    public void setCarOrderPrice(final Long orderId, final double price) {
        findById(orderId).setPrice(BigDecimal.valueOf(price));
    }


    private CarOrder carOrderDTOToEntity(final CarOrderDTO carOrderDTO) {
        CarOrder carOrder = new CarOrder();
        carOrder.setCar(carOrderDTO.getCar());
        carOrder.setDate(carOrderDTO.getDate());
        carOrder.setUser(carOrderDTO.getUser());
        carOrder.setStatus(Status.IN_PROCESS);
        return carOrder;
    }

    private CarPart carPartsDTOToEntity(final CarPartDTO carPartDTO) {
        CarPart carPart = new CarPart();
        carPart.setCode(carPartDTO.getCode());
        carPart.setTitle(carPartDTO.getTitle());
        carPart.setPrice(carPartDTO.getPrice());
        return carPart;
    }
}
