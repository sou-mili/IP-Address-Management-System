package com.ipam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ipam.dto.IpRequestDTO;
import com.ipam.model.IPAddress;
import com.ipam.model.Subnet;
import com.ipam.repository.IPRepository;
import com.ipam.repository.SubnetRepository;
import com.ipam.util.IPUtils;

@Service
public class IPService {

    private final IPRepository ipRepo;
    private final SubnetRepository subnetRepo;

    public IPService(IPRepository ipRepo, SubnetRepository subnetRepo) {
        this.ipRepo = ipRepo;
        this.subnetRepo = subnetRepo;
    }

    //  Get all IPs (FIX for your error)
    public List<IPAddress> getAll() {
        return ipRepo.findAll();
    }

    //  Allocate specific IP
    public IPAddress allocateSpecificIp(IpRequestDTO dto) {

        Subnet subnet = subnetRepo.findById(dto.getSubnetId())
                .orElseThrow(() -> new RuntimeException("Subnet not found"));

        //  Duplicate check
        Optional<IPAddress> existing = ipRepo.findByIp(dto.getIp());
        if (existing.isPresent() && existing.get().isAllocated()) {
            throw new RuntimeException("IP already allocated");
        }

        //  Check IP inside subnet
        long ip = IPUtils.ipToLong(dto.getIp());
        long first = IPUtils.ipToLong(subnet.getFirstIp());
        long last = IPUtils.ipToLong(subnet.getLastIp());

        if (ip < first || ip > last) {
            throw new RuntimeException("IP not in subnet range");
        }

        IPAddress ipAddress = new IPAddress();
        ipAddress.setIp(dto.getIp());
        ipAddress.setAllocated(true);
        ipAddress.setHostname(dto.getHostname());
        ipAddress.setMacAddress(dto.getMacAddress());
        ipAddress.setDeviceType(dto.getDeviceType());
        ipAddress.setOwner(dto.getOwner());
        ipAddress.setSubnet(subnet);

        return ipRepo.save(ipAddress);
    }

    //  Allocate next available IP
    public IPAddress allocateNextAvailable(Long subnetId) {

        Subnet subnet = subnetRepo.findById(subnetId)
                .orElseThrow(() -> new RuntimeException("Subnet not found"));

        List<IPAddress> existingIps = ipRepo.findBySubnetId(subnetId);

        long start = IPUtils.ipToLong(subnet.getFirstIp());
        long end = IPUtils.ipToLong(subnet.getLastIp());

        for (long i = start; i <= end; i++) {

            String ipStr = IPUtils.longToIp(i);

            boolean used = existingIps.stream()
                    .anyMatch(ip -> ip.getIp().equals(ipStr) && ip.isAllocated());

            if (!used) {
                IPAddress newIp = new IPAddress();
                newIp.setIp(ipStr);
                newIp.setAllocated(true);
                newIp.setSubnet(subnet);
                return ipRepo.save(newIp);
            }
        }

        throw new RuntimeException("No available IPs");
    }

    //  Bulk IP allocation
    public List<IPAddress> allocateBulk(Long subnetId, int count) {

        List<IPAddress> allocated = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            allocated.add(allocateNextAvailable(subnetId));
        }

        return allocated;
    }

    //  Release IP
    public void releaseIp(Long id) {
        IPAddress ip = ipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("IP not found"));

        ip.setAllocated(false);
        ipRepo.save(ip);
    }

    //  Get IPs by subnet
    public List<IPAddress> getBySubnet(Long subnetId) {
        return ipRepo.findBySubnetId(subnetId);
    }
}