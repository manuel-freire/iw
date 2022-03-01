package es.ucm.fdi.iw.model;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Restaurante{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    @OneToOne
    @JoinColumn(name="User_id")
    private User Dueno;
    private String nombre;
    private String descripcion;
    private String horario;
    private String direccion;
    private double valoracion;
    @OneToMany
    @JoinColumn(name="Comentario_id")
    private List<Comentario> comentarios = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="Label_id")
    private List<Label> labels = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="Plato_id")
    private List<Plato> platos = new ArrayList<>();
}