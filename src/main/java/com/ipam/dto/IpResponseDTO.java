package com.ipam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IpResponseDTO {
    private String ip;
    private boolean allocated;
}