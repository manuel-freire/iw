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

import es.ucm.fdi.iw.model.StClass;
import es.ucm.fdi.iw.model.User;

/**
 * Processes a JSON file and creates a new class
 * @author aitorcay
 */

public class ClassFileReader {
	
	private static final Logger log = LogManager.getLogger(ClassFileReader.class);

	/**
	 * Procesa la información de un fichero JSON y crea una nueva clase
	 * 
	 * @param jsonClass	contenido del fichero JSON
	 * @return			clase creada con la información procesada
	 */
	public static StClass readClassFile(String jsonClass) {
		StClass stClass = new StClass();
		User student;
				
		try {
			JSONObject jClass = new JSONObject(jsonClass);
			stClass.setName(jClass.getString("nombreClase"));
			stClass.setTeamList(new ArrayList<>());
			stClass.setClassContest(new ArrayList<>());
			
			JSONArray jStudentsList = jClass.getJSONArray("alumnos");
			JSONObject jStudent;
			List<User> studentList = new ArrayList<>();
			
			for (int i = 0; i < jStudentsList.length(); i++) {
				jStudent = jStudentsList.getJSONObject(i);
				student= new User();
				
				student.setEnabled((byte) 1);
				student.setRoles("USER");
				student.setFirstName(jStudent.getString("nombre"));
				student.setLastName(jStudent.getString("apellidos"));
				student.setUsername("ST-" + String.format("%03d" , i+1));
				student.setPassword(String.format("%03d" , i+1));
				student.setElo(1000);
				student.setCorrect(0);
				student.setPerfect(0);
				student.setPassed(0);
				student.setStClass(stClass);
				student.setResultList(new ArrayList<>());
				
				log.info("- Estudiante cargado con éxito -\n{}", student);
				studentList.add(student);
			}
			
			stClass.setStudents(studentList);
			
		} catch (JSONException e) {
			log.warn("Error durante el procesado. Por favor revisa el fichero", e);
		}
		
		return stClass;
	}

}
