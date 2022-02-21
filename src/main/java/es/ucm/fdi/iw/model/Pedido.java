package es.ucm.fdi.iw.model;

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
    private int idPedido;
    private String dirEntrega;
    private String infoPago;
    private Estado estado;
    private double propina;
    private double precioEntrega;
    private double precioServicio;
    
    @ManyToOne
    private User cliente;
    @ManyToOne
    private Restaurante restaurante;
    @ManyToOne
    private User repartidor;
}
