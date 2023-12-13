package org.sto.service;

import org.sto.dto.CarOrderDTO;
import org.sto.dto.CarPartDTO;
import org.sto.entity.CarOrder;
import org.sto.entity.enumerable.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CarOrderService {
    void addCarOrder(final CarOrderDTO carOrderDTO);
    List<CarOrder> getAllOrders();
    CarOrder findById(final Long id);
    List<CarOrder> displayCarsByProjectStatus(final Status status);
    void updateOrderStatus(final Long carOrderId, final Status status);
    void addCarPart(final Long orderId, final CarPartDTO carPartDTO);
    void setCarOrderPrice(final Long orderId, final double price);
}
