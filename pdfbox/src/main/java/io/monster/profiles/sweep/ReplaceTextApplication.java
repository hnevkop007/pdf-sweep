package io.monster.profiles.sweep;

import java.io.File;
import java.time.Instant;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SpringBootApplication
public class ReplaceTextApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ReplaceTextApplication.class);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {

    if (args.length != 1) {
      usage();
    } else {
      Resource resource = new ClassPathResource("Demo_Pdf.pdf");
      File file = resource.getFile();
      var document = PDDocument.load(file);
      if (document.isEncrypted()) {
        System.err.println("Error: Encrypted documents are not supported for this example.");
        System.exit(1);
      }

      var replaceText = new ReplaceText();
      var doc1 = replaceText.replaceTextProbe1(document, args[0]);
      var doc2 = replaceText.replaceTextProbe2(document, args[0], "");

      var timestamp = Instant.now().toString();
      doc1.save("ResumeReplace-pdfbox-1-" + timestamp + ".pdf");
      doc2.save("ResumeReplace-pdfbox-2-" + timestamp + ".pdf");
    }
  }

  private void usage() {
    System.err.println("Usage: java  <text-to-erase>");
  }
}