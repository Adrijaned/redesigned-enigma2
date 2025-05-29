package cz.adrijaned.inqool.controller;

import com.google.i18n.phonenumbers.NumberParseException;
import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.ReservationDao;
import cz.adrijaned.inqool.dao.UserDao;
import cz.adrijaned.inqool.dto.ReservationDto;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.Reservation;
import cz.adrijaned.inqool.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReservationsControllerTest {
    @Mock
    UserDao userDao;
    @Mock
    ReservationDao reservationDao;
    @Mock
    CourtDao courtDao;
    @InjectMocks
    ReservationsController reservationsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGet() {
        List<Reservation> res = new ArrayList<>();
        when(reservationDao.list()).thenReturn(res);
        assertEquals(res, reservationsController.getReservations());
    }

    private ReservationDto getTestDto() {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setReservationDate("2023-12-01");
        reservationDto.setFromTime("10:00");
        reservationDto.setToTime("11:00");
        reservationDto.setUserName("Petr Novák");
        reservationDto.setPhoneNumber("605 477 976");
        reservationDto.setGameType("DVOJHRA");
        reservationDto.setCourtId(1L);
        return reservationDto;
    }

    @Test
    public void testCollidingReservationReturns409() {
        Court court = new Court();
        when(courtDao.find(1L)).thenReturn(court);
        Reservation blocking = new Reservation();
        blocking.setFromTime(LocalDateTime.of(2023, 12, 1, 8, 0));
        blocking.setToTime(LocalDateTime.of(2023, 12, 1, 10, 0));
        blocking.setCourt(court);
        blocking.setId(1L);
        when(reservationDao.listBy(any(), eq(court), eq(ReservationDao.OrderBy.FROMTIME))).thenReturn(List.of(blocking));
        ReservationDto candidate2 = getTestDto();
        candidate2.setFromTime("8:30");
        candidate2.setToTime("9:30");
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.postReservation(candidate2));
        assertEquals(HttpStatus.CONFLICT, rse.getStatusCode());
        ReservationDto candidate3 = getTestDto();
        candidate3.setFromTime("7:30");
        candidate3.setToTime("10:30");
         rse = assertThrows(ResponseStatusException.class, () -> reservationsController.postReservation(candidate3));
        assertEquals(HttpStatus.CONFLICT, rse.getStatusCode());
        ReservationDto candidate4 = getTestDto();
        candidate4.setFromTime("7:30");
        candidate4.setToTime("8:30");
         rse = assertThrows(ResponseStatusException.class, () -> reservationsController.postReservation(candidate4));
        assertEquals(HttpStatus.CONFLICT, rse.getStatusCode());
        ReservationDto candidate5 = getTestDto();
        candidate5.setFromTime("9:30");
        candidate5.setToTime("10:30");
         rse = assertThrows(ResponseStatusException.class, () -> reservationsController.postReservation(candidate5));
        assertEquals(HttpStatus.CONFLICT, rse.getStatusCode());
    }

    @Test
    public void testDeleteUnknownResults404() {
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.deleteReservation(1L));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
    }

    @Test
    public void testDeleteAlreadyDeletedResults400() {
        Reservation r = new Reservation();
        r.setValid(false);
        when(reservationDao.find(1L)).thenReturn(r);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.deleteReservation(1L));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
    }

    @Test
    public void testDeletingWorks() {
        Reservation r = mock(Reservation.class);
        when(reservationDao.find(1L)).thenReturn(r);
        when(r.isValid()).thenReturn(true);
        reservationsController.deleteReservation(1L);
        verify(reservationDao, times(1)).find(1L);
        verify(r, times(1)).setValid(false);
        verify(reservationDao, times(1)).save(r);
    }

    @Test
    public void testListByCourtUnknownResults404() {
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.getReservationsByCourtId(1L));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
    }

    @Test
    public void testListByCourtWorks() {
        Court c = new Court();
        when(courtDao.find(1L)).thenReturn(c);
        List<Reservation> l = new ArrayList<>();
        when(reservationDao.listBy(c, ReservationDao.OrderBy.CREATED)).thenReturn(l);
        assertEquals(l, reservationsController.getReservationsByCourtId(1L));
    }

    @Test
    public void testListByUserUnknownUserResults404() throws NumberParseException {
        when(userDao.findByPhoneNumber("123456789")).thenReturn(Optional.empty());
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.getReservationsByUser("123456789", false));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
    }

    @Test
    public void testListByUserWorks() throws NumberParseException {
        User u = new User();
        List<Reservation> l = new ArrayList<>();
        when(userDao.findByPhoneNumber("123456789")).thenReturn(Optional.of(u));
        when(reservationDao.listBy(any(User.class), any())).thenReturn(l);
        when(reservationDao.listBy(any(User.class))).thenReturn(l);
        verify(reservationDao, times(0)).listBy(any(User.class), any());
        verify(reservationDao, times(0)).listBy(any(User.class));
        assertEquals(l, reservationsController.getReservationsByUser("123456789", false));
        verify(reservationDao, times(0)).listBy(any(User.class), any());
        verify(reservationDao, times(1)).listBy(any(User.class));
        assertEquals(l, reservationsController.getReservationsByUser("123456789", true));
        verify(reservationDao, times(1)).listBy(any(User.class), any());
        verify(reservationDao, times(1)).listBy(any(User.class));
    }

    @Test
    public void testPutUnknownReservationResults404() {
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.putReservation(1L, getTestDto()));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
    }

    @Test
    public void testPutReservationWorks() throws NumberParseException {
        User u = mock(User.class);
        when(u.getName()).thenReturn("Pepa");
        Reservation r = mock(Reservation.class);
        when(r.getUser()).thenReturn(u);
        when(r.getFromTime()).thenReturn(LocalDateTime.MIN);
        when(r.getToTime()).thenReturn(LocalDateTime.MAX);
        Court c = mock(Court.class);
        when(reservationDao.find(1L)).thenReturn(r);
        when(reservationDao.listBy(any(), any(), any())).thenReturn(List.of());
        when(courtDao.find(1L)).thenReturn(c);
        when(userDao.findByPhoneNumber("605 477 976")).thenReturn(Optional.empty());
        ReservationDto rdto = new ReservationDto();
        rdto.setPhoneNumber("605 477 976");
        rdto.setCourtId(1L);
        rdto.setGameType("CTYRHRA");
        rdto.setReservationDate("2023-12-02");
        reservationsController.putReservation(1L, rdto);
        verify(r).setCourt(c);
        verify(r).setGameType(Reservation.GameType.CTYRHRA);
        verify(userDao).saveUser(any());
        rdto = new ReservationDto();
    }
}
