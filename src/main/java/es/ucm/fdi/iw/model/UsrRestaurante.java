package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class UsrRestaurante extends User{
    @OneToMany
    //@JoinColumn(name="Pedido_id")
    private List<Pedido> pedidos = new ArrayList<>();
}
