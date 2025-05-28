package cz.adrijaned.inqool.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;
    @Getter
    @Setter
    private String name;
    @ManyToOne(optional = false)
    @Getter
    @Setter
    private SurfaceType surfaceType;
    @Getter
    @Setter
    private boolean valid = true;

    public Court(SurfaceType surfaceType, String name) {
        this.surfaceType = surfaceType;
        this.name = name;
    }

}
