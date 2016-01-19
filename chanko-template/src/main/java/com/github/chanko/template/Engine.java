package com.github.chanko.template;

import java.io.IOException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class Engine {
	private Arg arg;

	public Engine(String template, String properties, String output) {
		arg = new Arg(template, properties, output);
	}

	public void fire() throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile(arg.getTemplate());
		m.execute(arg.getOutputAsWriter(), arg.getProperties()).flush();
	}

}
