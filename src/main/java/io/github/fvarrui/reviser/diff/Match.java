package io.github.fvarrui.reviser.diff;

import java.io.IOException;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

public class Match {

	private ComparedFile file1;
	private ComparedFile file2;
	private double similarity = 0.0;
	private List<AbstractDelta<String>> deltas;

	public Match(ComparedFile file1, ComparedFile file2) throws IOException, DiffException {
		this.file1 = file1;
		this.file2 = file2;
		this.similarity = compare();
	}
	
	private int diffFiles(ComparedFile testFile1, ComparedFile testFile2) throws IOException, DiffException {
		Patch<String> patch = DiffUtils.diff(testFile1.getLines(), testFile2.getLines());
		deltas = patch.getDeltas();
		return countDifferences(deltas);
	}

	private int countDifferences(List<AbstractDelta<String>> deltas) {
		int count = 0;
		for (AbstractDelta<String> delta : deltas) {
			count += delta.getSource().size() + delta.getTarget().size();
		}
		return count;
	}
	
	private double compare() throws IOException, DiffException {
		int differences = diffFiles(file1, file2);
		int file1Lines = file2.getLines().size();
		int file2Lines = file2.getLines().size();
		return 100.0 - ((double)differences / (double)(file1Lines + file2Lines)) * 100.0;
	}
	
	public List<AbstractDelta<String>> getDeltas() {
		return deltas;
	}
	
	public ComparedFile getFile1() {
		return file1;
	}
	
	public ComparedFile getFile2() {
		return file2;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	@Override
	public String toString() {
		return getFile1() + " compared with " + getFile2() + ": files match in a " + String.format("%.2f", getSimilarity()) + "%. ";
	}
	
}
