package es.ucm.fdi.iw.model;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="Restaurante")
public class Restaurante{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int idRestaurante;
    @OneToOne
    @JoinColumn(name="id")
    private User Dueno;
    private String nombre;
    private String descripcion;
    private String horario;
    private String direccion;
    private double valoracion;
    //Poner ArrayList sin vincularlos a una tabla hace que crashee!!!
    //private List<String> comentarios = new ArrayList<>();
    //private List<String> labels = new ArrayList<>();
    @OneToMany
    @JoinColumn(name="idPlato")
    private List<Plato> platos = new ArrayList<>();
}