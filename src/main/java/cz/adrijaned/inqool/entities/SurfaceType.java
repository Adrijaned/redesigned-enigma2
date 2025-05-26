package cz.adrijaned.inqool.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
public class SurfaceType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;
    @Getter
    @Setter
    private String name;
    @Column(nullable = false, precision = 5, scale = 2)
    @Getter
    private BigDecimal minutePrice;

    public SurfaceType(BigDecimal minutePrice, String name) {
        this.minutePrice = minutePrice;
        this.name = name;
    }

    public void setMinutePrice(BigDecimal minutePrice) {
        minutePrice = minutePrice.stripTrailingZeros();
        if (minutePrice.scale() > 2 || minutePrice.precision() > 5) {
            //TODO throw
        }
        this.minutePrice = minutePrice;
    }
}
