package cz.adrijaned.inqool.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SurfaceTypeSimplifiedDto {
    private String name;
    private BigDecimal minutePrice;
}
