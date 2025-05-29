package cz.adrijaned.inqool.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SurfaceTypeTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetMinutePrice() {
        SurfaceType st = new SurfaceType();
        assertThrows(IllegalArgumentException.class, () -> st.setMinutePrice(new BigDecimal("100001")));
        assertDoesNotThrow(() -> st.setMinutePrice(new BigDecimal("10001")));
        assertThrows(IllegalArgumentException.class, () -> st.setMinutePrice(new BigDecimal("10.001")));
        assertDoesNotThrow(() -> st.setMinutePrice(new BigDecimal("100.01")));
        assertEquals(0, new BigDecimal("100.01").compareTo(st.getMinutePrice()));
    }

    @Test
    public void testConstructor() {
        SurfaceType st = new SurfaceType(new BigDecimal("1"), "abcd");
        assertEquals(0, new BigDecimal("1").compareTo(st.getMinutePrice()));
        assertEquals("abcd", st.getName());
    }
}
