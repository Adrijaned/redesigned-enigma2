package cz.adrijaned.inqool.controller;

import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.dto.CourtSimplifiedDto;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CourtController {

    @Autowired
    private CourtDao courtDao;

    @Autowired
    private SurfaceTypeDao surfaceTypeDao;

    @GetMapping("/kurt")
    public List<Court> getCourts() {
        return courtDao.list();
    }

    @PostMapping("/kurt")
    public Court postCourt(@RequestBody CourtSimplifiedDto newCourt) {
        if (newCourt.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court definitions require a defined name");
        }
        SurfaceType surfaceType = surfaceTypeDao.find(newCourt.getSurfaceTypeId());
        if (surfaceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court definition using invalid surface type");
        }

        Court court = new Court(surfaceType, newCourt.getName());
        return courtDao.save(court);
    }

    @PutMapping("/kurt/{id}")
    public Court putCourt(@PathVariable Long id, @RequestBody CourtSimplifiedDto newValues) {
        Court court = courtDao.find(id);
        if (court == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown court");
        }
        if (!court.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Editing deleted court");
        }
        if (newValues.getName() != null) {
            court.setName(newValues.getName());
        }
        if (newValues.getSurfaceTypeId() != null) {
            SurfaceType surfaceType = surfaceTypeDao.find(newValues.getSurfaceTypeId());
            if (surfaceType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid surface type");
            }
            court.setSurfaceType(surfaceType);
        }
        return courtDao.save(court);
    }

    @DeleteMapping("/kurt/{id}")
    public Court deleteCourt(@PathVariable Long id) {
        Court court = courtDao.find(id);
        if (court == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown court");
        }
        if (!court.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already deleted");
        }
        court.setValid(false);
        return courtDao.save(court);
    }
}
