package app.quantun.eb2c;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CucumberRunnerAdapter {

    @Test
    public void runCucumberTests() {
        // This is just an adapter to allow the CucumberRunner to be discovered by Gradle test task
        // The actual test logic is in the CucumberRunner class
    }
} 