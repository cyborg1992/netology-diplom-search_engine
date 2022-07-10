import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> index = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] listPdfFiles = getListFiles(pdfsDir, ".pdf");
        if (listPdfFiles != null) {
            for (File pdfFile : listPdfFiles) {
                scan(pdfFile);
            }
            index.values().forEach(Collections::sort);
        }
    }

    private void scan(File pdfFile) throws IOException {
        try (var doc = new PdfDocument(new PdfReader(pdfFile))) {
            int numberOfPages = doc.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                PdfPage page = doc.getPage(i);
                String textFromPage = PdfTextExtractor.getTextFromPage(page);
                var words = textFromPage.split("\\P{IsAlphabetic}+");
                Map<String, Integer> wordFrequency = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    wordFrequency.put(word.toLowerCase(),
                            wordFrequency.getOrDefault(word.toLowerCase(), 0) + 1);
                }
                for (var entry : wordFrequency.entrySet()) {
                    PageEntry pageEntry = new PageEntry(pdfFile.getName(), i, entry.getValue());
                    addPageEntryToIndex(index, entry.getKey(), pageEntry);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(@NotNull String word) {
        return index.get(word.toLowerCase());
    }

    private File @Nullable [] getListFiles(@NotNull File pdfsDir, String typeOfFile) {
        File[] tempListFiles = pdfsDir.listFiles();
        if (tempListFiles == null) return null;
        List<File> files = new ArrayList<>();
        for (File file : tempListFiles) {
            if (file.isDirectory()) {
                File[] listFiles = getListFiles(file, typeOfFile);
                if (listFiles != null) files.addAll(List.of(listFiles));
            } else {
                if (file.getName().toLowerCase().endsWith(typeOfFile)) {
                    files.add(file);
                }
            }
        }
        return files.toArray(new File[0]);
    }

    private void addPageEntryToIndex(@NotNull Map<String, List<PageEntry>> index, String word, PageEntry pageEntry) {
        var pageEntryList = index.getOrDefault(word, new ArrayList<>());
        pageEntryList.add(pageEntry);
        index.put(word, pageEntryList);
    }
}
