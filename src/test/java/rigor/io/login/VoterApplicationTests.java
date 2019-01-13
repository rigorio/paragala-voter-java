package rigor.io.login;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}

