package org.sto.dto;

import jakarta.persistence.OneToMany;
import org.sto.entity.CarOrder;

import java.util.List;

public class CarDTO {
    private String brand;
    private String model;
    private int year;
    private String vin;

    @OneToMany
    private List<CarOrder> serviceHistory;
}
