package ner;

/**
 * This class holds the first name, last name and gender of a person
 * 
 * @author YAn
 *
 */
public class Person {

	private String firstName;
	private String lastName;
	private String gender;
	
	/**
	 * A person can be created with a first name only. 
	 * @param fn
	 */
	public Person(String fn){
		this.firstName = fn;
		this.lastName = null;
		this.gender = null;
	}
	
	//getters and setters
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getGender(){
		return gender;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	
	/**
	 * for display
	 */
	public String toString(){
		String ans = "";
		
		ans += firstName;
		if(lastName != null)
			ans += " " + lastName;
		
		ans += " (" + gender + ")";
		
		return ans;
	}
}
