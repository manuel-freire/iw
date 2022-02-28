package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class Repartidor extends User{
    
    @OneToMany
    @JoinColumn(name="idPedido")
    private List<Pedido> pedidos = new ArrayList<>();
    private double valoracion;
}
