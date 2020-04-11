package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * A contest includes several questions. A teacher can create several contests
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="Contest.byTeacher",
	query="SELECT c FROM Contest c JOIN c.teacher t "
			+ "WHERE t.id = :userId")
})

public class Contest {
	
	private long id;
	private String name;
	private byte enabled;
	private User teacher;
	private List<Question> questions = new ArrayList<>();
	private List<Result> results = new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getEnabled() {
		return enabled;
	}

	public void setEnabled(byte enabled) {
		this.enabled = enabled;
	}

	@ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "contests")
	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}

	@OneToMany(targetEntity = Question.class)
	@JoinColumn(name = "contest")
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	@OneToMany(targetEntity = Result.class)
	@JoinColumn(name = "contest")
	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- CONCURSO ---\n");
		stb.append("Concurso: " + this.getName() + "\n");
		for (int i = 0; i < this.questions.size(); i++) {
			stb.append("- " + this.questions.get(i).toString() + "\n");
		}
		
	    return stb.toString();
	}
}
