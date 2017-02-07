import junit.framework.TestCase;

public class PalindromicNumberDemoTest extends TestCase {

	public void testIsPalindrome() {
		
		assertTrue(PalindromicNumberDemo.isPalindrome("0"));
		assertFalse(PalindromicNumberDemo.isPalindrome("123"));
		assertTrue(PalindromicNumberDemo.isPalindrome("100010001"));
		assertFalse(PalindromicNumberDemo.isPalindrome("10001000100"));

		assertTrue(PalindromicNumberDemo.isPalindrome("12321"));
		assertFalse(PalindromicNumberDemo.isPalindrome("7777777777777754"));
		
		assertFalse(PalindromicNumberDemo.isPalindrome("77777778877754"));
	}
}
