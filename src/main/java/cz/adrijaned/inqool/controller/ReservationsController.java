package cz.adrijaned.inqool.controller;

import com.google.i18n.phonenumbers.NumberParseException;
import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.ReservationDao;
import cz.adrijaned.inqool.dao.UserDao;
import cz.adrijaned.inqool.dto.ReservationDto;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.Reservation;
import cz.adrijaned.inqool.entities.User;
import cz.adrijaned.inqool.util._PhoneNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
public class ReservationsController {

    @Autowired
    private CourtDao courtDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ReservationDao reservationDao;

    @GetMapping("/rezervace")
    public List<Reservation> getReservations() {
        return reservationDao.list();
    }

    @PostMapping("/rezervace")
    public BigDecimal postReservation(@RequestBody ReservationDto newReservation) {
        if (newReservation.getPhoneNumber() == null
                || newReservation.getUserName() == null
                || newReservation.getCourtId() == null
                || newReservation.getGameType() == null
                || newReservation.getReservationDate() == null
                || newReservation.getFromTime() == null
                || newReservation.getToTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation missing a required field");
        }
        Reservation reservation = new Reservation();

        setReservationUser(newReservation, reservation);

        Court court = courtDao.find(newReservation.getCourtId());
        if (court == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid court id");
        }
        reservation.setCourt(court);

        setReservationFromToTimes(newReservation, reservation);

        try {
            reservation.setGameType(Reservation.GameType.valueOf(newReservation.getGameType()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game type");
        }

        if (isReservationCollision(reservation)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation for given court already exists during that time");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setCreated(now);
        reservation.setLastUpdate(now);

        return reservationDao.save(reservation).getPrice();
    }

    private static void setReservationFromToTimes(ReservationDto newReservation, Reservation reservation) {
        try {
            LocalDateTime baseDate = LocalDateTime.parse(newReservation.getReservationDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime fromDateTime = baseDate.plus(Duration.between(LocalTime.MIN, LocalTime.parse(newReservation.getFromTime(), DateTimeFormatter.ofPattern("HH:mm"))));
            LocalDateTime toDateTime = baseDate.plus(Duration.between(LocalTime.MIN, LocalTime.parse(newReservation.getToTime(), DateTimeFormatter.ofPattern("HH:mm"))));
            if (toDateTime.isBefore(fromDateTime)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation TO is before reservation FROM");
            }
            reservation.setFromTime(fromDateTime);
            reservation.setToTime(toDateTime);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't parse reservation from/to");
        }
    }

    private void setReservationUser(ReservationDto newReservation, Reservation reservation) {
        try {
            String formatted = _PhoneNumberUtil.normalizePhoneNumber(newReservation.getPhoneNumber());
            User user = userDao.findByPhoneNumber(formatted).orElse(new User(formatted));
            if (!newReservation.getUserName().equals(user.getName())) {
                user.setName(newReservation.getUserName());
                userDao.save(user);
            }
            reservation.setUser(user);
        } catch (NumberParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not supplied a valid phone number");
        }
    }

    private boolean isReservationCollision(Reservation reservation) {
        for (Reservation r : reservationDao.listBy(
                LocalDate.from(reservation.getFromTime()),
                reservation.getCourt(),
                ReservationDao.OrderBy.FROMTIME)) {
            if (r.getId() == reservation.getId()) continue;
            if (r.getFromTime().isBefore(reservation.getFromTime())) {
                if (r.getToTime().isAfter(reservation.getFromTime())) {
                    return true;
                }
            } else {
                if (r.getFromTime().isBefore(reservation.getToTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    @PutMapping("/rezervace/{id}")
    public Reservation putReservation(@PathVariable Long id, @RequestBody ReservationDto newValues) {
        Reservation reservation = reservationDao.find(id);
        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown reservation");
        }
        if (newValues.getPhoneNumber() != null) {
            if (newValues.getUserName() == null) {
                newValues.setUserName(reservation.getUser().getName());
                setReservationUser(newValues, reservation);
            }
        }
        if (newValues.getUserName() != null) {
            if (!reservation.getUser().getName().equals(newValues.getUserName())) {
                reservation.getUser().setName(newValues.getUserName());
                userDao.save(reservation.getUser());
            }
        }
        if (newValues.getCourtId() != null) {
            Court court = courtDao.find(newValues.getCourtId());
            if (court == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid court id");
            }
            reservation.setCourt(court);
        }
        if (newValues.getGameType() != null) {
            try {
                reservation.setGameType(Reservation.GameType.valueOf(newValues.getGameType()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game type");
            }
        }

        if (newValues.getReservationDate() != null || newValues.getToTime() != null || newValues.getFromTime() != null) {
            if (newValues.getReservationDate() == null) {
                newValues.setReservationDate(reservation.getFromTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            if (newValues.getFromTime() == null) {
                newValues.setFromTime(reservation.getFromTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            if (newValues.getToTime() == null) {
                newValues.setToTime(reservation.getToTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            setReservationFromToTimes(newValues, reservation);
        }

        if (isReservationCollision(reservation)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation for given court already exists during that time");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setLastUpdate(now);

        return reservationDao.save(reservation);
    }

    @DeleteMapping("/rezervace/{id}")
    public Reservation deleteReservation(@PathVariable Long id) {
        Reservation reservation = reservationDao.find(id);
        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown reservation");
        }
        if (!reservation.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already deleted");
        }
        reservation.setValid(false);
        return reservationDao.save(reservation);
    }

    @GetMapping("/rezervace/kurt/{id}")
    public List<Reservation> getReservationsByCourtId(@PathVariable Long id) {
        Court court = courtDao.find(id);
        if (court == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown court");
        }
        return reservationDao.listBy(court, ReservationDao.OrderBy.CREATED);
    }

    @GetMapping("/rezervace/uzivatel/{phoneNumber}")
    public List<Reservation> getReservationsByUser(@PathVariable String phoneNumber, @RequestParam(required = false, defaultValue = "false") Boolean futureOnly) {
        User user;
        try {
            user = userDao.findByPhoneNumber(phoneNumber).orElseThrow(() -> new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, ""));
        } catch (NumberParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user or invalid phone number");
        }
        if (futureOnly) {
            return reservationDao.listBy(user, LocalDateTime.now());
        } else {
            return reservationDao.listBy(user);
        }
    }

}
