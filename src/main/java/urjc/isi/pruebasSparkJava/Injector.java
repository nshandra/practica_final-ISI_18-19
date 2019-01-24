package urjc.isi.pruebasSparkJava;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.MultipartConfigElement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URISyntaxException;
import java.net.URI;

public class Injector {
	
	private static Connection c;
	
	public Injector(String name){
		try {
			if(c!=null) return;
				URI dbUri = new URI(System.getenv(name));
				String username = dbUri.getUserInfo().split(":")[0];
				String password = dbUri.getUserInfo().split(":")[1];
				String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
				connection = DriverManager.getConnection(dbUrl, username, password);
				c.setAutoCommit(false);
		}catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}

	public List<String> filtrarPorPelicula(String film) {
		String sql = "SELECT * FROM movies WHERE title = "+'"'+film+'"';
		List<String> result = new ArrayList<String>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		rs.next();
            String titleid = Integer.toString(rs.getInt("titleID"));
            String title = rs.getString("title");
            String year = Integer.toString(rs.getInt("year"));
            String runtimeMinutes = Integer.toString(rs.getInt("runtimeMinutes"));
            String averageRating = Double.toString(rs.getDouble("averageRating"));
            String numVotes = Integer.toString(rs.getInt("numVotes"));
            String genres = rs.getString("genres");
            result.add(titleid);
            result.add(title);
            result.add(year);
            result.add(runtimeMinutes);
            result.add(averageRating);
            result.add(numVotes);
            result.add(genres);
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}

	public List<String> filtrarPorAnyo(String year) {
		String sql = "SELECT * FROM movies WHERE year = "+'"'+year+'"';
		List<String> result = new ArrayList<String>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		while(rs.next()) {
                String title = rs.getString("title");
                result.add(title);
            }
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}

	public List<String> filtrarPorDuracion(Integer minutes) {
		String sql = "SELECT * FROM movies WHERE runtimeMinutes <= "+minutes;
		List<String> result = new ArrayList<String>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		while(rs.next()) {
                String title = rs.getString("title");
                result.add(title);
            }
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}

	public List<String> filtrarPorValoracion(Double rating) {
		String sql = "SELECT * FROM movies WHERE averageRating >= "+rating;
		List<String> result = new ArrayList<String>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		while(rs.next()) {
                String title = rs.getString("title");
                result.add(title);
            }
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}

	public List<Integer>getpuntuaciones(String film) {
		String sql = "SELECT score  FROM ratings JOIN movies ON movies.titleID = ratings.titleID WHERE movies.title LIKE "+'"'+film+'"';
		List<Integer> result = new ArrayList<Integer>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		while(rs.next()) {
                result.add(rs.getInt("score"));
               
            }
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}

	public List<String> filterByActorActress(String name) {
		String sql = "SELECT title FROM movies JOIN works_in ON movies.titleID=works_in.titleID ";
		sql+= "JOIN workers ON workers.nameID=works_in.nameID ";
		sql += "WHERE workers.primaryName LIKE "+'"' + name +'"';
		sql += " and (worksas LIKE 'actor' or worksas LIKE 'actress')";
		List<String> result = new ArrayList<String>();
    	
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		while(rs.next()) {
                String title = rs.getString("title");
                result.add(title);
            }
    	} catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return result;
	}
	
	public void close() {
        try {
            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
	
}
