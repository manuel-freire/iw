package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Wheel {
	
	public enum Position { FrontRight, FrontLeft, RearRight, RearLeft };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	private Car car;
	private Position position;

	@Override
	public String toString() {
		return "Wheel #" + id;
	}		
}
