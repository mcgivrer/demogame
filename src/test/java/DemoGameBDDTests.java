import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * TDD entry point for the test execution
 *
 * @author Frédéric Delorme
 * @since 2019
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "tests"
        },
        plugin = {
                "html:target/cucumber-html-report",
                "json:target/cucumber.json",
                "pretty:target/cucumber-pretty.txt",
                "usage:target/cucumber-usage.json",
                "junit:target/cucumber-results.xml",
        })
public class DemoGameBDDTests {
}
