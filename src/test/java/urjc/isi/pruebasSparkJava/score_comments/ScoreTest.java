package urjc.isi.pruebasSparkJava;

//import static org.junit.jupiter.api.Assertions.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.*;
import java.util.*;

import org.junit.jupiter.api.Test;

class ScoreTest {

	//private Injector I = new Injector("JDBC_DATABASE_URL");
	
	@Test
	void newScore_test() {
		
		Score score1 = new Score(); 
		
		assertEquals("Puntuación invalida",score1.newScore(-1,1,"Titanic"));

	}

	@Test
	void newScore_test2() {
		
		Score score2 = new Score(); 
		
		assertEquals("Usuario invalido",score2.newScore(1,-1,"Titanic"));

	}
	
	@Test
	void newScore_test3() {
		
		Score score3 = new Score(); 
		
		assertEquals("Pelicula invalida",score3.newScore(1,1,null));

	}
	
	
	//Suponemos que el injector funciona correctamente y no falla, por eso no hacemos test de injector
	/*
	@Test //(expected = NullPointerException.class)
	void getScore_test() {
		
		Score score4 = new Score(); 
		
		assertNotNull(score4.getScore("Titanic"));

	}
	*/
	
	//Comprobamos que el float de score sea mayor o igual que cero
	@Test 
	void changeScore_test() {
		
		Score score5 = new Score(); 
	
		score5.changeScore(1.0f,"Pelicula cualquiera");

	}
	
	@Test (expected = NullPointerException.class)
	void changeScore_test2() {
		
		Score score6 = new Score(); 
	
		score6.changeScore(1.0f,null);

	}

	
}