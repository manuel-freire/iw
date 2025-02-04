package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Driver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	
	@ManyToMany
	private List<Car> rides = new ArrayList<Car>();
	
	// -- generated; don't forget to annotate getters for id and relationships
	@Override
	public String toString() {
		return "Driver #" + id;
	}	
}
