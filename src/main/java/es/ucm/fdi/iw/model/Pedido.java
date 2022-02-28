package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name="Pedido")
public class Pedido {
    public enum Estado{
        PENDIENTE,
        PREPARANDO,
        LISTORECOGIDA,
        REPARTO,
        ENTREGADO
    }
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private int id;
    private String dirEntrega;
    //private String infoPago;
    private Estado estado;
    private double propina;
    private double precioEntrega;
    private double precioServicio;
    private LocalDateTime fechaPedido;
    @OneToMany
    @JoinColumn(name="idPlato")
    //private ArrayList<Plato> contenidoPedido = new ArrayList<>();
    private HashMap<Plato,Integer> contenidoPedido = new HashMap<>(); //<ID Plato, Cantidad>
    @ManyToOne
    private User cliente;
    @ManyToOne
    private Restaurante restaurante;
    @ManyToOne
    private User repartidor;
}
