package cucumber.utilities;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)
@CucumberOptions(features="../bank/features/shared", snippets=SnippetType.CAMELCASE, glue= {"cucumber.shared"})
public class SharedCucumberTest {
}
