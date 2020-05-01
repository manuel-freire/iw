package es.ucm.fdi.iw.utils;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.iw.model.Achievement;
import es.ucm.fdi.iw.model.Answer;
import es.ucm.fdi.iw.model.Contest;
import es.ucm.fdi.iw.model.Question;
import es.ucm.fdi.iw.model.Result;
import es.ucm.fdi.iw.model.StTeam;
import es.ucm.fdi.iw.model.User;

public class AutoCorrector {	
	
	public static Result correction(User user, StTeam team, Contest contest, List<String> answerList) {
		Result result;
		
		int correct = 0;
		double totalScore = 0;
		boolean passed = false;
		boolean perfect = false;
			
		Answer answer;
		int index;
		double score;
		
		result = new Result();
		result.setContest(contest);
		result.setUser(user);
		result.setAnswers(new ArrayList<>());
		
		for (int i = 0; i < answerList.size(); i++) {
			index = Integer.valueOf(answerList.get(i));
			answer = contest.getQuestions().get(i).getAnswers().get(index);
			result.getAnswers().add(answer);
			
			score = answer.getScore();
			totalScore += score * 10;
			if (score == 1) {
				correct++;
			}
		}
		
		if (totalScore >= answerList.size() * 10 / 2) {
			passed = true;
			if (totalScore >= answerList.size() * 10) {
				perfect = true;
			}
		}
		
		result.setCorrect(correct);
		result.setPassed(passed);
		result.setPerfect(perfect);
		result.setScore(Math.round(totalScore));
		
		if (user.getRoles().equals("USER")) {
			user.setElo(user.getElo() + (int)Math.round(totalScore));
			user.setCorrect(user.getCorrect() + correct);
			user.setPassed(user.getPassed() + 1);
			user.setPerfect(user.getPerfect() + 1);
			team.setElo(team.getElo() + (int)Math.round(totalScore));
			team.setCorrect(team.getCorrect() + correct);
		}
		
		return result;
	}
	
	public static List<Achievement> updateAchievementsUser(List<Achievement> achievements, User user) {
		String[] levels;
		
		for (Achievement a: achievements) {
			levels = a.getGoal().getLevels().split(",");
			
			switch(a.getGoal().getKey()) {
				case("CORRECT"):
					a.setProgress(user.getCorrect());
					if (user.getCorrect() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				case("PASSED"):
					a.setProgress(user.getPassed());
					if (user.getPassed() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				case("PERFECT"):
					a.setProgress(user.getPerfect());
					if (user.getPerfect() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				case("ELO"):
					a.setProgress(user.getElo());
					if (user.getElo() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				default:
					break;
			}
		}
		
		return achievements;
	}
	
	public static List<Achievement> updateAchievementsTeam(List<Achievement> achievements, StTeam team) {
		String[] levels;
		
		for (Achievement a: achievements) {
			levels = a.getGoal().getLevels().split(",");
			
			switch(a.getGoal().getKey()) {
				case("CORRECT"):
					a.setProgress(team.getCorrect());
					if (team.getCorrect() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				case("ELO"):
					a.setProgress(team.getElo());
					if (team.getElo() >= Integer.parseInt(levels[a.getLevel()])) {
						a.setLevel(a.getLevel() + 1);
					}
					break;
				default:
					break;
			}
		}
		
		return achievements;
	}
}
