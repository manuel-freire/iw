package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;

	private byte enabled;
	private byte complete;
	private byte checked;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@ManyToOne(fetch = FetchType.EAGER)
	private User teacher;

	@ManyToOne(fetch = FetchType.EAGER)
	private StClass stClass;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany
	@JoinColumn(name = "contest_id")
	private List<Question> questions = new ArrayList<>();
	
	@OneToMany
	@JoinColumn(name = "contest_id")
	private List<Result> results = new ArrayList<>();

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
	
	public byte getChecked() {
		return checked;
	}

	public void setChecked(byte checked) {
		this.checked = checked;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}
	
	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(StClass stClass) {
		this.stClass = stClass;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
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
