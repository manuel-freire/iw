package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@Data
@Table(name="IWUser")
public class Cliente extends User{
    private String direccion;
    private String infoPago;
    @OneToMany
    private List<Pedido> pedidos = new ArrayList<>();
}
