package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name="Plato")
public class Plato {
    private String nombre;
    private String descripcion;
    private double precio;
    private List<Extra> extras = new ArrayList<>();
}
