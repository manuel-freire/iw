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
	query="SELECT c FROM Contest c JOIN c.teacher t JOIN c.stClass stc "
			+ "WHERE t.id = :userId "
			+ "ORDER BY stc.name DESC"),
	@NamedQuery(name="Contest.byClassTeacher",
	query="SELECT c FROM Contest c JOIN c.stClass stc "
			+ "WHERE stc.id = :classId"),
	@NamedQuery(name="Contest.byClassUser",
	query="SELECT c FROM Contest c JOIN c.stClass stc "
			+ "WHERE stc.id = :classId "
			+ "AND (c.enabled = 1 OR c.complete = 1)"),
	@NamedQuery(name="Contest.byClassComplete",
	query="SELECT c FROM Contest c JOIN c.stClass stc "
			+ "WHERE stc.id = :classId "
			+ "AND c.complete = 1"),
})

public class Contest {
	
	private long id;
	private String name;
	private byte enabled;
	private byte complete;
	private User teacher;
	private StClass stClass;
	
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

	public byte getComplete() {
		return complete;
	}

	public void setComplete(byte complete) {
		this.complete = complete;
	}

	@ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "contestList")
	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}
	
	@ManyToOne(targetEntity = StClass.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "classContest")
	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(StClass stClass) {
		this.stClass = stClass;
	}

	@OneToMany(targetEntity = Question.class)
	@JoinColumn(name = "questionList")
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
