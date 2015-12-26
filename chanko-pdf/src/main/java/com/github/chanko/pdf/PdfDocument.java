package com.github.chanko.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences.DUPLEX;

public class PdfDocument implements AutoCloseable {
	private File file;
	private PDDocument document;

	public PdfDocument(String fileName) throws IOException {
		file = new File(fileName);
		if (file.exists()) {
			document = PDDocument.load(file);
		} else {
			document = new PDDocument();
		}
	}

	public void setDuplexPrint() {
		PDDocumentCatalog catalog = document.getDocumentCatalog();
		PDViewerPreferences viewerPrefs = catalog.getViewerPreferences();
		if (viewerPrefs == null) {
			COSDictionary dict = new COSDictionary();
			viewerPrefs = new PDViewerPreferences(dict);
			catalog.setViewerPreferences(viewerPrefs);
		}
		viewerPrefs.setDuplex(DUPLEX.DuplexFlipLongEdge);
	}

	public void save() throws IOException {
		try {
			document.save(file);
		} catch (COSVisitorException e) {
			throw new IOException(e);
		}
	}

	public void saveAs(String fileName, boolean overwrite) throws IOException {
		try {
			File targetFile = new File(fileName);
			if (targetFile.exists() && !overwrite) {
				throw new IOException("can not overwrite");
			}
			document.save(targetFile);
		} catch (COSVisitorException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws Exception {
		document.close();
	}
}
