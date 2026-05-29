package com.motofix.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotoDTO {
    
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private Integer modelYear;
    private String status;
    
    private Long clientId;
    private String clientName;
}