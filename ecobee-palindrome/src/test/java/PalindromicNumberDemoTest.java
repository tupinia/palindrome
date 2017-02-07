import junit.framework.TestCase;

public class PalindromicNumberDemoTest extends TestCase {

	/**
	 * Test unit for palindrome...
	 */
	public void testIsPalindrome() {
		
		assertTrue(PalindromicNumberDemo.isPalindrome("0"));
		assertFalse(PalindromicNumberDemo.isPalindrome("123"));
		assertTrue(PalindromicNumberDemo.isPalindrome("100010001"));
		assertFalse(PalindromicNumberDemo.isPalindrome("10001000100"));

		
		assertTrue(PalindromicNumberDemo.isPalindrome("22322"));
		assertFalse(PalindromicNumberDemo.isPalindrome("7777777777777754"));
	}
}
