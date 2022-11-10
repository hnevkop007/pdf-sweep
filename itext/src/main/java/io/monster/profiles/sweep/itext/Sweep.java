package io.monster.profiles.sweep.itext;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfcleanup.PdfCleaner;
import com.itextpdf.pdfcleanup.autosweep.CompositeCleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;
import java.io.IOException;

public class Sweep {
  public void sweep(PdfDocument document, String textToDelete) throws IOException {
    CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
    strategy.add(new RegexBasedCleanupStrategy(textToDelete).setRedactionColor(ColorConstants.WHITE));
    PdfCleaner.autoSweepCleanUp(document, strategy);

    for (IPdfTextLocation location : strategy.getResultantLocations()) {
      PdfPage page = document.getPage(location.getPageNumber() + 1);
      PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), page.getDocument());
      Canvas canvas = new Canvas(pdfCanvas, location.getRectangle());
      canvas.add(new Paragraph("SECRET"));
    }
  }
}
