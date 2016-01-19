package com.github.chanko.template.cli;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.chanko.template.Engine;

public class Cli {
	private static final Logger log = Logger.getLogger(Cli.class.getName());

	private static final String EXEC_NAME;
	private static final String EXEC_VERSION;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		EXEC_NAME = bundle.getString("exec.name");
		EXEC_VERSION = bundle.getString("exec.version");
	}

	public static void main(String[] args) {
		Cli cli = new Cli();
		System.exit(cli.exec(args));
	}

	public int exec(String[] args) {
		log.setLevel(Level.OFF); // for debug

		Options options = new Options();

		Option help = Option.builder("h").longOpt("help")
				.desc("print this message").build();
		Option version = Option.builder("v").longOpt("version")
				.desc("print version").build();
		Option output = Option.builder("o").longOpt("output").hasArg()
				.argName("FILE").desc("output file path").build();
		Option template = Option.builder("t").longOpt("template").hasArg()
				.argName("FILE").desc("template file path").build();
		Option properties = Option.builder("p").longOpt("properties").hasArg()
				.argName("FILE").desc("properties file path").build();

		options.addOption(help);
		options.addOption(version);
		options.addOption(template);
		options.addOption(properties);
		options.addOption(output);

		CommandLine cmd = parseUnsetRequires(options, args);

		// set the required for usage.
		template.setRequired(true);
		properties.setRequired(true);

		if (cmd != null) {
			boolean hasHelp = cmd.hasOption(help.getOpt());
			boolean hasVersion = cmd.hasOption(version.getOpt());

			if (hasHelp) {
				usage(options);
				return 0;
			}

			if (hasVersion) {
				System.out.println(EXEC_NAME + " " + EXEC_VERSION);
				return 0;
			}
		}

		// reset the required option for parse..
		// if you do not reset, required option ignored.
		options.addOption(template);
		options.addOption(properties);

		try {
			DefaultParser parser = new DefaultParser();
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			usage(options, e.getMessage());
			return 1;
		}

		try {
			Engine engine = new Engine(cmd.getOptionValue(template.getOpt()),
					cmd.getOptionValue(properties.getOpt()),
					cmd.getOptionValue(output.getOpt(), "-"));

			engine.fire();
		} catch (Throwable t) {
			log.log(Level.SEVERE, t.getMessage(), t);
			System.err.println(t.getMessage());
			return 1;
		}

		return 0;
	}

	private CommandLine parseUnsetRequires(Options options, String[] args) {
		DefaultParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return cmd;
	}

	private void usage(Options options) {
		usage(options, "", "");
	}

	private void usage(Options options, String header) {
		usage(options, header, "");
	}

	private void usage(Options options, String header, String footer) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(EXEC_NAME, header, options, footer, true);
	}
}
