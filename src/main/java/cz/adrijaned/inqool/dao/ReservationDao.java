package cz.adrijaned.inqool.dao;

import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.Reservation;
import cz.adrijaned.inqool.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public class ReservationDao extends AbstractDao<Reservation> {

    protected ReservationDao() {
        super(Reservation.class);
    }

    @PersistenceContext
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Reservation save(Reservation entity) {
        return super.save(entity);
    }

    @Override
    public List<Reservation> list() {
        return listBy(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public List<Reservation> listBy(LocalDate date, Court court, OrderBy orderBy) {
        return listBy(Optional.of(date), Optional.of(court), Optional.of(orderBy), Optional.empty(), Optional.empty());
    }

    public List<Reservation> listBy(Court court, OrderBy orderBy) {
        return listBy(Optional.empty(), Optional.of(court), Optional.of(orderBy), Optional.empty(), Optional.empty());
    }

    public List<Reservation> listBy(User user, LocalDateTime fromTime) {
        return listBy(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(user), Optional.of(fromTime));
    }

    public List<Reservation> listBy(User user) {
        return listBy(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(user), Optional.empty());
    }

    private List<Reservation> listBy(Optional<LocalDate> date, Optional<Court> court, Optional<OrderBy> orderBy, Optional<User> user, Optional<LocalDateTime> fromTime) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT r FROM Reservation r WHERE r.valid = true");
        if (date.isPresent()) queryString.append(" AND r.fromTime BETWEEN :before AND :after ");
        if (court.isPresent()) queryString.append(" AND r.court = :court ");
        if (user.isPresent()) queryString.append(" AND r.user = :user ");
        if (fromTime.isPresent()) queryString.append(" AND r.fromTime > :fromTime ");
        orderBy.ifPresent(orderByReal -> {
            queryString.append(" ORDER BY ").append(orderByReal.s).append(" ASC");
        });
        TypedQuery<Reservation> query = entityManager.createQuery(queryString.toString(), Reservation.class);
        date.ifPresent(dateReal -> {
            LocalDateTime before = LocalDateTime.of(dateReal, LocalTime.MAX).minusDays(1);
            LocalDateTime after = LocalDateTime.of(dateReal, LocalTime.MIN).plusDays(1);
            query.setParameter("before", before).setParameter("after", after);
        });
        court.ifPresent(courtReal -> query.setParameter("court", courtReal));
        user.ifPresent(userReal -> query.setParameter("user", userReal));
        fromTime.ifPresent(fromTimeReal -> query.setParameter("fromTime", fromTimeReal));
        return query.getResultList();
    }

    public enum OrderBy {
        FROMTIME("r.fromTime"),
        TOTIME("r.toTime"),
        CREATED("r.created"),
        LASTUPDATE("r.lastUpdate")
        ;
        public final String s;
        OrderBy(String s) {
            this.s = s;
        }
    }
}
