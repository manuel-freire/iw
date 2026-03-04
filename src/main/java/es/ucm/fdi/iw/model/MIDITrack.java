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
public class MIDITrack implements Transferable<MIDITrack.Transfer> {
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

    public MIDITrack(MIDITrack.Transfer transfer, MIDISequence sequence){
        this.id = transfer.getId();
        this.sequence = sequence;
        this.instrument = transfer.getInstrument();
        this.notes = transfer.getNotes();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private int pitch;
        private double time;
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private int instrument;
        private long sequenceId;
        private List<Note> notes;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(this.id, this.instrument, this.sequence.getId(), this.notes);
    }

}
