import junit.framework.TestCase;

public class PalindromicNumberDemoTest extends TestCase {

	public void testIsPalindrome() {
		
		assertTrue(PalindromicNumberDemo.isPalindrome("1"));
		assertFalse(PalindromicNumberDemo.isPalindrome("123"));
		assertTrue(PalindromicNumberDemo.isPalindrome("100010001"));
	}
}
