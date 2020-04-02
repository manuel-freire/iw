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

import es.ucm.fdi.iw.constants.ConstantsFromFile;
import es.ucm.fdi.iw.model.Answer;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Question;

public class ContestFileReader {
	
	private static final Logger log = LogManager.getLogger(ContestFileReader.class);
	
	@Autowired
	private static PasswordEncoder passwordEncoder;

	public static Contest readContestFile(String jsonContest) {
		Contest contest = new Contest();
		
		try {
			JSONObject jContest = new JSONObject(jsonContest);
			contest.setName(jContest.getString("nombreConcurso"));
			contest.setEnabled((byte) 0);
			
			JSONArray jQuestionsList = jContest.getJSONArray("preguntas");
			JSONObject jQuestion;

			List<Contest> contestList = new ArrayList<>();
			List<Question> questionList = new ArrayList<>();
			List<Answer> answerList;
			
			JSONArray answerInput;
			String[] answerData;
			Question question;
			Answer answer;			
			
			contestList.add(contest);
			for (int i = 0; i < jQuestionsList.length(); i++) {
				jQuestion = jQuestionsList.getJSONObject(i);
				question = new Question();
				question.setText(jQuestion.getString("enunciado"));
				question.setContest(contestList);
				
				answerList = new ArrayList<>();
				answerInput = jQuestion.getJSONArray("respuestas");
				for(int j = 0; j < answerInput.length(); j++) {
					answerData = answerInput.getString(j).split(ConstantsFromFile.SEPARATOR);
					log.info(answerInput);
					answer = new Answer();
					answer.setQuestion(question);
					answer.setText(answerData[0]);
					answer.setScore(Double.parseDouble(answerData[1]));
					answerList.add(answer);
				}
				
				question.setAnswers(answerList);
				questionList.add(question);
			}

			contest.setQuestions(questionList);
			log.info("Concurso cargado con éxito:\n{}", contest);
			
		} catch (JSONException e) {
			log.warn("Error durante el procesado. Por favor revisa el fichero", e);
		}
		
		return contest;
	}

}
