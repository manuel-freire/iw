package es.ucm.fdi.iw.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;
    private String texto;
    @ManyToOne
    @JoinColumn(name="User_id")
    private User autor;
    @ManyToOne
    @JoinColumn(name="Plato_id")
    private int idPlato;
}
