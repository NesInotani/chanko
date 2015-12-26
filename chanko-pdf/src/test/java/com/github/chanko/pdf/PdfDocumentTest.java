package com.github.chanko.pdf;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class PdfDocumentTest {

	@Test
	public void test_duplex() throws Exception {
		String srcFileName = "yahoo.pdf";
		String dstFileName = "dup_yahoo.pdf";
		File file = new File(dstFileName);
		if (file.exists()) {
			file.delete();
		}

		try (PdfDocument pdf = new PdfDocument(srcFileName)) {
			pdf.setDuplexPrint();
			pdf.saveAs(dstFileName, true);
		}

		assertThat(file.exists(), is(true));
	}

	@Test
	public void test_nonduplex() throws Exception {
		String srcFileName = "yahoo.pdf";
		String dstFileName = "nondup_yahoo.pdf";
		File file = new File(dstFileName);
		if (file.exists()) {
			file.delete();
		}

		try (PdfDocument pdf = new PdfDocument(srcFileName)) {
			pdf.saveAs(dstFileName, true);
		}

		assertThat(file.exists(), is(true));
	}

}
