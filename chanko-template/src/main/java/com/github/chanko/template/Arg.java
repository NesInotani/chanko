package com.github.chanko.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

class Arg {
	private String template;
	private String properties;
	private String output;

	private Path templatePath;
	private Path propertiesPath;
	private Path outputPath;

	Arg(String template, String properties, String output) {
		this.template = template;
		this.properties = properties;
		this.output = output;

		validate();
	}

	private void validate() {
		if (template == null) {
			throw new NullPointerException("template must not be null.");
		}
		if (properties == null) {
			throw new NullPointerException("properties must not be null.");
		}
		if (output == null) {
			throw new NullPointerException("output must not be null.");
		}

		templatePath = Paths.get(template);
		if (!Files.isRegularFile(templatePath)) {
			throw new IllegalArgumentException(
					"template file does not exists.[" + templatePath + "]");
		}

		propertiesPath = Paths.get(properties);
		if (!Files.isRegularFile(propertiesPath)) {
			throw new IllegalArgumentException(
					"properties file does not exists.[" + propertiesPath + "]");
		}

		if (Objects.equals(output, "-")) {
			return;
		}

		outputPath = Paths.get(output);
		if (Files.exists(outputPath) && !Files.isWritable(outputPath)) {
			throw new IllegalArgumentException(
					"output path does not exists or not writable.[" + outputPath
							+ "]");
		}
	}

	String getTemplate() {
		return template;
	}

	Properties getProperties() throws IOException {
		Properties p = new Properties();
		try (InputStream in = Files.newInputStream(propertiesPath)) {
			p.load(in);
		}

		return p;

	}

	Writer getOutputAsWriter() throws IOException {
		if (Objects.equals(output, "-")) {
			return new PrintWriter(System.out);
		}
		return new PrintWriter(Files.newOutputStream(outputPath));
	}

}
