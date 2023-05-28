import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> content = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // в конструкторе читаем все pdf файлы и сохраняем данные по каждому слову,
        // тк во время поиска сервер не должен уже читать файлы
        for (File pdf : requireNonNull(pdfsDir.listFiles())) {
            var doc = new PdfDocument(new PdfReader(pdf));
            int countPages = doc.getNumberOfPages();
            for (int i = 1; i <= countPages; i++) {
                var page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> fregs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    fregs.put(word, fregs.getOrDefault(word, 0) + 1);
                }
                for (String word : fregs.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, fregs.get(word));
                    if (content.containsKey(word)) {
                        content.get(word).add(pageEntry);
                    } else {
                        content.put(word, new ArrayList<>());
                        content.get(word).add(pageEntry);
                    }
                }
            }
        }
        for (String newWord : content.keySet()) {
            Collections.sort(content.get(newWord));
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result;
        if (content.get(word) == null) {
            result = Collections.emptyList();
        } else {
            result = content.get(word);
        }
        return result;
    }
}
