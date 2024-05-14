package jUnitTests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Assert.*;
import spearmint.Login;

public class LoginTest {

	@Test
    public void testValidateCredentialsPass() {
        Login login = new Login();
        boolean result = login.validateCredentials("test", "test");
        assertTrue(result);
    }

    @Test
    public void testValidateCredentialsFail() {
        Login login = new Login();
        boolean result = login.validateCredentials("invalid", "invalid");
        assertFalse(result);
    }

}
