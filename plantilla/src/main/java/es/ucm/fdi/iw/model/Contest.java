package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * A contest includes several questions. A teacher can create several contests
 *
 * @author aitorcay
 */

@Entity
//@NamedQueries({
//	@NamedQuery(name="Contest.byTeacher",
//	query="SELECT c FROM Contest c "
//			+ "WHERE c.teacher = :teacherId")
//})

public class Contest {
	
	private long id;
	private String name;
//	private User teacher;
//	private List<Question> questions = new ArrayList<>();
	
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

//	@ManyToOne(targetEntity = User.class)
//	@JoinColumn(name = "id")
//	public User getTeacher() {
//		return teacher;
//	}
//
//	public void setTeacher(User teacher) {
//		this.teacher = teacher;
//	}
//
//	@ManyToMany(targetEntity = Question.class)
//	@JoinColumn(name = "id")
//	public List<Question> getQuestions() {
//		return questions;
//	}
//
//	public void setQuestions(List<Question> questions) {
//		this.questions = questions;
//	}
}
