package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import javax.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name="Plato")
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int idPlato;
    private String nombre;
    private String descripcion;
    private double precio;
    @OneToMany
    @JoinColumn(name = "idExtra")
    private List<Extra> extras = new ArrayList<>();
}
