import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String,List<PageEntry>> searchResult = new TreeMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        var files = pdfsDir.listFiles();
        for(var file : files) {
            var doc = new PdfDocument(new PdfReader(file));
            int pagesCount = doc.getNumberOfPages();
            for (int i = 1; i <= pagesCount; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page).toLowerCase();
                var words = text.split("\\P{IsAlphabetic}+");
                for (var word : words) {
                    var count = Arrays.asList(words).stream().filter(o -> o.equals(word)).toList().size();
                    var pageEntry = new PageEntry(file.getName(), i, count);
                    if (searchResult.containsKey(word)) {
                        if (!searchResult.get(word).contains(pageEntry)) {
                            searchResult.get(word).add(pageEntry);
                        }
                    } else {
                        List<PageEntry> wordList = new ArrayList<>();
                        wordList.add(pageEntry);
                        searchResult.put(word, wordList);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        if(searchResult.containsKey(word)){
            return searchResult.get(word).stream().sorted(PageEntry::compareTo).toList();
        }
        return Collections.emptyList();
    }
}
