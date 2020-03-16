package es.ucm.fdi.iw.constants;

import java.util.List;

import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.User;

public class ClassFileDTO {

	private StClass stClass;
	
	private List<User> students;

	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(StClass stClass) {
		this.stClass = stClass;
	}

	public List<User> getStudents() {
		return students;
	}

	public void setStudents(List<User> students) {
		this.students = students;
	}
}
