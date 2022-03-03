package es.ucm.fdi.iw.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Data
@DiscriminatorValue("RESTAURANTE")
public class UsrRestaurante extends User{
    @OneToMany
    private List<Restaurante> restaurantes = new ArrayList<>();
    @OneToMany
    //@JoinColumn(name="Pedidos_ids")
    @Column(name = "pedidos_restaurante")
    private List<Pedido> pedidos = new ArrayList<>();
}
