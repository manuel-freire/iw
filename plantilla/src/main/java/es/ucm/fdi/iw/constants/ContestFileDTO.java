package es.ucm.fdi.iw.constants;

import java.util.List;

import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Question;


public class ContestFileDTO {

	private Contest contest;
	
	private List<Question> questions;

	public Contest getContest() {
		return contest;
	}

	public void setContest(Contest contest) {
		this.contest = contest;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
