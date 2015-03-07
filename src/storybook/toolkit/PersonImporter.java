package storybook.toolkit;

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

import ner.Person;

/**
 * The class PersonImporter.
 */
public class PersonImporter {

    /**
     * The classifier model.
     */
    protected static final String CLASSIFIER =
        "classifiers/english.all.3class.distsim.crf.ser.gz";

    /**
     * The filename.
     */
    private String filename;

    /**
     * Instantiate a new PersonImporter.
     *
     * @param filename the filename
     */
    public PersonImporter(String filename) {
        this.filename = filename;
    }

    /**
     * Get the characters.
     *
     * @return an ArrayList of characters
     */
    public ArrayList<Person> getCharacters() {
        ArrayList<Person> people = new ArrayList<Person>();
        Genderize api = GenderizeIoAPI.create();

        AbstractSequenceClassifier<CoreLabel> classifier;
        String fileContents;
        List<Triple<String, Integer, Integer>> list;
        HashSet<String> existingNames;

        try {
            classifier = CRFClassifier.getClassifier(CLASSIFIER);
            fileContents = IOUtils.slurpFile(filename);
            list = classifier.classifyToCharacterOffsets(fileContents);

            existingNames = new HashSet<String>();
            for (Triple<String, Integer, Integer> item : list) {
                if (item.first().equals("PERSON")) {
                    String nameStr = fileContents.substring(item.second(),
                                                            item.third());
                    nameStr = nameStr.replace("\n", " ")
                        .replace("\r", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

                    if (!existingNames.contains(nameStr)) {
                        existingNames.add(nameStr);

                        String[] names = nameStr.split(" ");
                        Person p = new Person(names[0]);
                        if (names.length > 1) {
                            p.setLastName(names[1]);
                        }

                        NameGender gender = api.getGender(p.getFirstName());
                        if (gender.getGender() != null) {
                            p.setGender(gender.getGender());
                        } else {
                            p.setGender(getRandomGender());
                        }

                        people.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return people;
    }

    /**
     * Simulate a coin toss for guessing a gender.
     *
     * @return a random gender
     */
    public String getRandomGender() {
        Random rand = new Random();
        int toss = rand.nextInt(2);

        return toss == 0 ? "male" : "female";
    }
}
