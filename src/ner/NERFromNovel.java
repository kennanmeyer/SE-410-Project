package ner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.github.irobson.jgenderize.GenderizeIoAPI;
import com.github.irobson.jgenderize.client.Genderize;
import com.github.irobson.jgenderize.model.NameGender;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

/**
 * This class defines a method for extracting possible 
 * person information from a text file. It utilizes 
 * the Stanford NER package and jgenderize implementation 
 * for Genderize.io service.
 * 
 * @author YAn
 *
 */
public class NERFromNovel {

	public static ArrayList<Person> extractPerson(String file) {
		
		//location of the classifier model
		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";

		ArrayList<Person> result = new ArrayList<Person>();

		Genderize api = GenderizeIoAPI.create();
		
		try {
			AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier
					.getClassifier(serializedClassifier);

			String fileContents = IOUtils.slurpFile(file);

			List<Triple<String, Integer, Integer>> list = classifier
					.classifyToCharacterOffsets(fileContents);
			
			//used for preventing duplicated names.
			HashSet<String> existingNames = new HashSet<String>();
			
			for (Triple<String, Integer, Integer> item : list) {
				if (item.first().equals("PERSON")) {
					String namestr = fileContents.substring(item.second(),
							item.third());
					
					//remove line breaks and extra spaces between fn and ln
					namestr = namestr.replace("\n", " ").replace("\r", " ")
							.replaceAll("\\s+", " ").trim();
					if(!existingNames.contains(namestr)){
						existingNames.add(namestr);
						String[] names = namestr.split(" ");
						Person p = new Person(names[0]);
						if(names.length > 1)
							p.setLastName(names[1]);
						
						NameGender gender = api.getGender(p.getFirstName());	
						if(gender.getGender() != null)
							p.setGender(gender.getGender());
						else
							p.setGender(randomGenderGuess());
						
						result.add(p);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Simulate a coin toss for guessing gender
	 * @return
	 */
	public static String randomGenderGuess(){
		
		Random rand = new Random();
		
		int toss = rand.nextInt(2);
		
		if(toss == 0)
			return "male";
	
		return "female";
	}
	
	public static void main(String[] args) {

		String file = "pride-and-prejudice.txt";

		ArrayList<Person> pers = NERFromNovel.extractPerson(file);

		for(Person p: pers)
			System.out.println(p);
		
		System.out.println("Total Characters: " + pers.size());
	}

}
