package com.ipam.repository;

import com.ipam.model.Subnet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubnetRepository extends JpaRepository<Subnet, Long> {
}