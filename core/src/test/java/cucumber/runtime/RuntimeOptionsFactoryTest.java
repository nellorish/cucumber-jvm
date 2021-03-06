package cucumber.runtime;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import gherkin.formatter.JSONFormatter;
import gherkin.formatter.PrettyFormatter;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static cucumber.runtime.RuntimeOptionsFactory.packageName;
import static cucumber.runtime.RuntimeOptionsFactory.packagePath;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuntimeOptionsFactoryTest {
    @Test
    public void create_strict() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(Strict.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertTrue(runtimeOptions.isStrict());
    }

    @Test
    public void create_non_strict() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(NotStrict.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertFalse(runtimeOptions.isStrict());
    }

    @Test
    public void create_without_options() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(WithoutOptions.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertFalse(runtimeOptions.isStrict());
        assertEquals(asList("classpath:cucumber/runtime"), runtimeOptions.getFeaturePaths());
        assertEquals(asList("classpath:cucumber/runtime"), runtimeOptions.getGlue());
        assertEquals(1, runtimeOptions.getPlugins().size());
        assertEquals("cucumber.runtime.formatter.NullFormatter", runtimeOptions.getPlugins().get(0).getClass().getName());
    }

    @Test
    public void create_without_options_with_base_class_without_options() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(WithoutOptionsWithBaseClassWithoutOptions.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertEquals(asList("classpath:cucumber/runtime"), runtimeOptions.getFeaturePaths());
        assertEquals(asList("classpath:cucumber/runtime"), runtimeOptions.getGlue());
        assertEquals(1, runtimeOptions.getPlugins().size());
        assertEquals("cucumber.runtime.formatter.NullFormatter", runtimeOptions.getPlugins().get(0).getClass().getName());
    }

    @Test
    public void create_with_no_name() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(NoName.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertTrue(runtimeOptions.getFilters().isEmpty());
    }

    @Test
    public void create_with_multiple_names() throws Exception {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(MultipleNames.class);

        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        List<Object> filters = runtimeOptions.getFilters();
        assertEquals(2, filters.size());
        Iterator<Object> iterator = filters.iterator();
        assertEquals("name1", getRegexpPattern(iterator.next()));
        assertEquals("name2", getRegexpPattern(iterator.next()));
    }

    @Test
    public void create_with_snippets() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(Snippets.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        assertEquals(SnippetType.CAMELCASE, runtimeOptions.getSnippetType());
    }

    private String getRegexpPattern(Object pattern) {
        return ((Pattern) pattern).pattern();
    }

    @Test
    public void finds_path_for_class_in_package() {
        assertEquals("java/lang", packagePath(String.class));
    }

    @Test
    public void finds_path_for_class_in_toplevel_package() {
        assertEquals("", packageName("TopLevelClass"));
    }

    @Test
    public void inherit_plugin_from_baseclass() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(SubClassWithFormatter.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        List<Object> plugins = runtimeOptions.getPlugins();
        assertEquals(2, plugins.size());
        assertTrue(plugins.get(0) instanceof PrettyFormatter);
        assertTrue(plugins.get(1) instanceof JSONFormatter);
    }

    @Test
    public void override_monochrome_flag_from_baseclass() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(SubClassWithMonoChromeTrue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        assertTrue(runtimeOptions.isMonochrome());
    }


    @CucumberOptions(snippets = SnippetType.CAMELCASE)
    static class Snippets {
        // empty
    }

    @CucumberOptions(strict = true)
    static class Strict {
        // empty
    }

    @CucumberOptions
    static class NotStrict {
        // empty
    }

    @CucumberOptions(name = {"name1", "name2"})
    static class MultipleNames {
        // empty
    }

    @CucumberOptions
    static class NoName {
        // empty
    }
    static class WithoutOptions {
        // empty
    }

    static class WithoutOptionsWithBaseClassWithoutOptions extends WithoutOptions {
        // empty
    }

    @CucumberOptions(plugin = "pretty")
    static class SubClassWithFormatter extends BaseClassWithFormatter {
        // empty
    }

    @CucumberOptions(plugin = "json:test-json-report.json")
    static class BaseClassWithFormatter {
        // empty
    }

    @CucumberOptions(monochrome = true)
    static class SubClassWithMonoChromeTrue extends BaseClassWithMonoChromeFalse {
        // empty
    }

    @CucumberOptions(monochrome = false)
    static class BaseClassWithMonoChromeFalse {
        // empty
    }
}
