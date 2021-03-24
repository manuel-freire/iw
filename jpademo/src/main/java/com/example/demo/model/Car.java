package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
