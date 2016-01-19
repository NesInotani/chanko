package com.github.chanko.template;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.github.mustachejava.MustacheException;

@RunWith(Enclosed.class)
public class EngineTest {
	public static class MainTest {
		private final ByteArrayOutputStream out = new ByteArrayOutputStream();
		private PrintStream stdout;

		@Rule
		public final TemporaryFolder tmp = new TemporaryFolder(
				new File("target"));

		private File template;
		private File properties;
		private File output;

		@Before
		public void setUpFile() throws IOException {
			template = tmp.newFile();
			properties = tmp.newFile();
			output = tmp.newFile();
		}

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
		public void test_success_output_file() throws IOException {

			List<String> templateLines = new ArrayList<>();
			templateLines.add("test template.");
			templateLines.add("replace here -> {{test.message}}");

			List<String> propLines = new ArrayList<>();
			propLines.add("test.message=test ok");

			List<String> expectedLines = new ArrayList<>();
			expectedLines.add(templateLines.get(0));
			expectedLines.add("replace here -> test ok");

			Files.write(template.toPath(), templateLines);
			Files.write(properties.toPath(), propLines);

			Engine engine = new Engine(template.getPath(), properties.getPath(),
					output.getPath());
			engine.fire();

			assertThat(Files.readAllLines(output.toPath()), is(expectedLines));
		}

		@Test
		public void test_success_stdout() throws IOException {

			List<String> templateLines = new ArrayList<>();
			templateLines.add("test template.");
			templateLines.add("replace here -> {{test.message}}");

			List<String> propLines = new ArrayList<>();
			propLines.add("test.message=test ok");

			List<String> expectedLines = new ArrayList<>();
			expectedLines.add(templateLines.get(0));
			expectedLines.add("replace here -> test ok");

			Files.write(template.toPath(), templateLines);
			Files.write(properties.toPath(), propLines);

			Engine engine = new Engine(template.getPath(), properties.getPath(),
					"-");
			engine.fire();

			assertThat(outLines(), is(expectedLines));
		}
	}

	public static class IllegalTest {

		@Rule
		public TemporaryFolder tmp = new TemporaryFolder();
		@Rule
		public ExpectedException thrown = ExpectedException.none();

		private File template;
		private File properties;
		private File output;

		@Before
		public void setUpFile() throws IOException {
			template = tmp.newFile();
			properties = tmp.newFile();
			output = tmp.newFile();
		}

		@Test
		public void test_file_not_root() throws IOException {
			thrown.expect(MustacheException.class);
			thrown.expectMessage("File not under root: "
					+ Paths.get(new File(".").getAbsolutePath()).getParent());

			Engine engine = new Engine(template.getPath(), properties.getPath(),
					output.getPath());
			engine.fire();
		}
	}
}
