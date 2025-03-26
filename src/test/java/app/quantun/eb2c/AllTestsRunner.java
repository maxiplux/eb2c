package app.quantun.eb2c;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Test suite runner for all tests in the backend package.
 * This class serves as a test suite runner for executing all tests in the specified package.
 */
@Suite
@SelectPackages("app.quantun.eb2c") // Replace with your project's test package
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AllTestsRunner {
    // This class serves as a test suite runner
}
