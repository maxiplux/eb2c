package app.quantun.eb2c.cucumber.steps;

import app.quantun.eb2c.Eb2cApplication;
import app.quantun.eb2c.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = Eb2cApplication.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    // This class can be empty - it just serves as a holder for the annotations
}

