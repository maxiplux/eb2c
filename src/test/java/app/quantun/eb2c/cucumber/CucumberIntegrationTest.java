package app.quantun.eb2c.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"app.quantun.eb2c.cucumber.steps"},
        plugin = {"pretty", "html:target/cucumber-reports"},
        tags = "not @ignore"
)
public class CucumberIntegrationTest {
} 