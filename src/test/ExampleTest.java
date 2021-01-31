import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;

public class ExampleTest {
	@Test
	public void greetReturnsHelloWorld() {
		assertEquals(Example.greet(), "Hello world!");
	}

	@Ignore
	@Test
	public void meowReturnsMeow() {
		assertEquals(Example.meow(), "Meow!");
	}
}
