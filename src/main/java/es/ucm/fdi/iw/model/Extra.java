package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="Extra")
public class Extra {
    private String nombre;
    
}
