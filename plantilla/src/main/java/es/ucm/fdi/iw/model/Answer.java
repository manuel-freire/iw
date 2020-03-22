package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;


/**
 * A question can be included in several contest. It can be answered with different options. Each option has
 * an associated score.
 *
 * @author aitorcay
 */

@Entity
//@NamedQueries({
//	@NamedQuery(name="Question.byContest",
//	query="SELECT q FROM Question q "
//			+ "WHERE q.contest = :contestId")
//})

public class Answer {
	
	private long id;
	private String text;
	private double score;
	private Question question;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	@ManyToOne(targetEntity = Question.class)
	@JoinColumn(name = "answers")
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		
		stb.append( this.text + " | " + Double.toString(this.score) + " ptos\n");
		
	    return stb.toString();
	}
}
