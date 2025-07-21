package com.codagis.nordeste_servicos.dto;

import com.codagis.nordeste_servicos.model.StatusOS;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private StatusOS status;
} 