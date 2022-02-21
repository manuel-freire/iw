package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private int idPedido;
    private String dirEntrega;
    private String infoPago;
    private Estado estado;
}
