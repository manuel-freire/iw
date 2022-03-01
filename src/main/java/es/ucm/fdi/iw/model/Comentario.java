package es.ucm.fdi.iw.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    private String texto;
    @ManyToOne
    @JoinColumn(name="User_id")
    private User autor;
    @ManyToOne
    @JoinColumn(name="platoID")
    private int idPlato;
}
