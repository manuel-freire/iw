package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 * A class consist of a group of students coordinated by a teacher
 *
 * @author aitorcay
 */

@Entity
//@NamedQueries({
//	@NamedQuery(name="StClass.byTeacher",
//	query="SELECT stc FROM StClass stc "
//			+ "WHERE stc.teacher = :teacherId")
//})

public class StClass {

	private long id;
	private String className;
//	private User teacher;

	private List<User> students = new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}	
//	
//	@ManyToOne(targetEntity = User.class)
//	@JoinColumn(name = "stClass")
//	public User getTeacher() {
//		return teacher;
//	}
//
//	public User setTeacher() {
//		return teacher;
//	}
//	
	@OneToMany(targetEntity = User.class)
	@JoinColumn(name = "stClass")
	public List<User> getStudents() {
		return students;
	}

	public void setStudents(List<User> students) {
		this.students = students;
	}
}
