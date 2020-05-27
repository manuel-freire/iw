package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;


/**
 * A class consists of a group of students coordinated by a teacher
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="StClass.byTeacher",
	query="SELECT st FROM StClass st JOIN st.teacher t "
			+ "WHERE t.id = :userId"),
	@NamedQuery(name="StClass.contestOwner",
	query="SELECT st FROM StClass st JOIN st.classContest c "
			+ "WHERE c.id = :contestId"),
	@NamedQuery(name="StClass.contestTeams",
	query="SELECT tl FROM StClass st JOIN st.classContest c JOIN st.teamList tl "
			+ "WHERE c.id = :contestId")
})
public class StClass {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;

	@ManyToOne(fetch = FetchType.EAGER)
	private User teacher;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "st_class_id")
	private List<User> students = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "st_class_id")
	private List<StTeam> teamList = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "st_class_id")
	private List<Contest> classContest = new ArrayList<>();
	
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
	
	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}
	
	public List<User> getStudents() {
		return students;
	}

	public void setStudents(List<User> students) {
		this.students = students;
	}

	public List<StTeam> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<StTeam> teamList) {
		this.teamList = teamList;
	}
	
	public List<Contest> getClassContest() {
		return classContest;
	}

	public void setClassContest(List<Contest> classContest) {
		this.classContest = classContest;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- CLASE ---\n");
		stb.append("Clase: " + this.getName() + "\n");
		stb.append("Profesor/a: " + this.teacher.getUsername() + "\n");
		for (int i = 0; i < this.students.size(); i++) {
			stb.append(this.students.get(i).getUsername() + "\n");
		}
		
	    return stb.toString();
	}
}
