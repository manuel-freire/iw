package com.example.demo.control;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Car;
import com.example.demo.model.Wheel;

/**
 * Controlador para probar cosas con la BD.
 * 
 * OJO: Sólo para probar cosas. En una aplicación real, debería haber
 * - Control de accesos. No todo el mundo debe poder tocar todo.
 * - Validación de campos. No nos podemos fiar de que los usuarios no intenten
 *   introducir valores "malignos" en la aplicación, y hay que revisarlos tanto
 *   en el cliente (=js en su navegador), por si lo hacen sin querer, como en el 
 *   servidor (=aquí), por si los meten queriendo (y entonces pueden pasar
 *   fácilmente de las validaciones del navegador)
 * - Paginación. Mostrar toda la BD a la vez es caro, lento, e innecesario:
 *   mejor mostrar sólo lo que se necesita en cada momento.
 * 
 * @author mfreire
 */

@Controller
public class TestController {

	@Autowired 
	private EntityManager entityManager;
	
	private static final Logger log = LogManager.getLogger(TestController.class);

	@PostMapping("/addCar1")
	@Transactional 
	public String addCar1(
			@RequestParam String company,
			@RequestParam String model, Model m) {
		Car car = new Car();
		car.setCompany(company);
		car.setModel(model);
		entityManager.persist(car);
	     
	    //Do Something
	    return dump(m);
	}
	
	@PostMapping("/addCar2")
	@Transactional 
	public String addCar2(@Valid @ModelAttribute Car car, 
			BindingResult result,  ModelMap model, Model m) {
		if (result.hasErrors()) {
			log.warn("Validation errors: {}", 
					result.getAllErrors());
	        return "error";
	    }
		entityManager.persist(car);
	     
	    //Do Something
	    return dump(m);
	}
		
	
	@GetMapping("/car")
	public String getCar(@RequestParam long id, Model model ) {
		log.info("Requesting info about car {}",  id);
		model.addAttribute("car", entityManager.find(Car.class, id));
		return "car";
	}
	
	@GetMapping("/conduce1")
	@Transactional
	public String conduce1(@RequestParam long idConductor, @RequestParam long idCoche, Model m) {
		log.info("Now, {} drives {}",  idConductor, idCoche);
		Car car = entityManager.find(Car.class, idCoche);
		com.example.demo.model.Driver driver =  entityManager.find(com.example.demo.model.Driver.class, idConductor);
		car.getDrivers().add(driver);
		return dump(m);
	}
	
	@GetMapping("/conduce2")
	@Transactional
	public String conduce2(@RequestParam long idConductor, @RequestParam long idCoche, Model m) {
		log.info("Now, {} drives {}",  idConductor, idCoche);
		Car car = entityManager.find(Car.class, idCoche);
		com.example.demo.model.Driver driver =  entityManager.find(com.example.demo.model.Driver.class, idConductor);
		driver.getRides().add(car);
		return dump(m);
	}
	

	/**
	 * Añade, modifica o elimina un nuevo objeto del tipo especificado con los campos especificados.
	 * 
	 * Usa introspección para nombre de tabla y campos: esto es sólo para no escribir más
	 * código de la cuenta, y porque no estoy validando el contenido de estos campos. 
	 * Si estuviese validando, las comprobaciones de validación habría que hacerlas 
	 * caso a caso.
	 * 
	 * Es además feo y poco legible: NO USEIS INTROSPECCIÓN SIN MUY BUENA EXCUSA
	 * 
	 * @param model
	 * @return
	 */
	@PostMapping("/")
	@Transactional
	public String mod(Model model, 
			@RequestParam String tableName, 
			@RequestParam(required=false) Long id, 
			HttpServletRequest request) {
			
		if (id >= 0) {
			boolean isNewObject = (id == 0);
			Object o = isNewObject ? 
					newObjectByName(tableName) : 
					existingObjectById(tableName, id);
			
			for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
				if (e.getKey().equals("tableName|id")) {
					continue; // not a valid field name
				}
				setObjectProperty(o, e.getKey(), String.join(",", e.getValue()));		
			}
			if (isNewObject) {
				entityManager.persist(o); // tells the entityManager to actively manage this object
			}
		} else {
			// I am using negative numbers to erase stuff. So id==10 modifies #10, id==-10 deletes it.
			entityManager.remove(existingObjectById(tableName, -id));
		}

