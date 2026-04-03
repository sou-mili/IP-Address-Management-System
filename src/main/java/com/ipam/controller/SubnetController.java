package com.ipam.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipam.model.Subnet;
import com.ipam.service.SubnetService;

@RestController
@RequestMapping("/subnet")
public class SubnetController {

    private final SubnetService service;

    public SubnetController(SubnetService service) {
        this.service = service;
    }

    // ✅ Create subnet (CIDR + auto calculation)
    @PostMapping
    public Subnet create(@RequestBody Subnet subnet) {
        return service.createSubnet(subnet);
    }

    // ✅ Get all subnets (normal)
    @GetMapping
    public List<Subnet> getAll() {
        return service.getAll();
    }

    // ✅ Get all subnets with pagination
    @GetMapping("/page")
    public Page<Subnet> getAllWithPagination(Pageable pageable) {
        return service.getAll(pageable);
    }

    // ✅ Get subnet by ID
    @GetMapping("/{id}")
    public Subnet getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ✅ Update subnet metadata (description)
    @PutMapping("/{id}")
    public Subnet update(@PathVariable Long id, @RequestBody Subnet subnet) {
        return service.update(id, subnet);
    }

    // ✅ Get subnet utilization
    @GetMapping("/utilization/{id}")
    public String getUtilization(@PathVariable Long id) {
        return service.getUtilization(id);
    }

    // ✅ Delete subnet (only if no IP allocated)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Subnet deleted successfully";
    }
}