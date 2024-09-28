package com.echobot.echobotexample.repository;

import com.echobot.echobotexample.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByPlaka(String plaka);
}
