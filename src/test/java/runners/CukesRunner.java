package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "html:target/cucumber-reports.html",
                "rerun:target/rerun.txt",
                "me.jvt.cucumber.report.PrettyReports:target/cucumber"},
        features = "src/test/resources/features/acceptance_criteria_implementation.feature",
        glue = "step_definitions",
        dryRun = false,
        tags = "@SearchProduct",
        publish = true
)

public class CukesRunner {

}
