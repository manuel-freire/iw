package es.ucm.fdi.iw.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "idx"}))
public class SongLayer {

  public enum LayerType { DRUMS, BASS, MELODY, FULL }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
  @SequenceGenerator(name = "gen", sequenceName = "gen")
  private long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "song_id")
  private Song song;

  @Column(name = "idx", nullable = false)
  private int idx;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LayerType label;

  @Column(name = "audio_url", nullable = false)
  private String audioUrl;
}