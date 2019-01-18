package rigor.io.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import rigor.io.paragala.voter.nominees.Nominee;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ParsingTest {


  @Test
  public void testParse() throws IOException {
    File file = new File("src/main/resources/results.json");
    List<Nominee> nominees = new ObjectMapper().readValue(file, new TypeReference<List<Nominee>>(){});
    nominees.forEach(System.out::println);
  }
}
