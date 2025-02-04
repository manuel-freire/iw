package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String company;
	
	@NotNull
	@Size(max=10)
	private String model;
	
	@ManyToMany(mappedBy="rides")
	private List<Driver> drivers = new ArrayList<>();
	@OneToMany
	@JoinColumn(name="car_id")
	private List<Wheel> wheels = new ArrayList<>();

	@Override
	public String toString() {
		return "Car #" + id;
	}	
}
