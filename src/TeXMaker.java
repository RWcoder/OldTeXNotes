import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Makes LaTeX documents for note taking based on user preferences.
 *
 * @author David Thomson
 * @version 1.0
 */
public class TeXMaker {

    private static final TeXOption[] OPTIONS = {
            new TeXOption("No Indent", "\\setlength\\parindent{0pt}", 1),
            new TeXOption("AMS Math", "\\usepackage{amsmath}", 2),
            new TeXOption("Microtype", "\\usepackage{microtype}", 3),
            new TeXOption("Full Page", "\\usepackage{fullpage}", 4)
    };

    /**
     * Makes and compiles the LaTeX file as a PDF for the user's notes.
     *
     * @param fileName      The name of the file to create
     * @param fontSize      The font size of the document
     * @param options       List of options requested by the user
     * @param courseName    The name of the course
     * @param lectureNumber The lecture number
     * @param date          The date on which the notes were taken
     * @param directory     The directory in which to put the file
     */
    public static void makeTeX(String fileName, int fontSize,
            ArrayList<TeXOption> options, String courseName, int lectureNumber,
            LocalDate date, File directory) throws IOException,
            InterruptedException {
        File file = writeFile(
                fileName,
                fontSize,
                options,
                courseName,
                lectureNumber,
                date,
                directory
        );

        ProcessBuilder pb = new ProcessBuilder("pdflatex", file.getName());
        pb.directory(directory);
        pb.start().waitFor();
    }

    /**
     * Writes the LaTeX file to be compiled into notes as a PDF.
     *
     * @param fileName      The name of the file to create
     * @param fontSize      The font size of the document
     * @param options       List of options requested by the user
     * @param courseName    The name of the course
     * @param lectureNumber The lecture number
     * @param date          The date on which the notes were taken
     * @param directory     The directory in which to put the file
     *
     * @return The file written by
     * @throws IOException
     */
    public static File writeFile(String fileName, int fontSize,
            ArrayList<TeXOption> options, String courseName, int lectureNumber,
            LocalDate date, File directory) throws IOException {
        String pathName = directory.getAbsolutePath() + "\\" + fileName + ".tex";
        File file = new File(pathName);
        PrintStream fileStream = new PrintStream(file);

        fileStream.printf("\\documentclass[%dpt]{article}\n", fontSize);
        options.stream().forEach(o -> fileStream.println(o.getCode()));
        fileStream.println("\\begin{document}\n");
        fileStream.println("\\begin{center}");
        fileStream.printf("\\Huge{%s}\n\n", courseName);
        fileStream.println("\\Large{Class Notes}\n");
        fileStream.printf("\\Large{Lecture %d $\\vert$ %d-%d-%d}\n\n",
                lectureNumber, date.getYear(), date.getMonthValue(),
                date.getDayOfMonth());
        fileStream.println("\\end{center}\n");
        fileStream.println("\\end{document}");
        fileStream.close();

        return file;
    }

    /**
     * @return The available options for the TeXMaker
     */
    public static TeXOption[] getOptions() {
        return OPTIONS;
    }
}
