package cz.adrijaned.inqool;

import cz.adrijaned.inqool.dao.CourtDao;
import cz.adrijaned.inqool.dao.SurfaceTypeDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(args = "--demo")
public class InitTest {

    @Autowired
    SurfaceTypeDao surfaceTypeDao;
    @Autowired
    CourtDao courtDao;

    @Test
    public void testInitialDataPresent() {
        assertEquals(2, surfaceTypeDao.list().size());
        assertEquals(4, courtDao.list().size());
    }
}
