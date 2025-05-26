package cz.adrijaned.inqool;

import cz.adrijaned.inqool.controller.SurfaceTypeController;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.dto.SurfaceTypeSimplifiedDto;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SurfaceTypeControllerTest {
    @Mock
    SurfaceTypeDao surfaceTypeDao;

    @InjectMocks
    SurfaceTypeController surfaceTypeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testList() {
        List<SurfaceType> testData = List.of(
                new SurfaceType(new BigDecimal(1), "a"),
                new SurfaceType(new BigDecimal(2), "b")
        );
        when(surfaceTypeDao.list()).thenReturn(testData);
        var res = surfaceTypeController.getSurfaceTypes();
        assertEquals(2, res.size());
        assertEquals(testData.get(0), res.get(0));
        assertEquals(testData.get(1), res.get(1));
    }

    @Test
    public void testIncompletePostReturning400() {
        final SurfaceTypeSimplifiedDto surfaceType = new SurfaceTypeSimplifiedDto();
        surfaceType.setMinutePrice(new BigDecimal(1));
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> surfaceTypeController.postSurfaceType(surfaceType));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());

        final SurfaceTypeSimplifiedDto surfaceType2 = new SurfaceTypeSimplifiedDto();
        surfaceType2.setName("a");
        exc = assertThrows(ResponseStatusException.class, () -> surfaceTypeController.postSurfaceType(surfaceType2));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());
    }

    @Test
    public void testPostBeingSaved() {
        when(surfaceTypeDao.save(any())).thenAnswer((Answer<SurfaceType>) invocation -> new SurfaceType(new BigDecimal(1), "b"));
        SurfaceTypeSimplifiedDto data = new SurfaceTypeSimplifiedDto();
        data.setName("a");
        data.setMinutePrice(new BigDecimal(10));
        SurfaceType res = surfaceTypeController.postSurfaceType(data);
        assertEquals("b", res.getName());
    }

    @Test
    public void test404WhenUpdatingNonExistent() {
        when(surfaceTypeDao.find(anyLong())).thenReturn(null);
        var data = new SurfaceTypeSimplifiedDto();
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> surfaceTypeController.putSurfaceType(1L, data));
        assertEquals(HttpStatus.NOT_FOUND, exc.getStatusCode());
    }

    @Test
    public void testUpdatingAllSuppliedFieldsAndNothingElse() {
        SurfaceType mocked = mock(SurfaceType.class);
        when(surfaceTypeDao.find(1L)).thenReturn(mocked);
        SurfaceTypeSimplifiedDto data;

        data = new SurfaceTypeSimplifiedDto();
        surfaceTypeController.putSurfaceType(1L, data);
        verify(mocked, times(0)).setMinutePrice(any());
        verify(mocked, times(0)).setName(any());

        data = new SurfaceTypeSimplifiedDto();
        data.setMinutePrice(new BigDecimal(1));
        surfaceTypeController.putSurfaceType(1L, data);
        verify(mocked, times(1)).setMinutePrice(any());
        verify(mocked, times(0)).setName(any());

        data = new SurfaceTypeSimplifiedDto();
        data.setName("a");
        surfaceTypeController.putSurfaceType(1L, data);
        verify(mocked, times(1)).setMinutePrice(any());
        verify(mocked, times(1)).setName(any());

        data = new SurfaceTypeSimplifiedDto();
        data.setMinutePrice(new BigDecimal(1));
        data.setName("a");
        surfaceTypeController.putSurfaceType(1L, data);
        verify(mocked, times(2)).setMinutePrice(any());
        verify(mocked, times(2)).setName(any());
    }
}
