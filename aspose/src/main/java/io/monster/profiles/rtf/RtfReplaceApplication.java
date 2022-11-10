package io.monster.profiles.rtf;

import java.io.File;
import java.time.Instant;
import java.util.regex.Pattern;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.aspose.words.*;

@SpringBootApplication
public class RtfReplaceApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(RtfReplaceApplication.class);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {

    if (args.length != 1) {
      usage();
    } else {
      Resource resource = new ClassPathResource("Demo_Rtf.rtf");
      File file = resource.getFile();
      Document rtf = new Document(file.getPath());

      FindReplaceOptions options = new FindReplaceOptions();
      rtf.getRange().replace(Pattern.compile(args[0]), "***********", options);

      var timestamp = Instant.now().toString();
      rtf.save("aspose-word-rtf-"+timestamp+".rtf");
    }
  }

  private void usage() {
    System.err.println("Usage: java  <text-to-erase>");
  }

}
