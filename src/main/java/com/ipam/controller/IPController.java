package com.ipam.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ipam.dto.IpRequestDTO;
import com.ipam.model.IPAddress;
import com.ipam.service.IPService;

@RestController
@RequestMapping("/ip")
public class IPController {

    private final IPService service;

    public IPController(IPService service) {
        this.service = service;
    }

    // ✅ Allocate specific IP
    @PostMapping("/allocate-specific")
    public IPAddress allocateSpecific(@RequestBody IpRequestDTO dto) {
        return service.allocateSpecificIp(dto);
    }

    // ✅ Allocate next available IP
    @PostMapping("/allocate-next/{subnetId}")
    public IPAddress allocateNext(@PathVariable Long subnetId) {
        return service.allocateNextAvailable(subnetId);
    }

    // ✅ Bulk IP allocation
    @PostMapping("/bulk/{subnetId}/{count}")
    public List<IPAddress> allocateBulk(@PathVariable Long subnetId, @PathVariable int count) {
        return service.allocateBulk(subnetId, count);
    }

    // ✅ Get all IPs (admin/debug)
    @GetMapping
    public List<IPAddress> getAll() {
        return service.getAll();
    }

    // ✅ Get IPs by subnet
    @GetMapping("/subnet/{subnetId}")
    public List<IPAddress> getBySubnet(@PathVariable Long subnetId) {
        return service.getBySubnet(subnetId);
    }

    // ✅ Release IP
    @PutMapping("/release/{id}")
    public String release(@PathVariable Long id) {
        service.releaseIp(id);
        return "IP Released Successfully";
    }
}