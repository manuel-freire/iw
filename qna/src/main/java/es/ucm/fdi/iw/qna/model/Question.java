package es.ucm.fdi.iw.qna.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Question {
    private String text;
    private List<Answer> answers = new ArrayList<>();

    public Question(String text, String ... answersAndValues) {
        this.text = text;
        for (String a : answersAndValues) {
            String[] parts = a.split("@");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Respuestas deben tener exactamente 1 @ separando texto y valor");
            }
            answers.add(Answer.builder().text(parts[0]).value(Float.parseFloat(parts[1])).build());
        }
    }

    public float grade(List<Integer> chosen) {
        float total = 0;
        for (int i=0; i<answers.size(); i++) {
            if (chosen.contains(i)) {
                total += answers.get(i).getValue();
            }
        }
        return total;
    }
}