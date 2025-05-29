package cz.adrijaned.inqool.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGameTypeValues() {
        assertEquals(new BigDecimal("1"), Reservation.GameType.DVOJHRA.priceMultiplier);
        assertEquals(new BigDecimal("1.5"), Reservation.GameType.CTYRHRA.priceMultiplier);
    }

    @Test
    public void testPriceCalculation() {
        SurfaceType st1 = new SurfaceType(new BigDecimal("1.5"), "a");
        SurfaceType st2 = new SurfaceType(new BigDecimal("2.3"), "a");
        Court c1 = new Court(st1, "a");
        Court c2 = new Court(st2, "a");
        Reservation r1 = new Reservation();
        r1.setFromTime(LocalDateTime.of(2023, 12, 1, 10, 0));
        r1.setToTime(LocalDateTime.of(2023, 12, 1, 11, 0));
        r1.setCourt(c1);
        r1.setGameType(Reservation.GameType.DVOJHRA);
        assertEquals(0, new BigDecimal("90").compareTo(r1.getPrice()));
        r1.setGameType(Reservation.GameType.CTYRHRA);
        r1.setCourt(c2);
        r1.setToTime(LocalDateTime.of(2023, 12, 1, 10, 50));
        assertEquals(0, new BigDecimal("172.5").compareTo(r1.getPrice()));
    }
}
