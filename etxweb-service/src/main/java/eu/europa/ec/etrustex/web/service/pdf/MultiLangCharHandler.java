package eu.europa.ec.etrustex.web.service.pdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.font.PDFont;
import rst.pdfbox.layout.elements.Paragraph;

import java.io.IOException;


@Slf4j
public class MultiLangCharHandler {
    private static final String REPLACEMENT_CHARACTER_ENCODING = "\uFFFD";
    private final PDFont[] fonts;

    /**
     *
     * @param fonts Fonts to use to try to find glyphs. The first font must contain the
     *              REPLACEMENT_CHARACTER_ENCODING = "\uFFFD" (E.g. NotoSans-Regular.ttf).
     */
    public MultiLangCharHandler(PDFont... fonts) {
        this.fonts = fonts;
    }

    public void addChar(char character, Paragraph paragraph, int fontSize) throws IOException {
        String str = String.valueOf(character);

        for (int i = 0; i < fonts.length; i++) {
            PDFont font = fonts[i];

            try {
                font.encode(str);
                paragraph.addText(str, fontSize, font);
                break;
            } catch (Exception e) {
                log.debug("Error trying to add character to paragraph using font {}", font);

                if (i == fonts.length - 1) {
                    addReplacementChar(paragraph, fontSize);
                }
            }
        }
    }

    private void addReplacementChar(Paragraph paragraph, int fontSize) throws IOException {
        paragraph.addText(REPLACEMENT_CHARACTER_ENCODING, fontSize, fonts[0]);
    }
}
