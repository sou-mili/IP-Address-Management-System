
package com.ipam.dto;

import lombok.Data;

@Data
public class IpRequestDTO {
    private Long subnetId;
    private String ip;
    private String hostname;
    private String macAddress;
    private String deviceType;
    private String owner;
}