package es.ucm.fdi.iw.model;

import java.util.Random;

/**
 * Código para generar basura. Todo son métodos estáticos.
 */
public class Lorem {
    
    public static String firstNames[] = {
        // top-100 nombres de nacidos (top-50 chicas y top-50 chicos) en 2002 
        // según https://www.ine.es
        "Alba", "Andrea", "Sara", "Ana",
        "Nerea", "Claudia", "Cristina", "Marina", "Elena", "Irene", "Natalia", "Carla",
        "Carmen", "Nuria", "Ainhoa", "Patricia", "Julia", "Angela", "Rocio", "Sandra",
        "Raquel", "Sofia", "Alicia", "Clara", "Noelia", "Miriam", "Alejandra", "Eva",
        "Isabel", "Silvia", "Celia", "Lorena", "Ines", "Beatriz", "Mireia", "Laia",
        "Lidia", "Carlota", "Blanca", "Ariadna", "Adriana", "Anna", "Carolina",
        "Monica", "Ana Maria", "Veronica",
        "Alejandro", "Pablo", "Daniel", "David", "Adrian", "Javier", "Alvaro", "Sergio",
        "Carlos", "Jorge", "Mario", "Raul", "Diego", "Manuel", "Miguel", "Ivan",
        "Antonio", "Juan", "Ruben", "Victor", "Alberto", "Jesus", "Marc", "Oscar",
        "Angel", "Francisco", "Jose", "Alex", "Marcos", "Jaime", "Ismael", "Luis",
        "Francisco Javier", "Miguel Angel", "Pedro", "Samuel", "Cristian", "Pau",
        "Andres", "Iker", "Jose Antonio", "Guillermo", "Ignacio", "Rafael", "Fernando",
        "Jose Manuel", "Nicolas", "Gonzalo", "Gabriel", "Hugo", "Joel"
    };

    public static String lastNames[] = {
        // top-100 apellidos nacionales, misma fuente
        "Garcia", "Rodriguez", "Gonzalez", "Fernandez", "Lopez", "Martinez", "Sanchez",
        "Perez", "Gomez", "Martin", "Jimenez", "Hernandez", "Ruiz", "Diaz", "Moreno",
        "Muñoz", "Alvarez", "Romero", "Gutierrez", "Alonso", "Navarro", "Torres",
        "Dominguez", "Vazquez", "Ramos", "Ramirez", "Gil", "Serrano", "Molina", "Blanco",
        "Morales", "Suarez", "Ortega", "Castro", "Delgado", "Ortiz", "Marin", "Rubio",
        "Nuñez", "Sanz", "Medina", "Iglesias", "Castillo", "Cortes", "Garrido", "Santos",
        "Guerrero", "Lozano", "Cano", "Mendez", "Cruz", "Prieto", "Flores", "Herrera",
        "Peña", "Leon", "Marquez", "Gallego", "Cabrera", "Calvo", "Vidal", "Campos", "Vega",
        "Reyes", "Fuentes", "Carrasco", "Diez", "Caballero", "Aguilar", "Nieto", "Santana",
        "Pascual", "Herrero", "Montero", "Gimenez", "Hidalgo", "Lorenzo", "Vargas",
        "Ibañez", "Santiago", "Duran", "Benitez", "Ferrer", "Arias", "Mora", "Carmona",
        "Vicente", "Crespo", "Soto", "Roman", "Rojas", "Pastor", "Velasco", "Saez",
        "Parra", "Moya", "Bravo", "Soler", "Gallardo", "Esteban"
    };

    public static String alAzar(String[] posibles) {
        int i = new Random().nextInt(posibles.length);
        return posibles[i];
    }

    public static String nombreAlAzar() {
        return alAzar(firstNames);
    }
    
    public static String apellidoAlAzar() {
        return alAzar(lastNames);
    }
}
