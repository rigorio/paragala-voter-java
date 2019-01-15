package rigor.io.login;

import org.junit.Test;
import rigor.io.paragala.voter.details.School;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VoterApplicationTests {

	@Test
	public void contextLoads() {
		String a = "asdf:as:df";
		String[] data = a.split(":");
		System.out.println(data.length);
		System.out.println(data[0]);
		System.out.println(data[1]);
		System.out.println(data.length==2);
	}

	@Test
	public void testList() {
		List<School> schools = new ArrayList<>(Arrays.asList(
				new School(1L, "A"),
				new School(1L, "Ab"),
				new School(1L, "bA")
		));

		String[] sc = schools.stream().map(School::getSchool).collect(Collectors.toList()).toArray(new String[schools.size()]);
		for (int i = 0; i < sc.length; i++) {
			System.out.println(sc[i]);
		};

	}

}

