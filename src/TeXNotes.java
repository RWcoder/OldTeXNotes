import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The TeXNotes Application allows the user to set their note taking
 * preferences and then writes and compiles a skeleton LaTeX file for them.
 *
 * @author David Thomson
 * @version 1.0
 */
public class TeXNotes extends Application {
    
    private static final String WELCOME_TEXT = "Welcome to TeXNotes!\n";
    private static final String SUCCESS_TEXT = "Notes created successfully!\n";
    private static final String ERROR_TEXT = "(Error: Nothing was created)\n";

    private static int fontSize;
    private static int parIndent;
    private static ArrayList<TeXOption> options = new ArrayList<TeXOption>();
    private static LocalDate date;
    private static File directory;

    private static TextArea dialog;
    private static TextField courseNameField;
    private static TextField fileNameField;
    private static TextField lectureNumberField;
    private static boolean useLectureNumber;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(initUI());
        stage.setTitle("TeXNotes");
        stage.show();
    }

    /**
     * Initializes the entire TeXNotes UI and puts it into a Scene which can be
     * put into a Stage for display.
     *
     * @return A Scene containing the TeXNotes UI
     */
    private static Scene initUI() {
        dialog = new TextArea(WELCOME_TEXT);
        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(initGridPane());
        borderPane.setRight(initOptions());
        borderPane.setBottom(dialog);

        return new Scene(borderPane);
    }

    /**
     * Initializes the GridPane portion of the TeXNotes UI. This contains the
     * fields for the user to enter information about the notes, such as course
     * name, lecture number, date, directory in which to create the notes, etc.
     *
     * @return A GridPane with the appropriate parts of the UI
     */
    private static GridPane initGridPane() {
        GridPane gp = new GridPane();

        courseNameField = new TextField();
        gp.add(new Label("Course Name"), 0, 0);
        gp.add(courseNameField, 1, 0);

        fileNameField = new TextField();
        gp.add(new Label("File Name"), 0, 1);
        gp.add(fileNameField, 1, 1);

        useLectureNumber = false;
        CheckBox useLectureNumberCheck = new CheckBox("Use lecture number for "
                + "file name");
        useLectureNumberCheck.setOnAction(event -> {
            useLectureNumber = useLectureNumberCheck.isSelected();
        });
        gp.add(useLectureNumberCheck, 0, 2);

        DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(event -> {
            date = datePicker.getValue();
        });
        gp.add(new Label("Date"), 0, 3);
        gp.add(datePicker, 1, 3);

        lectureNumberField = new TextField();
        gp.add(new Label("Lecture Number"), 0, 4);
        gp.add(lectureNumberField, 1, 4);

        ObservableList<Integer> fontSizes = FXCollections.observableArrayList();
        fontSizes.addAll(10, 11, 12);
        ComboBox<Integer> fontSelector = new ComboBox<Integer>(fontSizes);
        fontSelector.setOnAction(event -> {
            fontSize = fontSelector.getValue();
        });
        gp.add(new Label("Font Size"), 0, 5);
        gp.add(fontSelector, 1, 5);

        TextField directoryDisplay = new TextField();
        directoryDisplay.setEditable(false);

        Button chooseDirectory = new Button("Choose directory");
        chooseDirectory.setOnAction(event -> {
            DirectoryChooser dc = new DirectoryChooser();
            directory = dc.showDialog(new Popup());
            directoryDisplay.setText(directory.getAbsolutePath());
        });
        gp.add(chooseDirectory, 0, 6);
        gp.add(directoryDisplay, 1, 6);

        Button makeButton = new Button("Make Notes!");
        makeButton.setOnAction(event ->  {
            try {
                makeNotes();
            } catch (IOException e) {
                dialog.appendText("An exception was thrown");
                dialog.appendText(ERROR_TEXT);
            } catch (InterruptedException e) {
                dialog.appendText("Something went wrong while compiling the "
                        + "document! A valid LaTeX file may exist.");
            }
        });
        gp.add(makeButton, 0, 7);

        return gp;
    }

    /**
     * Initializes the Checkboxes that allow the user to select different
     * options for their notes, such as fullpage and microtype.
     *
     * @return A VBox of the Checkboxes for each option
     */
    private static VBox initOptions() {
        TeXOption[] optionList = TeXMaker.getOptions();
        CheckBox[] checkBoxes = new CheckBox[optionList.length];

        for (int i = 0; i < optionList.length; i++) {
            TeXOption option = optionList[i];
            CheckBox checkBox = new CheckBox(option.getName());

            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    options.add(option);
                } else {
                    options.remove(option);
                }
            });

            checkBoxes[i] = checkBox;
        }

        return new VBox(checkBoxes);
    }

    /**
     * Takes in all the information entered by the user and writes and compiles
     * a TeX file for them using the TeXMaker.
     *
     * @throws IOException If something goes wrong while making the file
     */
    private static void makeNotes() throws IOException, InterruptedException {
        if (courseNameField.getText() == null
                || courseNameField.getText().trim().equals("")) {
            dialog.appendText("Please enter a course name!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        } else if ((fileNameField.getText() == null
                || fileNameField.getText().trim().equals(""))
                && !useLectureNumber) {
            dialog.appendText("Please enter a file name!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        } else if (date == null) {
            dialog.appendText("Please enter a date!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        } else if (lectureNumberField.getText() == null
                || lectureNumberField.getText().trim().equals("")) {
            dialog.appendText("Please enter a lecture number!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        } else if (fontSize == 0) {
            dialog.appendText("Please choose a font size!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        } else if (directory == null) {
            dialog.appendText("Please choose a directory!\n");
            dialog.appendText(ERROR_TEXT);
            return;
        }

        String courseName = courseNameField.getText();

        int lectureNumber;
        try {
            lectureNumber = Integer.parseInt(lectureNumberField.getText());
        } catch (NumberFormatException e) {
            dialog.appendText("Lecture number must be a valid number!");
            return;
        }

        String fileName;
        if (useLectureNumber) {
            fileName = "Lecture_" + lectureNumber;
        } else {
            fileName = fileNameField.getText();
        }

        Collections.sort(options);

        TeXMaker.makeTeX(
                fileName,
                fontSize,
                options,
                courseName,
                lectureNumber,
                date,
                directory
        );

        dialog.appendText(SUCCESS_TEXT);
    }
}
