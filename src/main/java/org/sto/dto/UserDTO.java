package org.sto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sto.entity.Car;

import java.util.List;

public class UserDTO {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    @Size
    private List<Car> carsList;
}
