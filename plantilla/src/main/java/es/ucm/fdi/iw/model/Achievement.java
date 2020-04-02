//package es.ucm.fdi.iw.model;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToMany;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
//
//
///**
// * An achievement is unlocked by a student once a goal is reached
// *
// * @author aitorcay
// */
//
//@Entity
//@NamedQueries({
//	@NamedQuery(name="Achievement.byStudent",
//	query="SELECT a FROM Achievement a "
//			+ "WHERE a.student = :studentId")
//})
//
//public class Achievement {
//	
//	private long id;
//	private String description;
//	private int progress;
//	private int goal;
//	private User student;
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	public long getId() {
//		return id;
//	}
//	
//	public void setId(long id) {
//		this.id = id;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public int getProgress() {
//		return progress;
//	}
//
//	public void setProgress(int progress) {
//		this.progress = progress;
//	}
//
//	public int getGoal() {
//		return goal;
//	}
//
//	public void setGoal(int goal) {
//		this.goal = goal;
//	}
//
//	@ManyToMany(targetEntity = User.class)
//	@JoinColumn(name = "id")
//	public User getStudent() {
//		return student;
//	}
//
//	public void setStudent(User student) {
//		this.student = student;
//	}	
//}
