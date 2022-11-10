package io.monster.profiles.sweep.itext;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class SweepTextApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(SweepTextApplication.class);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (args.length != 1) {
      usage();
    } else {
      String path = new ClassPathResource("ResumeReplace.pdf").getPath();
      var timestamp = Instant.now().toString();
      PdfReader reader = new PdfReader(path);
      PdfWriter writer = new PdfWriter("ResumeReplace-sweep-" + timestamp + ".pdf");
      var document = new PdfDocument(reader, writer);
      new Sweep().sweep(document, args[0]);
      document.close();
    }
  }

  private static void usage() {
    System.err.println("Usage: java  <text-to-erase>");
  }
}
