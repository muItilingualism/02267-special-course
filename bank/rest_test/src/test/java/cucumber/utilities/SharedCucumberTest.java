package cucumber.utilities;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="../bank/features/shared", snippets=SnippetType.CAMELCASE, glue= {"cucumber.shared"})
public class SharedCucumberTest {
}
