import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String,List<PageEntry>> searchResult = new TreeMap<>();

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
                    List<PageEntry> wordList;
                    if (searchResult.containsKey(word)) {
                        wordList = searchResult.get(word);
                        if (!wordList.contains(pageEntry)) {
                            wordList.add(pageEntry);
                            wordList.sort(PageEntry::compareTo);
                        }
                    } else {
                        wordList = new ArrayList<>();
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
            return searchResult.get(word);
        }
        return Collections.emptyList();
    }
}
