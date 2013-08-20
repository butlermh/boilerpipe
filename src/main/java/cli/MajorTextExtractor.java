package cli;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * Command line interface for BoilerPipe.
 */
public class MajorTextExtractor {

    @Parameter(names = { "-i", "--input" }, required = true, description = "File or URL input")
    private String fileOrUrlInput;

    @Parameter(names = { "-o", "--output" }, required = false, description = "Output file")
    private String outputFile;

    /**
     * Command line interface.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        MajorTextExtractor task = new MajorTextExtractor();
        JCommander jc = new JCommander(task);
        jc.setProgramName(MajorTextExtractor.class.getSimpleName());
        try {
            jc.parse(args);
            task.apply();
        } catch (Exception e) {
            System.out.println(String.format("[ERROR] %s%n", e.getMessage()));
            jc.usage();
        }
    }

    /**
     * Perform the task.
     *
     * @throws BoilerpipeProcessingException Thrown if there is a problem performing the task.
     */
    public void apply() throws BoilerpipeProcessingException {

        // FIXME - we should not assume the encoding is UTF-8

        String input = loadFile("UTF-8");
        if (input != null) {
            String result = ArticleExtractor.INSTANCE.getText(input);
            if (outputFile == null) {
                System.out.println(result);
            } else {
                try {
                    FileUtils.writeStringToFile(new File(outputFile), result);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.printf("Could not write to %s\n", outputFile);
                }
            }
        }
    }

    /**
     * Retrieve a resource from a file or from a URL.
     *
     * @param encoding The encoding of the resource.
     * @return The resource as a String.
     */
    String loadFile(String encoding) {
        String input = null;
        if (fileOrUrlInput != null) {
            if (fileOrUrlInput.startsWith("http")) {
                try {
                    URL u = new URL(fileOrUrlInput);
                    input = IOUtils.toString(u, encoding);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.printf("Could not resolve %s\n", fileOrUrlInput);
                }
            } else {
                try {
                    input = FileUtils.readFileToString(
                            new File(fileOrUrlInput), encoding);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.printf("Could not load %s\n", fileOrUrlInput);
                    System.exit(1);
                }
            }
        }
        return input;
    }
}
