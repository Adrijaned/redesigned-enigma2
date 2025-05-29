package cz.adrijaned.inqool.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;
    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Court court;
    @Getter
    @Setter
    private GameType gameType;
    @Getter
    @Setter
    private LocalDateTime fromTime;
    @Getter
    @Setter
    private LocalDateTime toTime;
    @Getter
    @Setter
    private LocalDateTime created;
    @Getter
    @Setter
    private LocalDateTime lastUpdate;
    @Getter
    @Setter
    private boolean valid = true;
    @Getter
    @Setter
    @ManyToOne(optional = false)
    private User user;

    public BigDecimal getPrice() {
        long minutes = (toTime.toEpochSecond(ZoneOffset.UTC) - fromTime.toEpochSecond(ZoneOffset.UTC)) / 60;
        return court.getSurfaceType().getMinutePrice()
                .multiply(gameType.priceMultiplier)
                .multiply(new BigDecimal(minutes));
    }

    public enum GameType {
        DVOJHRA(new BigDecimal("1")),
        CTYRHRA(new BigDecimal("1.5"))
        ;

        public final BigDecimal priceMultiplier;

        GameType(BigDecimal priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
        }
    }
}
