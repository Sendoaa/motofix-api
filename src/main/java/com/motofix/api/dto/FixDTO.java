package com.motofix.api.dto;

import com.motofix.api.model.FixStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixDTO {

    private Long id;
    private String description;
    private String technicalNotes;
    private BigDecimal totalCost;
    private FixStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private Long motoId;
    private String motoBrand;
    private String motoModel;
    private String licensePlate;
}