		entityManager.flush();    // make the change immediately visible (so 'dump()' can see it)
		return dump(model);
	}

	@GetMapping("/")
	public String dump(Model model) {
		// list of all Objects to scan. 
		for (String tableName : "Car Driver Wheel".split(" ")) {
			// queries all objects
			List<?> results = entityManager.createQuery(
					"select x from " + tableName + " x").getResultList();
			
			// dumps them via log
			log.info("Dumping table {}", tableName);
			for (Object o : results) {
				log.info("\t{}", o);
			}
			
			// adds them to model
			model.addAttribute(tableName, results);
			// adds id-to-text map to model, too
			Map<String, String> idsToText = new HashMap<>();
			for (Object o : results) {
				idsToText.put(getObjectId(o), o.toString());
			}
			model.addAttribute(tableName+"Map", idsToText);
		}
				
		return "dump";
	}
	
	private Object existingObjectById(String className, long id) {
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(className); 
			return entityManager.find(clazz, id);
		} catch (Exception e) {
			log.warn("Error retrieving object of class " + className + " with ID " + id, e);
			return null;
		}
	}
	
	private Object newObjectByName(String className) {
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(className);
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			log.warn("Error instantiating object of class " + className, e);
			return null;
		}
	}
		
	/**
	 * Sets any property of an object.
	 * @param o object to write to
	 * @param propertyName to use. For references, use '_id' at the end. 
	 * @param propertyValue to use. For references, use the id(s) of the object(s) to reference
	 *     Only knows how to handle a few literals. To add more, convert them from String 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setObjectProperty(Object o, String propertyName, String propertyValue) {
		boolean ok = true;
		try {
			Class<?> clazz = o.getClass();
			if ("tableName".equals(propertyName)) {
				return;
			}
			if (propertyName.endsWith("_id")) {
				propertyName = propertyName.substring(
						0, propertyName.length()-"_id".length()); // ignore the trailing '_id'
				Field f = o.getClass().getDeclaredField(propertyName);
				if (List.class.isAssignableFrom(f.getType())) {
					// add a list of references
					Method getter = getAccessor(clazz, true, propertyName);
					Class<?> inner = getter.getAnnotation(OneToMany.class) != null ?
							getter.getAnnotation(OneToMany.class).targetEntity() : 
							getter.getAnnotation(ManyToMany.class).targetEntity();
					List list = (List)getter.invoke(o);
					list.clear(); // remove previous values
					for (String id : propertyValue.split(",")) {
						list.add(entityManager.find(inner, 
								Long.parseLong(id))); 
					}
				} else {
					// set one reference
					Method setter = getAccessor(clazz, false, propertyName);
					setter.invoke(o, entityManager.find(f.getType(), 
							Long.parseLong(propertyValue))); 
				}
			} else {
				// set a literal value
				Method setter = getAccessor(clazz, false, propertyName);
				Class<?> type = setter.getParameters()[0].getType();
				if (type.equals(String.class)) {
					setter.invoke(o, propertyValue);
				} else if (type.isPrimitive()) {
					// rely on Spring - as per https://stackoverflow.com/a/15973019/15472
					PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(o);
					accessor.setPropertyValue(propertyName, propertyValue);
				} else if (type.isEnum()) {
					setter.invoke(o, Wheel.Position.valueOf(propertyValue));
				} else {
					throw new UnsupportedOperationException("do not know how to set a " + type);
				}
			}
		} catch (Exception e) {
			log.warn("Error setting property {} to {} in a {}", propertyName, 
					propertyValue, o.getClass().getSimpleName());
			log.warn("... exception was:",  e);
			ok = false;
		}
		if (ok) {
			log.info("Correctly set property {} to {} in a {}", propertyName, 
				propertyValue, o.getClass().getSimpleName());
		}
	}
	
	private Method getAccessor(Class<?> clazz, boolean read, String propertyName) throws Exception {
		for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
			if (prop.getName().equals(propertyName)) {
				return read ? prop.getReadMethod() : prop.getWriteMethod();
			}
		}
		throw new IllegalArgumentException(
				"No " + (read?"read":"write") + " accessor for " 
						+ propertyName + " in " + clazz.getSimpleName());
	}
	
	private String getObjectId(Object o) {
		try {
			Field f = o.getClass().getDeclaredField("id");
			f.setAccessible(true);
			return ""+f.get(o);
		} catch (Exception e) {
			log.warn("Error retrieving id of class " + o.getClass().getSimpleName(), e);
			return null;
		}
	}
}
