package org.sto.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sto.entity.enumerable.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carOrder")
public class CarOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne
    private Car car;
    @OneToOne
    private User user;
    @Column
    private LocalDateTime date;
    @Column
    private Status status;
    @Column
    private BigDecimal price;

    @OneToMany
    private List<CarPart> carParts;
}
