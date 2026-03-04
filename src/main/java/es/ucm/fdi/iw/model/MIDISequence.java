package es.ucm.fdi.iw.model;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MIDISequence implements Transferable<MIDISequence.Transfer> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    private MIDIGame game;

    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MIDITrack> tracks;

    public MIDISequence(MIDISequence.Transfer transfer, MIDIGame game) {
        this.id = transfer.getId();
        this.game = game;
        this.tracks = transfer.getTracks().stream().map(trackTransfer -> new MIDITrack(trackTransfer, this))
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private long gameId;
        private List<MIDITrack.Transfer> tracks;
    }

    @Override
    public Transfer toTransfer() {
        List<MIDITrack.Transfer> trackTransfers = this.tracks.stream()
                .map(MIDITrack::toTransfer)
                .collect(Collectors.toList());

        return new Transfer(
                this.id,
                this.game != null ? this.game.getId() : 0L,
                trackTransfers);
    }
}
