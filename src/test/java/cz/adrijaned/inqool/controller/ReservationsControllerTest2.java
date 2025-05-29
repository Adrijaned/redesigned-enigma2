package cz.adrijaned.inqool.controller;

import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.dto.ReservationDto;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReservationsControllerTest2 {
    @Autowired
    ReservationsController reservationsController;
    @Autowired
    CourtDao courtDao;
    @Autowired
    SurfaceTypeDao surfaceTypeDao;

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

    private void trashDtoFieldAndVerifyPostHttp400(Consumer<ReservationDto> trasher, String expectedMessage) {
        ReservationDto rdto = getTestDto();
        trasher.accept(rdto);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.postReservation(rdto));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        assertEquals(expectedMessage, rse.getReason());
    }

    private void trashDtoFieldAndVerifyPutHttp400(Consumer<ReservationDto> trasher, String expectedMessage) {
        ReservationDto rdto = getTestDto();
        trasher.accept(rdto);
        ResponseStatusException rse = assertThrows(ResponseStatusException.class, () -> reservationsController.putReservation(1L, rdto));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        assertEquals(expectedMessage, rse.getReason());
    }

    @Test
    public void testPostTrashedFieldResultsIn400() {
        SurfaceType surfaceType = new SurfaceType(new BigDecimal("1"), "a");
        surfaceTypeDao.save(surfaceType);
        Court court = new Court(surfaceType, "b");
        courtDao.save(court);
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setReservationDate(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setFromTime(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setToTime(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setUserName(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setPhoneNumber(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setGameType(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setCourtId(null), "Reservation missing a required field");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setReservationDate("2023/12/1"), "Can't parse reservation from/to"); // NON-ISO format
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setFromTime("trash"), "Can't parse reservation from/to");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setToTime("25:15"), "Can't parse reservation from/to");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setToTime("9:59"), "Reservation TO is before reservation FROM");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setPhoneNumber("123456789"), "Not supplied a valid phone number");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setCourtId(8L), "Invalid court id");
        trashDtoFieldAndVerifyPostHttp400(rdto -> rdto.setGameType("TROJHRA"), "Invalid game type");
        reservationsController.postReservation(getTestDto());
        trashDtoFieldAndVerifyPutHttp400(rdto -> rdto.setCourtId(8L), "Invalid court id");
        trashDtoFieldAndVerifyPutHttp400(rdto -> rdto.setGameType("TROJHRA"), "Invalid game type");
    }
}
