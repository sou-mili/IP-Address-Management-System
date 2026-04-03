
package com.ipam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipam.model.IPAddress;

public interface IPRepository extends JpaRepository<IPAddress, Long> {

    //  Find IP by address (for duplicate check)
    Optional<IPAddress> findByIp(String ip);

    //  Get all IPs of a subnet
    List<IPAddress> findBySubnetId(Long subnetId);
}