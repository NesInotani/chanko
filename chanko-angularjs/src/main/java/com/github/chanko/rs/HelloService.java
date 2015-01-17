package com.github.chanko.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class HelloService {

	@GET
	@Path("{word}")
	@Produces(MediaType.APPLICATION_JSON)
	public Hello world(@PathParam("word") String word) {
		Hello h = new Hello();
		h.setWord(word);
		return h;
	}

	private class Hello {
		private String word;

		public String getWord() {
			return "hello " + this.word;
		}

		public void setWord(String word) {
			this.word = word;
		}
	}
}
