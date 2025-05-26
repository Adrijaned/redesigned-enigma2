package cz.adrijaned.inqool.config;

import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class Initialize {

    @Bean
    CommandLineRunner initSurfaceType(SurfaceTypeDao surfaceTypeDao) {
        return (args -> {
            SurfaceType a = new SurfaceType(new BigDecimal("1.25"), "Tráva");
            SurfaceType b = new SurfaceType(new BigDecimal("2.0"), "Antuka");
            surfaceTypeDao.save(a);
            surfaceTypeDao.save(b);
        });
    }
}
