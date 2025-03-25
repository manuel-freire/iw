package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

import lombok.Data;

/**
 * A group of users, with an associated chat.
 */
@Data
@Entity
@NamedQueries({
  @NamedQuery(name = "Topic.byKey", query = "SELECT t FROM Topic t "
      + "WHERE t.key = :key")
})
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
  @SequenceGenerator(name = "gen", sequenceName = "gen")
  private long id;

  @ManyToMany
  private List<User> members = new ArrayList<>();
  private String name;
  @Column(nullable = false, unique = true, name="topic_key") // key is reserved
  private String key;

  @OneToMany
  @JoinColumn(name = "topic_id")
  private List<Message> messages = new ArrayList<>();

  @Override
  public String toString() {
    return name + " (" + key + ")";
  }
}
