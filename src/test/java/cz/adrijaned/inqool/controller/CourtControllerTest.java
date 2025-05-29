package cz.adrijaned.inqool.controller;

import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.dto.CourtSimplifiedDto;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourtControllerTest {
    @Mock
    SurfaceTypeDao surfaceTypeDao;
    @Mock
    CourtDao courtDao;

    @InjectMocks
    CourtController courtController;

    private final SurfaceType surfaceType = new SurfaceType(new BigDecimal(1), "a");
    private final SurfaceType surfaceType2 = new SurfaceType(new BigDecimal(2), "b");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testList() {
        List<Court> testData = List.of(
                new Court(surfaceType, "a"),
                new Court(surfaceType, "b")
        );
        when(courtDao.list()).thenReturn(testData);
        var res = courtController.getCourts();
        assertEquals(2, res.size());
        assertEquals(testData.get(0), res.get(0));
        assertEquals(testData.get(1), res.get(1));
    }

    @Test
    public void testIncompletePostReturning400() {
        when(surfaceTypeDao.find(anyLong())).thenReturn(null);
        final CourtSimplifiedDto court = new CourtSimplifiedDto();
        court.setName("a");
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.postCourt(court));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());

        when(surfaceTypeDao.find(1L)).thenReturn(surfaceType);
        final CourtSimplifiedDto court2 = new CourtSimplifiedDto();
        court2.setSurfaceTypeId(1L);
        exc = assertThrows(ResponseStatusException.class, () -> courtController.postCourt(court2));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());
    }

    @Test
    public void testPostBeingSaved() {
        when(surfaceTypeDao.find(1L)).thenReturn(surfaceType2);
        final CourtSimplifiedDto court = new CourtSimplifiedDto();
        court.setName("a");
        court.setSurfaceTypeId(1L);
        courtController.postCourt(court);
        ArgumentCaptor<Court> captor = ArgumentCaptor.forClass(Court.class);
        verify(courtDao).save(captor.capture());
        assertEquals("a", captor.getValue().getName());
        assertEquals(surfaceType2, captor.getValue().getSurfaceType());
    }

    @Test
    public void testPutInvalidCourtThrows404() {
        when(courtDao.find(anyLong())).thenReturn(null);
        CourtSimplifiedDto data = new CourtSimplifiedDto();
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.putCourt(1L, data));
        assertEquals(HttpStatus.NOT_FOUND, exc.getStatusCode());
    }

    @Test
    public void testPutDeletedCourtThrows400() {
        Court c = new Court();
        c.setValid(false);
        when(courtDao.find(1L)).thenReturn(c);
        CourtSimplifiedDto data = new CourtSimplifiedDto();
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.putCourt(1L, data));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());
    }

    @Test
    public void testPutInvalidSurfaceTypeThrows400() {
        Court c = new Court();
        when(courtDao.find(1L)).thenReturn(c);
        when(surfaceTypeDao.find(anyLong())).thenReturn(null);
        CourtSimplifiedDto data = new CourtSimplifiedDto();
        data.setSurfaceTypeId(1L);
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.putCourt(1L, data));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());
    }

    @Test
    public void testPutCorrectWorks() {
        Court c = mock(Court.class);
        when(c.isValid()).thenReturn(true);
        when(courtDao.find(2L)).thenReturn(c);
        SurfaceType s = new SurfaceType();
        SurfaceType s2 = new SurfaceType();
        when(surfaceTypeDao.find(2L)).thenReturn(s);
        when(surfaceTypeDao.find(3L)).thenReturn(s2);
        CourtSimplifiedDto data = new CourtSimplifiedDto();
        data.setSurfaceTypeId(2L);
        data.setName("abcd");
        courtController.putCourt(2L, data);
        verify(c, times(1)).setName("abcd");
        verify(c, times(1)).setSurfaceType(s);
        verify(courtDao, times(1)).save(c);
        data.setSurfaceTypeId(null);
        data.setName("abcde");
        courtController.putCourt(2L, data);
        verify(c, times(1)).setName("abcde");
        verify(c, times(1)).setSurfaceType(s);
        data.setSurfaceTypeId(3L);
        data.setName(null);
        courtController.putCourt(2L, data);
        verify(c, times(2)).setName(anyString());
        verify(c, times(2)).setSurfaceType(any());
    }

    @Test
    public void testDeletingUnknownSurfaceTypeFailsWith400() {
        when(courtDao.find(anyLong())).thenReturn(null);
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.deleteCourt(5L));
        assertEquals(HttpStatus.NOT_FOUND, exc.getStatusCode());
    }

    @Test
    public void testDeletingAlreadyDeletedFailsWith404() {
        Court court = new Court();
        court.setValid(false);
        when(courtDao.find(anyLong())).thenReturn(court);
        ResponseStatusException exc = assertThrows(ResponseStatusException.class, () -> courtController.deleteCourt(5L));
        assertEquals(HttpStatus.BAD_REQUEST, exc.getStatusCode());
    }

    @Test
    public void testDeletingWorks() {
        Court court = mock(Court.class);
        when(courtDao.find(1L)).thenReturn(court);
        when(court.isValid()).thenReturn(true);
        courtController.deleteCourt(1L);
        verify(courtDao, times(1)).find(1L);
        verify(court, times(1)).setValid(false);
        verify(courtDao, times(1)).save(court);
    }
}
