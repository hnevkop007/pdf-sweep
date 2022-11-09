package io.monster.profiles.sweep;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public final class ReplaceText {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      usage();
    } else {
      File file = new ReplaceText().getResourceFile("Resume2.pdf");
      var document = PDDocument.load(file);

      if (document.isEncrypted()) {
        System.err.println("Error: Encrypted documents are not supported for this example.");
        System.exit(1);
      }

      var doc1 = replaceTextProbe1(document, args[0]);
      var doc2 = replaceTextProbe2(document, args[0],"");

      var timestamp = Instant.now().toString();
      doc1.save("ResumeReplace-pdfbox-1-" + timestamp + ".pdf");
      doc2.save("ResumeReplace-pdfbox-2-" + timestamp + ".pdf");
    }
  }

  public static PDDocument replaceTextProbe1(PDDocument document, String searchString) throws IOException {
    if (StringUtils.isEmpty(searchString)) {
      return document;
    }

    for (PDPage page : document.getDocumentCatalog().getPages()) {

      PdfContentStreamEditor editor = new PdfContentStreamEditor(document, page) {
        final StringBuilder recentChars = new StringBuilder();
        final List<String> TEXT_SHOWING_OPERATORS = Arrays.asList("Tj", "'", "\"", "TJ");

        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
          String string = font.toUnicode(code);
          if (string != null) {
            recentChars.append(string);
          }

          super.showGlyph(textRenderingMatrix, font, code, displacement);
        }

        protected void write(ContentStreamWriter contentStreamWriter, Operator operator, List<COSBase> operands) throws IOException {
          String recentText = recentChars.toString();
          recentChars.setLength(0);
          String operatorString = operator.getName();

          if (TEXT_SHOWING_OPERATORS.contains(operatorString) && searchString.equals(recentText)) {
            return;
          }

          super.write(contentStreamWriter, operator, operands);
        }
      };
      editor.processPage(page);
    }

    return document;
  }

  public static PDDocument replaceTextProbe2(PDDocument document, String searchString, String replacement) throws IOException {
    for (PDPage page : document.getDocumentCatalog().getPages()) {
      PDFStreamParser parser = new PDFStreamParser(page);
      parser.parse();
      List tokens = parser.getTokens();
      for (int j = 0; j < tokens.size(); j++) {
        Object next = tokens.get(j);

        if (next instanceof Operator) {
          Operator op = (Operator) next;
          //Tj and TJ are the two operators that display strings in a PDF
          if (op.getName().equals("Tj")) {
            // Tj takes one operator and that is the string to display so lets update that operator
            COSString previous = (COSString) tokens.get(j - 1);
            String string = previous.getString();
            string = string.replaceFirst(searchString, replacement);
            previous.setValue(string.getBytes());
          } else if (op.getName().equals("TJ")) {
            COSArray previous = (COSArray) tokens.get(j - 1);
            for (int k = 0; k < previous.size(); k++) {
              Object arrElement = previous.getObject(k);
              if (arrElement instanceof COSString) {
                COSString cosString = (COSString) arrElement;
                String string = cosString.getString();
                string = StringUtils.replaceOnce(string, searchString, replacement);
                cosString.setValue(string.getBytes());
              }
            }
          }
        }
      }
      // now that the tokens are updated we will replace the page content stream.
      PDStream updatedStream = new PDStream(document);
      OutputStream out = updatedStream.createOutputStream();
      ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
      tokenWriter.writeTokens(tokens);
      page.setContents(updatedStream);
      out.close();
    }
    return document;
  }

  private static void usage() {
    System.err.println("Usage: java  <text-to-erase>");
  }

  private File getResourceFile(final String fileName)
  {
    URL url = this.getClass()
        .getClassLoader()
        .getResource(fileName);

    if(url == null) {
      throw new IllegalArgumentException(fileName + " is not found");
    }
    return new File(url.getFile());
  }
}