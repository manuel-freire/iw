package es.ucm.fdi.iw.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.ucm.fdi.iw.constants.ClassFileDTO;
import es.ucm.fdi.iw.control.UserController;
import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.User;

public class ClassFileReader {
	
	private static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired
	private static PasswordEncoder passwordEncoder;

	public static ClassFileDTO readClassFile(String jsonClass) {
		ClassFileDTO classFile = new ClassFileDTO();
		
		try {
			JSONObject jClass = new JSONObject(jsonClass);
			StClass stClass = new StClass();
			stClass.setClassName(jClass.getString("nombreClase"));
			classFile.setStClass(stClass);
			
			log.info("- Clase cargada con éxito -\n {}", stClass);
			
			JSONArray jStudentsList = jClass.getJSONArray("alumnos");
			JSONObject jStudent;
			List<User> studentList = new ArrayList<>();
			User student;
			
			for (int i = 0; i < jStudentsList.length(); i++) {
				jStudent = jStudentsList.getJSONObject(i);
				student= new User();
				
				student.setEnabled((byte) 1);
				student.setRoles("USER");
				student.setFirstName(jStudent.getString("nombre"));
				student.setLastName(jStudent.getString("apellidos"));
				student.setUsername("ST." + String.format("%03d" , jStudent.getInt("n")));
				student.setPassword(String.format("%03d" , jStudent.getInt("n")));
				student.setElo(1000);
				
				log.info("- Estudiante cargado con éxito -\n{}", student);
				studentList.add(student);
			}
			
			classFile.setStudents(studentList);
			
		} catch (JSONException e) {
			log.warn("Error durante el procesado. Por favor revisa el fichero", e);
		}
		
		return classFile;
	}

}
