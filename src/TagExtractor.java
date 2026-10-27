import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TagExtractor extends JFrame {

    private JTextArea textArea;
    private JLabel fileLabel;

    private File textFile;
    private File stopWordFile;

    private Set<String> stopWords;
    private Map<String, Integer> frequencies;

    public TagExtractor() {
        setTitle("Tag/Keyword Extractor");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();

        JButton openTextButton = new JButton("Open Text File");
        JButton openStopButton = new JButton("Open Stop Word File");
        JButton extractButton = new JButton("Extract Tags");
        JButton saveButton = new JButton("Save Results");

        topPanel.add(openTextButton);
        topPanel.add(openStopButton);
        topPanel.add(extractButton);
        topPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);

        fileLabel = new JLabel("No file selected");
        add(fileLabel, BorderLayout.SOUTH);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        openTextButton.addActionListener(e -> chooseTextFile());

        openStopButton.addActionListener(e -> chooseStopWordFile());

        extractButton.addActionListener(e -> extractTags());

        saveButton.addActionListener(e -> saveResults());

        setVisible(true);
    }

    private void chooseTextFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            textFile = chooser.getSelectedFile();
            fileLabel.setText("Text File: " + textFile.getName());
        }
    }

    private void chooseStopWordFile() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            stopWordFile = chooser.getSelectedFile();

            JOptionPane.showMessageDialog(
                    this,
                    "Stop word file selected:\n"
                            + stopWordFile.getName());
        }
    }

    private void extractTags() {

        if (textFile == null || stopWordFile == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select both files first!");
            return;
        }

        try {

            stopWords = loadStopWords(stopWordFile);

            frequencies = new TreeMap<>();

            Scanner input = new Scanner(textFile);

            while (input.hasNext()) {

                String word = input.next();

                word = word.replaceAll("[^a-zA-Z]", "");
                word = word.toLowerCase();

                if (word.isEmpty()) {
                    continue;
                }

                if (stopWords.contains(word)) {
                    continue;
                }

                frequencies.put(
                        word,
                        frequencies.getOrDefault(word, 0) + 1);
            }

            input.close();

            displayResults();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage());
        }
    }

    private Set<String> loadStopWords(File file)
            throws FileNotFoundException {

        Set<String> stopWords = new TreeSet<>();

        Scanner input = new Scanner(file);

        while (input.hasNextLine()) {
            stopWords.add(
                    input.nextLine()
                            .trim()
                            .toLowerCase());
        }

        input.close();

        return stopWords;
    }

    private void displayResults() {

        StringBuilder output = new StringBuilder();

        for (String word : frequencies.keySet()) {

            output.append(
                    String.format(
                            "%-20s %d%n",
                            word,
                            frequencies.get(word)));
        }

        textArea.setText(output.toString());
    }

    private void saveResults() {

        if (frequencies == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nothing to save.");
            return;
        }

        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            File outputFile = chooser.getSelectedFile();

            try {

                PrintWriter writer =
                        new PrintWriter(outputFile);

                for (String word : frequencies.keySet()) {

                    writer.println(
                            word + "," +
                                    frequencies.get(word));
                }

                writer.close();

                JOptionPane.showMessageDialog(
                        this,
                        "Results saved!");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                TagExtractor::new);
    }
}