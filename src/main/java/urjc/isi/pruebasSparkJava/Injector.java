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
	
	public Injector(String name) throws URISyntaxException{
		try {
			URI dbUri = new URI(System.getenv(name));
			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
			c = DriverManager.getConnection(dbUrl, username, password);
			c.setAutoCommit(false);
		}catch (SQLException e) {
            throw new RuntimeException(e);
        }
	}

	public List<String> filterByName(String film) {
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

	public List<String> filterByYear(String year) {
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

	public List<String> filterByDuration(Integer minutes) {
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

	public List<String> filterByRating(Double rating) {
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

	public Integer meanScores(String film) {
		String sql = "SELECT avg(score)  FROM ratings JOIN movies ON movies.titleID = ratings.titleID WHERE movies.title LIKE "+'"'+film+'"' + "GROUP BY ratings.titleID";
    	Integer result = 0;
    	try (PreparedStatement pstmt = c.prepareStatement(sql)) {
    		ResultSet rs = pstmt.executeQuery();
    		c.commit();
    		rs.next();
    		result = rs.getInt("score");
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
	

    public Boolean searchRating(Integer titleID, Integer clientID) {
		String sql = "SELECT score FROM ratings WHERE titleID = "+ titleID;
		sql += " and clientID = "+ clientID;
		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			ResultSet rs= pstmt.executeQuery();
			rs.next();
			rs.getInt("score");
			return true;
		}catch (SQLException e) {
			return false;		
		}
	}
	
	public void insertRating(Integer titleID, Integer clientID, Integer score) {
		String sql= new String();
    	
    	if(searchRating(titleID, clientID)) {
    		sql = "UPDATE ratings SET score=" + score; 
    		sql += " WHERE titleID=" + titleID + " and clientID="+ clientID;
    		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
        		pstmt.executeUpdate();
        		c.commit();
        	} catch (SQLException e) {    		
        		System.out.println(e.getMessage());
        	}
    	}else {
    		sql = "INSERT INTO ratings(titleID, clientID,score) VALUES(?,?,?)";
    		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
        		pstmt.setInt(1, titleID);
        		pstmt.setInt(2, clientID);
        		pstmt.setInt(3, score);
        		pstmt.executeUpdate();
        		c.commit();
        	} catch (SQLException e) {    		
        		System.out.println(e.getMessage());
        	}
    	}

    public Boolean searchUser(Integer clientID) {
		String sql = "SELECT clientID FROM clients WHERE clientID = "+ clientID;
		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
			ResultSet rs= pstmt.executeQuery();
			rs.next();
			rs.getInt("clientID");
			return true;
		}catch (SQLException e) {
			return false;		
		}
	}
	
	public void insertUser(Integer clientid) {
		String sql= new String();
    	
    	if(not searchRating(titleID, clientID)) {
    		sql = "INSERT INTO clients(clientID) VALUES(clientid)";
    		try (PreparedStatement pstmt = c.prepareStatement(sql)) {
        		pstmt.executeUpdate();
        		c.commit();
        	} catch (SQLException e) {    		
        		System.out.println(e.getMessage());
        	}
    	}
    	}

	public void close() {
        try {
            c.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
	
}
