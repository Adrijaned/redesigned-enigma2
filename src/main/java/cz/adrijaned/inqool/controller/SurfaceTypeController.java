package cz.adrijaned.inqool.controller;

import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.dto.SurfaceTypeSimplifiedDto;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class SurfaceTypeController {

    @Autowired
    private SurfaceTypeDao surfaceTypeDao;

    @GetMapping("/povrch")
    public List<SurfaceType> getSurfaceTypes() {
        return surfaceTypeDao.list();
    }

    @PostMapping("/povrch")
    public SurfaceType postSurfaceType(@RequestBody SurfaceTypeSimplifiedDto newSurfaceType){
        if (newSurfaceType.getMinutePrice() == null || newSurfaceType.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Surface type definitions require both a defined name and a price");
        }
        SurfaceType surfaceType = new SurfaceType(newSurfaceType.getMinutePrice(), newSurfaceType.getName());
        return surfaceTypeDao.save(surfaceType);
    }

    @PutMapping("/povrch/{id}")
    public SurfaceType putSurfaceType(@PathVariable Long id, @RequestBody SurfaceTypeSimplifiedDto newValues) {
        SurfaceType surfaceType = surfaceTypeDao.find(id);
        if (surfaceType == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown surface type");
        }
        if (!surfaceType.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Editing deleted surface type");
        }
        if (newValues.getName() != null) {
            surfaceType.setName(newValues.getName());
        }
        if (newValues.getMinutePrice() != null) {
            surfaceType.setMinutePrice(newValues.getMinutePrice());
        }
        return surfaceTypeDao.save(surfaceType);
    }

    @DeleteMapping("/povrch/{id}")
    public SurfaceType deleteSurfaceType(@PathVariable Long id) {
        SurfaceType surfaceType = surfaceTypeDao.find(id);
        if (surfaceType == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown surface type");
        }
        if (!surfaceType.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already deleted");
        }
        surfaceType.setValid(false);
        return surfaceTypeDao.save(surfaceType);
    }
}
