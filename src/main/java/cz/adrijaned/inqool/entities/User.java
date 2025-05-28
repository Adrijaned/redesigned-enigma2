package cz.adrijaned.inqool.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @Column(unique = true)
    private String phoneNumber;
    @Getter
    @Setter
    private boolean valid = true;

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
