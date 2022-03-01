package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import javax.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    @OneToMany
    @JoinColumn(name = "Extra_id")
    private List<Extra> extras = new ArrayList<>();
}
