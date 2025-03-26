package app.quantun.eb2c;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"app.quantun.eb2c.cucumber.steps", "app.quantun.eb2c.config"},
        plugin = {"pretty", "html:target/cucumber-reports"}
)


public class CucumberRunner {
}
