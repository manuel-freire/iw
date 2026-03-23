package es.ucm.fdi.iw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.iw.model.SongLayer.LayerType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GarticGame extends MIDIGame {
    public enum GarticGameStatus {WAITING, PLAYING, FINISHED};

    @ElementCollection
    @CollectionTable(name = "gartic_seq_assignment",
                     joinColumns = @JoinColumn(name = "game_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "seq_id")
    private Map<Long, Long> trackAssignments = new HashMap<>();

    private List<Integer> roundInstruments;

    private int currentRound;

    private int totalRounds;

    private int roundTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GarticGameStatus status;
}
