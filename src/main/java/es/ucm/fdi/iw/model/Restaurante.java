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
    @JoinColumn(name="id")
    private User Dueno;
    private String nombre;
    private String descripcion;
    private String horario;
    private String direccion;
    private double valoracion;
    @OneToMany
    @JoinColumn(name="idComentario")
    private List<Comentario> comentarios = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="idLabel")
    private List<Label> labels = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="idPlato")
    private List<Plato> platos = new ArrayList<>();
}