package org.sto.resource;

import org.sto.dto.CarOrderDTO;
import org.sto.dto.CarPartDTO;
import org.sto.entity.CarOrder;
import org.sto.entity.enumerable.Status;
import org.sto.service.CarOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/carOrder")
public class CarOrderResource {

    @Autowired
    private CarOrderService carOrderService;

    @PostMapping("/addOrder")
    public void addOrder(final @RequestBody CarOrderDTO carOrderDTO ) {
        carOrderService.addCarOrder(carOrderDTO);
    }
    @GetMapping("/getAllOrders")
    public List<CarOrder> getAllOrders() {
       return carOrderService.getAllOrders();
    }
    @GetMapping("/projectStatus/{status}")
    public List<CarOrder> displayOrderByStatus(final @PathVariable Status status) {
       return carOrderService.displayCarsByProjectStatus(status);
    }
    @PatchMapping("/updateStatus/{orderId}")
    public void updateOrderStatus(final @PathVariable Long orderId, final @RequestParam Status status) {
        carOrderService.updateOrderStatus(orderId, status);
    }
    @PostMapping("/addCarPart/{orderId}")
    public void addCarParts(final @PathVariable Long orderId, final @RequestBody CarPartDTO carPartDTO) {
        carOrderService.addCarPart(orderId, carPartDTO);
    }
    @PatchMapping("/setPrice")
    public void addCarParts(final @PathVariable Long orderId, final @RequestParam double price) {
        carOrderService.setCarOrderPrice(orderId, price);
    }
}

