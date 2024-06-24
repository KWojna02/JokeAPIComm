package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class JokeAppGUI extends JFrame {
    private final JokeFetcher jokeFetcher;
    private final DatabaseManager dbManager;
    private final JTextArea jokeTextArea;
    private final JCheckBox nsfwCheckBox;
    private final JCheckBox religiousCheckBox;
    private final JCheckBox politicalCheckBox;
    private final JCheckBox racistCheckBox;
    private final JCheckBox sexistCheckBox;
    private final JCheckBox explicitCheckBox;
    private final JTextField deleteJokeTextField;
    private final JLabel requestLabel;
    private final JSpinner jokeCountSpinner;

    public JokeAppGUI() throws SQLException {
        jokeFetcher = new JokeFetcher();
        dbManager = new DatabaseManager();
        jokeTextArea = new JTextArea();
        JButton fetchJokesButton = new JButton("Fetch Jokes");
        JButton clearDatabaseButton = new JButton("Clear Database");
        JButton deleteJokeButton = new JButton("Delete Joke");
        deleteJokeTextField = new JTextField(5);
        requestLabel = new JLabel("Remaining Requests: " + jokeFetcher.getRemainingRequests());

        nsfwCheckBox = new JCheckBox("NSFW");
        religiousCheckBox = new JCheckBox("Religious");
        politicalCheckBox = new JCheckBox("Political");
        racistCheckBox = new JCheckBox("Racist");
        sexistCheckBox = new JCheckBox("Sexist");
        explicitCheckBox = new JCheckBox("Explicit");

        jokeCountSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 120, 1));

        //darkmode
        Color backgroundColor = new Color(43, 43, 43);
        Color foregroundColor = new Color(187, 187, 187);
        Color buttonColor = new Color(60, 63, 65);
        Color panelColor = new Color(50, 50, 50);

        //window settings
        setTitle("Joke Fetcher");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        //window bg color
        getContentPane().setBackground(backgroundColor);

        //gbc
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        //left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBackground(panelColor);
        leftPanel.setForeground(foregroundColor);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel jokeCountLabel = new JLabel("Number of Jokes:");
        jokeCountLabel.setForeground(foregroundColor);
        leftPanel.add(jokeCountLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        jokeCountSpinner.setForeground(foregroundColor);
        jokeCountSpinner.setBackground(buttonColor);
        leftPanel.add(jokeCountSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        fetchJokesButton.setForeground(foregroundColor);
        fetchJokesButton.setBackground(buttonColor);
        leftPanel.add(fetchJokesButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        requestLabel.setForeground(foregroundColor);
        leftPanel.add(requestLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        clearDatabaseButton.setForeground(foregroundColor);
        clearDatabaseButton.setBackground(buttonColor);
        leftPanel.add(clearDatabaseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel deleteJokeLabel = new JLabel("Delete Joke ID:");
        deleteJokeLabel.setForeground(foregroundColor);
        leftPanel.add(deleteJokeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        deleteJokeTextField.setForeground(foregroundColor);
        deleteJokeTextField.setBackground(buttonColor);
        leftPanel.add(deleteJokeTextField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        deleteJokeButton.setForeground(foregroundColor);
        deleteJokeButton.setBackground(buttonColor);
        leftPanel.add(deleteJokeButton, gbc);

        //filters panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 0;

        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        filtersPanel.setBackground(panelColor);
        filtersPanel.setForeground(foregroundColor);
        filtersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(foregroundColor), "Filters", 0, 0, null, foregroundColor));

        nsfwCheckBox.setForeground(foregroundColor);
        nsfwCheckBox.setBackground(panelColor);
        filtersPanel.add(nsfwCheckBox);

        religiousCheckBox.setForeground(foregroundColor);
        religiousCheckBox.setBackground(panelColor);
        filtersPanel.add(religiousCheckBox);

        politicalCheckBox.setForeground(foregroundColor);
        politicalCheckBox.setBackground(panelColor);
        filtersPanel.add(politicalCheckBox);

        racistCheckBox.setForeground(foregroundColor);
        racistCheckBox.setBackground(panelColor);
        filtersPanel.add(racistCheckBox);

        sexistCheckBox.setForeground(foregroundColor);
        sexistCheckBox.setBackground(panelColor);
        filtersPanel.add(sexistCheckBox);

        explicitCheckBox.setForeground(foregroundColor);
        explicitCheckBox.setBackground(panelColor);
        filtersPanel.add(explicitCheckBox);

        //make the border more compact
        filtersPanel.setPreferredSize(new Dimension(200, 200));

        leftPanel.add(filtersPanel, gbc);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        add(leftPanel, gbc);

        jokeTextArea.setForeground(foregroundColor);
        jokeTextArea.setBackground(buttonColor);
        JScrollPane scrollPane = new JScrollPane(jokeTextArea);
        scrollPane.getViewport().setBackground(buttonColor);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.7;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        //onclick
        fetchJokesButton.addActionListener(e -> fetchAndDisplayJokes());
        clearDatabaseButton.addActionListener(e -> clearDatabase());
        deleteJokeButton.addActionListener(e -> deleteJoke());

        //checkbox action listeners for filtering
        nsfwCheckBox.addActionListener(e -> filterAndDisplayJokes());
        religiousCheckBox.addActionListener(e -> filterAndDisplayJokes());
        politicalCheckBox.addActionListener(e -> filterAndDisplayJokes());
        racistCheckBox.addActionListener(e -> filterAndDisplayJokes());
        sexistCheckBox.addActionListener(e -> filterAndDisplayJokes());
        explicitCheckBox.addActionListener(e -> filterAndDisplayJokes());

        //timer to update ui
        Timer uiTimer = new Timer(1000, e -> updateRequestLabel());
        uiTimer.start();

        //load and display existing jokes from the database
        loadAndDisplayExistingJokes();
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    new JokeAppGUI().setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayJokes() {
        int jokeCount = (Integer) jokeCountSpinner.getValue();
        int remainingRequests = jokeFetcher.getRemainingRequests();
        if (jokeCount > remainingRequests) {
            JOptionPane.showMessageDialog(this, "Requested number of jokes exceeds remaining requests (" + remainingRequests + ").", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        jokeTextArea.setText(""); //clear text area
        try {
            jokeFetcher.fetchAndSaveJokes(dbManager, jokeCount);

            List<Joke> jokes = dbManager.getAllJokes();
            displayJokes(jokes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch jokes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateRequestLabel();
    }

    private void filterAndDisplayJokes() {
        try {
            List<Joke> jokes = dbManager.getAllJokes();
            jokes = jokes.stream().filter(this::passesFilter).collect(Collectors.toList());
            displayJokes(jokes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to filter jokes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean passesFilter(Joke joke) {
        if (nsfwCheckBox.isSelected() && !joke.nsfw()) return false;
        if (religiousCheckBox.isSelected() && !joke.religious()) return false;
        if (politicalCheckBox.isSelected() && !joke.political()) return false;
        if (racistCheckBox.isSelected() && !joke.racist()) return false;
        if (sexistCheckBox.isSelected() && !joke.sexist()) return false;
        return !explicitCheckBox.isSelected() || joke.explicit();
    }

    private void displayJokes(List<Joke> jokes) {
        jokeTextArea.setText(""); //clear text area
        for (Joke joke : jokes) {
            jokeTextArea.append("ID: " + joke.id() + "\n");
            jokeTextArea.append(joke.text() + "\n\n");
        }
    }

    private void updateRequestLabel() {
        int remainingRequests = jokeFetcher.getRemainingRequests();
        requestLabel.setText("Remaining Requests: " + remainingRequests);
        ((SpinnerNumberModel) jokeCountSpinner.getModel()).setMaximum(remainingRequests);
    }

    private void loadAndDisplayExistingJokes() {
        try {
            List<Joke> jokes = dbManager.getAllJokes();
            displayJokes(jokes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load existing jokes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDatabase() {
        try {
            dbManager.clearDatabase();
            JOptionPane.showMessageDialog(this, "Database cleared.", "Info", JOptionPane.INFORMATION_MESSAGE);
            loadAndDisplayExistingJokes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to clear database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteJoke() {
        try {
            int jokeId = Integer.parseInt(deleteJokeTextField.getText());
            dbManager.deleteJoke(jokeId);
            JOptionPane.showMessageDialog(this, "Deleted joke with ID: " + jokeId, "Info", JOptionPane.INFORMATION_MESSAGE);
            loadAndDisplayExistingJokes();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid joke ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete joke: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
