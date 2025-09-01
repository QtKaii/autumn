package gui;

import exceptions.UnrecognizedInstrumentException;
import instruments.Instrument;
import instruments.InstrumentFactory;
import logging.AssignmentLogger;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class InstrumentGUI extends JFrame {
    private JTextField inputField;
    private JButton searchButton;
    private JButton playButton;
    private JButton langButton;
    private JLabel imageLabel;
    private JLabel descriptionLabel;
    private JLabel errorLabel;

    private Locale currentLocale = Locale.ENGLISH;
    private ResourceBundle bundle = ResourceBundle.getBundle("internationalization.MessagesBundle", currentLocale);

    private Instrument currentInstrument = null;

    public InstrumentGUI() {
        super();
        initUI();
        wireActions();
        AssignmentLogger.logConstructor(this);
    }

    private void initUI() {
        AssignmentLogger.logMethodEntry(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setTitle(bundle.getString("gui.title") + " (EN/ES)");

        // Text field centered with Search button to the right
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JPanel inputRow = new JPanel(new BorderLayout(8, 8));
        inputField = new JTextField();
        inputField.setColumns(28);
        inputRow.add(inputField, BorderLayout.CENTER);
        searchButton = new JButton(bundle.getString("gui.searchButton"));
        inputRow.add(searchButton, BorderLayout.EAST);
        topPanel.add(inputRow, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 0, 16));
        add(topPanel, BorderLayout.NORTH);

        // Image with description beneath
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(640, 360));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(imageLabel, BorderLayout.CENTER);

        descriptionLabel = new JLabel(" ");
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(descriptionLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Error label on top, SWitch language and play buttons below
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(errorLabel, BorderLayout.NORTH);

        JPanel buttonsRow = new JPanel(new GridLayout(1, 2, 16, 0));
        playButton = new JButton(bundle.getString("gui.playButton"));
        playButton.setEnabled(false);
        langButton = new JButton(bundle.getString("gui.langButton"));
        buttonsRow.add(playButton);
        buttonsRow.add(langButton);
        bottomPanel.add(buttonsRow, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
        AssignmentLogger.logMethodExit(this);
    }

    private void wireActions() {
        AssignmentLogger.logMethodEntry(this);
        searchButton.addActionListener(_ -> performSearch());
        inputField.addActionListener(_ -> performSearch());
        playButton.addActionListener(_ -> {
            AssignmentLogger.logMethodEntry(this);
            if (currentInstrument != null) {
                currentInstrument.playSound();
            }
            AssignmentLogger.logMethodExit(this);
        });
        langButton.addActionListener(_ -> {
            AssignmentLogger.logMethodEntry(this);
            toggleLanguage();
            AssignmentLogger.logMethodExit(this);
        });
        AssignmentLogger.logMethodExit(this);
    }

    private void performSearch() {
        AssignmentLogger.logMethodEntry(this);
        errorLabel.setText(" ");
        descriptionLabel.setText(" ");
        imageLabel.setIcon(null);
        playButton.setEnabled(false);

        final String text = inputField.getText();
        try {
            Instrument found = InstrumentFactory.fromInput(text, currentLocale);
            currentInstrument = found;
            // Delay showing result by 500ms using SwingWorker
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500);
                    return null;
                }

                @Override
                protected void done() {
                    displayInstrument(currentInstrument);
                }
            };
            worker.execute();
            playButton.setEnabled(true);
        } catch (UnrecognizedInstrumentException ex) {
            logging.AssignmentLogger.logCatchException(ex);
            errorLabel.setText(bundle.getString("error.invalid"));
            currentInstrument = null;
        }
        AssignmentLogger.logMethodExit(this);
    }

    private void displayInstrument(Instrument instrument) {
        AssignmentLogger.logMethodEntry(this);
        if (instrument == null) {
            AssignmentLogger.logMethodExit(this);
            return;
        }
        String key = instrument.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
        String descKey = "instrument." + key + ".desc";
        String desc;
        try {
            desc = bundle.getString(descKey);
        } catch (Exception __ignored) {
            desc = instrument.getDescription();
        }
        descriptionLabel.setText(desc);

        String path = instrument.getImagePath();
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            // scale to fit preferred size while preserving aspect ratio
            int maxW = imageLabel.getWidth() > 0 ? imageLabel.getWidth() : 640;
            int maxH = imageLabel.getHeight() > 0 ? imageLabel.getHeight() : 360;
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            double scale = Math.min((double) maxW / w, (double) maxH / h);
            int nw = (int) Math.max(1, Math.round(w * scale));
            int nh = (int) Math.max(1, Math.round(h * scale));
            Image scaled = icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setIcon(null);
        }
        AssignmentLogger.logMethodExit(this);
    }

    private void toggleLanguage() {
        AssignmentLogger.logMethodEntry(this);
        currentLocale = currentLocale.getLanguage().equals("en") ? Locale.forLanguageTag("es") : Locale.ENGLISH;
        bundle = ResourceBundle.getBundle("internationalization.MessagesBundle", currentLocale);
        updateTexts();
        AssignmentLogger.logMethodExit(this);
    }

    private void updateTexts() {
        AssignmentLogger.logMethodEntry(this);
        setTitle(bundle.getString("gui.title") + " (EN/ES)");
        searchButton.setText(bundle.getString("gui.searchButton"));
        playButton.setText(bundle.getString("gui.playButton"));
        langButton.setText(bundle.getString("gui.langButton"));
        if (currentInstrument != null) {
            // this refreshes language-specific text
            displayInstrument(currentInstrument);
        }
        AssignmentLogger.logMethodExit(this);
    }

    public static void main(String[] args) {
        AssignmentLogger.logMain();
        SwingUtilities.invokeLater(() -> {
            InstrumentGUI gui = new InstrumentGUI();
            gui.setVisible(true);
        });
    }
}
