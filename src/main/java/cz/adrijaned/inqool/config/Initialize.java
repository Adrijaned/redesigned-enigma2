package cz.adrijaned.inqool.config;

import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import cz.adrijaned.inqool.entities.Court;
import cz.adrijaned.inqool.entities.SurfaceType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class Initialize {

    @Bean
    CommandLineRunner initSurfaceType(SurfaceTypeDao surfaceTypeDao, CourtDao courtDao) {
        return (args -> {
            if (Arrays.asList(args).contains("--demo")) {
                SurfaceType a = new SurfaceType(new BigDecimal("1.25"), "Tráva");
                SurfaceType b = new SurfaceType(new BigDecimal("2.0"), "Antuka");
                surfaceTypeDao.save(a);
                surfaceTypeDao.save(b);
                Court c1 = new Court(a, "a1");
                Court c2 = new Court(a, "a2");
                Court c3 = new Court(b, "b1");
                Court c4 = new Court(b, "b2");
                courtDao.save(c1);
                courtDao.save(c2);
                courtDao.save(c3);
                courtDao.save(c4);
            }
        });
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setContextPath("/api");
    }
}
