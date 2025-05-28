package cz.adrijaned.inqool.dto;

import lombok.Data;

@Data
public class ReservationDto {
    private String phoneNumber;
    private String userName;
    private Long courtId;
    private String reservationDate;
    private String fromTime;
    private String toTime;
    private String gameType;
}
