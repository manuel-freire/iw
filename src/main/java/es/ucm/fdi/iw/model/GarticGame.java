package es.ucm.fdi.iw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class GarticGame extends MIDIGame {

    // el atributo de lobbyCode debe de ser unique obligatoriamente 
    @Column(unique = true, nullable = false)
    private String lobbyCode;
}
