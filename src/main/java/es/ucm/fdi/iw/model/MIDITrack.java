package es.ucm.fdi.iw.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MIDITrack {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    private MIDISequence sequence;

    private int instrument;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "varchar")
    private List<Note> notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private int pitch;
        private double time;
    }

}
