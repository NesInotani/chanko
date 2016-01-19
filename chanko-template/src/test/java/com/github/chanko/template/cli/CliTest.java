package com.github.chanko.template.cli;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class CliTest {
	public static class OptionTest {

		private Cli cli = new Cli();
		private final ByteArrayOutputStream out = new ByteArrayOutputStream();

		@Before
		public void setUpStdout() {
			System.setOut(new PrintStream(out));
		}

		@After
		public void cleanUpStdout() {
			System.setOut(null);
		}

		private List<String> outLines() {
			return Arrays.asList(out.toString().split(System.lineSeparator()));
		}

		@Test
		public void test_printHelp() throws ParseException, IOException {
			String[] args = { "-h", "-t", "template.tpl" };

			assertThat(cli.exec(args), is(0));

			assertThat(outLines().get(0),
					is("usage: tpl [-h] [-o <FILE>] -p <FILE> -t <FILE> [-v]"));
		}

		@Test
		public void test_printVersion() throws ParseException, IOException {
			String[] args = { "-v", "-t", "template.tpl" };

			assertThat(cli.exec(args), is(0));

			assertThat(outLines().get(0), is("tpl 1.0.0"));
		}

		@Test
		public void test_unrecognizedOption()
				throws ParseException, IOException {
			String[] args = { "-a", "-t", "template.tpl" };

			assertThat(cli.exec(args), is(1));

			assertThat(outLines().get(1), is("Unrecognized option: -a"));
		}

		@Test
		public void test_missingOption_p() throws ParseException, IOException {
			String[] args = { "-t", "conf.tpl" };

			assertThat(cli.exec(args), is(1));

			assertThat(outLines().get(1), is("Missing required option: p"));
		}

		@Test
		public void test_missingOption_t() throws ParseException, IOException {
			String[] args = { "-p", "conf.properties" };

			assertThat(cli.exec(args), is(1));

			assertThat(outLines().get(1), is("Missing required option: t"));
		}
	}

	public static class MainTest {
		private Cli cli = new Cli();
		private final ByteArrayOutputStream out = new ByteArrayOutputStream();
		private PrintStream stdout;

		@Rule
		public final TemporaryFolder tmp = new TemporaryFolder(
				new File("target"));

		@Before
		public void setUpStdout() {
			stdout = System.out;
			System.setOut(new PrintStream(out));
		}

		@After
		public void cleanUpStdout() {
			System.setOut(stdout);
		}

		private List<String> outLines() {
			return Arrays.asList(out.toString().split(System.lineSeparator()));
		}

		@Test
		public void test_success() throws IOException {

			File templateFile = tmp.newFile();
			File propFile = tmp.newFile();

			List<String> templateLines = new ArrayList<>();
			templateLines.add("test template.");
			templateLines.add("replace here -> {{test.message}}");

			List<String> propLines = new ArrayList<>();
			propLines.add("test.message=test ok");

			List<String> expectedLines = new ArrayList<>();
			expectedLines.add(templateLines.get(0));
			expectedLines.add("replace here -> test ok");

			Files.write(templateFile.toPath(), templateLines);
			Files.write(propFile.toPath(), propLines);

			String[] args = { "-t", templateFile.getPath(), "-p",
					propFile.getPath() };

			assertThat(cli.exec(args), is(0));

			assertThat(outLines(), is(expectedLines));
		}
	}
}
