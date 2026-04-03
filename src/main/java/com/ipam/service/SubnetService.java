package com.ipam.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipam.model.IPAddress;
import com.ipam.model.Subnet;
import com.ipam.repository.IPRepository;
import com.ipam.repository.SubnetRepository;
import com.ipam.util.IPUtils;

@Service
public class SubnetService {

    private final SubnetRepository repo;
    private final IPRepository ipRepo;

    public SubnetService(SubnetRepository repo, IPRepository ipRepo) {
        this.repo = repo;
        this.ipRepo = ipRepo;
    }

    //  Create Subnet with CIDR validation + calculation
    public Subnet createSubnet(Subnet subnet) {

        if (!IPUtils.isValidCIDR(subnet.getCidr())) {
            throw new RuntimeException("Invalid CIDR format");
        }

        IPUtils.calculateSubnetDetails(subnet);

        return repo.save(subnet);
    }

    //  Get all subnets (normal)
    public List<Subnet> getAll() {
        return repo.findAll();
    }

    //  Pagination support
    public Page<Subnet> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    //  Get subnet by ID
    public Subnet getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subnet not found"));
    }

    //  Update subnet metadata
    public Subnet update(Long id, Subnet updatedSubnet) {

        Subnet subnet = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subnet not found"));

        subnet.setDescription(updatedSubnet.getDescription());

        return repo.save(subnet);
    }

    //  Subnet utilization
    public String getUtilization(Long subnetId) {

        Subnet subnet = repo.findById(subnetId)
                .orElseThrow(() -> new RuntimeException("Subnet not found"));

        long total = subnet.getTotalIps();

        long used = ipRepo.findBySubnetId(subnetId)
                .stream()
                .filter(IPAddress::isAllocated)
                .count();

        long free = total - used;

        return "Total: " + total + ", Used: " + used + ", Free: " + free;
    }

    //  Delete only if no IP allocated
    public void delete(Long id) {

        Subnet subnet = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Subnet not found"));

        long used = ipRepo.findBySubnetId(id)
                .stream()
                .filter(IPAddress::isAllocated)
                .count();

        if (used > 0) {
            throw new RuntimeException("Cannot delete subnet. IPs are still allocated.");
        }

        repo.delete(subnet);
    }
}