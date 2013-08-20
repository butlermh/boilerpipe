package com.oisys.boilerpipe;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CanolaExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import de.l3s.boilerpipe.extractors.KeepEverythingExtractor;
import de.l3s.boilerpipe.extractors.LargestContentExtractor;

@SuppressWarnings("javadoc")
public class AccuracyTest {

	public static final String testCorpus = "src/test/resources/corpus";

	public double extract(String bpextractor) throws Exception {
		File folder = new File(testCorpus);
		File[] folders = folder.listFiles();
		double result = 0;
		for (File f : folders) {
			String html = FileUtils.readFileToString(new File(f, "input.html"));
			String expected = FileUtils.readFileToString(new File(f,
					"output.txt"));
			String actual;

			if (bpextractor.equals("article")) {
				actual = ArticleExtractor.INSTANCE.getText(html);
			} else if (bpextractor.equals("largest")) {
				actual = LargestContentExtractor.INSTANCE.getText(html);
			} else if (bpextractor.equals("everything")) {
				actual = KeepEverythingExtractor.INSTANCE.getText(html);
			} else if (bpextractor.equals("canola")) {
				actual = CanolaExtractor.INSTANCE.getText(html);
			} else {
				actual = DefaultExtractor.INSTANCE.getText(html);
			}

			double cosim = 0;
			if (actual != null && !actual.isEmpty()) {
				cosim = CosineDocumentSimilarity.getCosineSimilarity(expected, actual);
			}
			System.out.printf("%s cosim = %f%n", f, cosim);
			result += cosim;
		}
		return result;
	}
	
	@Test
	public void test01() throws Exception {
		System.out.printf("Article %f%n", extract("article"));
	}
	
	@Test
	public void test02() throws Exception {
		System.out.printf("Canola %f%n", extract("canola"));
	}
	
	@Test
	public void test03() throws Exception {
		System.out.printf("Largest %f%n", extract("largest"));
	}
	
	@Test
	public void test04() throws Exception {
		System.out.printf("Default %f%n", extract("default"));
	}
}
