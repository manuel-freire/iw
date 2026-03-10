package es.ucm.fdi.iw.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MIDIInstrument implements Transferable<MIDIInstrument.Transfer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    private int program;

    private String instrumentName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "varchar")
    private List<Note> notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private int pitch;
        private String label;
        private boolean isBlack;
        private boolean showLabel;
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private int program;
        private String instrumentName;
        private List<Note> notes;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(this.id, this.program, this.instrumentName, this.notes);
    }

}
