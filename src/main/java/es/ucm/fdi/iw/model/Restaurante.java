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
    private int idDueno;
    private String nombre;
    private String descripcion;
    private String horario;
    private String direccion;
    private double valoracion;
    private List<String> comentarios = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private List<Plato> platos = new ArrayList<>();
}