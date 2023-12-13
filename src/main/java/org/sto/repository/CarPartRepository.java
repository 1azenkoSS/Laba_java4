package org.sto.repository;

import org.sto.entity.CarPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarPartRepository extends JpaRepository<CarPart,Long> {
}
