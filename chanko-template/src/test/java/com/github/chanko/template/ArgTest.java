package com.github.chanko.template;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ArgTest {
	public static class NullTest {
		@Rule
		public ExpectedException thrown = ExpectedException.none();

		@Test
		public void test_template_is_null() {
			thrown.expect(NullPointerException.class);
			thrown.expectMessage("template must not be null.");

			new Arg(null, "test.properties", "test.out");
		}

		@Test
		public void test_properties_is_null() {
			thrown.expect(NullPointerException.class);
			thrown.expectMessage("properties must not be null.");

			new Arg("test.tpl", null, "test.out");
		}

		@Test
		public void test_output_is_null() {
			thrown.expect(NullPointerException.class);
			thrown.expectMessage("output must not be null.");

			new Arg("test.tpl", "test.properties", null);
		}
	}

	public static class FileTest {
		@Rule
		public ExpectedException thrown = ExpectedException.none();

		@Rule
		public TemporaryFolder tmp = new TemporaryFolder();

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
		public void test_template_not_exists() {
			thrown.expect(IllegalArgumentException.class);
			thrown.expectMessage("template file does not exists.["
					+ template.getPath() + "]");

			template.delete();
			new Arg(template.getPath(), properties.getPath(), output.getPath());
		}

		@Test
		public void test_properties_not_exists() {
			thrown.expect(IllegalArgumentException.class);
			thrown.expectMessage("properties file does not exists.["
					+ properties.getPath() + "]");

			properties.delete();
			new Arg(template.getPath(), properties.getPath(), output.getPath());
		}

		@Test
		public void test_output_not_writable() {
			thrown.expect(IllegalArgumentException.class);
			thrown.expectMessage("output path does not exists or not writable.["
					+ output.getPath() + "]");

			output.setWritable(false);
			new Arg(template.getPath(), properties.getPath(), output.getPath());
		}
	}

	public static class GetTest {
		@Rule
		public TemporaryFolder tmp = new TemporaryFolder();

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
		public void test_template_get() {
			Arg arg = new Arg(template.getPath(), properties.getPath(),
					output.getPath());

			assertThat(arg.getTemplate(), is(template.getPath()));
		}

		@Test
		public void test_properties_get_as_object() throws IOException {
			Arg arg = new Arg(template.getPath(), properties.getPath(),
					output.getPath());

			assertThat(arg.getProperties(), is(new Properties()));
		}

		@Test
		public void test_output_as_write() throws IOException {
			Arg arg = new Arg(template.getPath(), properties.getPath(),
					output.getPath());

			try (Writer actual = arg.getOutputAsWriter()) {
				actual.write("test");
				actual.flush();

				assertThat(Files.readAllLines(output.toPath()).get(0),
						is("test"));
			}
		}

		@Test
		public void test_output_as_stdout() throws IOException {
			Arg arg = new Arg(template.getPath(), properties.getPath(), "-");

			PrintStream out = System.out;

			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(baos)) {
				System.setOut(ps);
				try (Writer actual = arg.getOutputAsWriter()) {

					actual.write("test");
					actual.flush();

					assertThat(outLines(baos).get(0), is("test"));
				}
			} finally {
				System.setOut(out);
			}
		}

		private List<String> outLines(ByteArrayOutputStream baos) {
			return Arrays.asList(baos.toString().split(System.lineSeparator()));
		}

	}
}
