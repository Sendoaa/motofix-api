package com.motofix.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MechanicDTO {
    
    private Long id;
    private String name;
    private String specialty;
    private boolean available;
}