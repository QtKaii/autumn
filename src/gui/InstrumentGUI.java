package gui;

import exceptions.UnrecognizedInstrumentException;
import instruments.Instrument;
import instruments.InstrumentFactory;
import logging.AssignmentLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.border.Border;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Swing-based user interface for searching instruments (EN/ES), previewing an
 * image and description, and playing the associated sound. Text content comes
 * from ResourceBundles to support runtime language switching.
 */
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

    // UI affordances
    private String placeholderText;
    private boolean usingPlaceholder = false;
    private Color normalInputColor;
    private Border defaultInputBorder;

    /**
     * Builds the frame and wires listeners. Heavy work (loading image) is
     * deferred using a short {@code SwingWorker} to keep the EDT responsive.
     */
    public InstrumentGUI() {
        super();
        initUI();
        wireActions();
        AssignmentLogger.logConstructor(this);
    }

    /**
     * Creates and arranges all Swing components.
     */
    private void initUI() {
        AssignmentLogger.logMethodEntry(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        setTitle(bundle.getString("gui.title") + " " + bundle.getString("gui.titleSuffix"));

        // Text field centered with Search button to the right
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JPanel inputRow = new JPanel(new BorderLayout(8, 8));
        inputField = new JTextField();
        inputField.setColumns(28);
        // Placeholder setup
        normalInputColor = inputField.getForeground();
        defaultInputBorder = inputField.getBorder();
        placeholderText = computePlaceholder();
        activatePlaceholder();
        // Toggle placeholder on focus
        inputField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                AssignmentLogger.logMethodEntry(this);
                if (usingPlaceholder) {
                    inputField.setText("");
                    inputField.setForeground(normalInputColor);
                    usingPlaceholder = false;
                }
                AssignmentLogger.logMethodExit(this);
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                AssignmentLogger.logMethodEntry(this);
                if (inputField.getText().trim().isEmpty()) {
                    activatePlaceholder();
                }
                AssignmentLogger.logMethodExit(this);
            }
        });
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

        // Error label on top, Switch language and play buttons below
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

    /**
     * Hooks up event handlers for buttons and the text field.
     */
    private void wireActions() {
        AssignmentLogger.logMethodEntry(this);
        ActionListener searchAction = new SearchAction();
        searchButton.addActionListener(searchAction);
        inputField.addActionListener(searchAction);
        playButton.addActionListener(new PlayAction());
        langButton.addActionListener(new ToggleLanguageAction());
        AssignmentLogger.logMethodExit(this);
    }

    /**
     * Reads user input, resolves it to a known instrument (via
     * {@link InstrumentFactory}), then delays 500ms before updating the UI to
     * simulate work and avoid abrupt transitions.
     */
    private void performSearch() {
        AssignmentLogger.logMethodEntry(this);
        clearError();
        descriptionLabel.setText(" ");
        imageLabel.setIcon(null);
        playButton.setEnabled(false);

        final String text = inputField.getText().trim();
        if (usingPlaceholder || text.isEmpty()) {
            showError(bundle.getString("error.invalid"));
            AssignmentLogger.logMethodExit(this);
            return;
        }
        setBusy(true);
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
                    setBusy(false);
                }
            };
            worker.execute();
            playButton.setEnabled(true);
        } catch (UnrecognizedInstrumentException ex) {
            logging.AssignmentLogger.logCatchException(ex);
            showError(bundle.getString("error.invalid"));
            currentInstrument = null;
            setBusy(false);
        }
        AssignmentLogger.logMethodExit(this);
    }

    /**
     * Updates the image and localized description for the selected instrument.
     * Scaling preserves aspect ratio and fits the available preview area.
     */
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
        java.io.File imgFile = new java.io.File(path);
        if (!imgFile.exists()) {
            showError(bundle.getString("error.imageMissing"));
            AssignmentLogger.logMethodExit(this);
            return;
        }
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

    /**
     * Toggles between English and Spanish and refreshes all UI labels.
     */
    private void toggleLanguage() {
        AssignmentLogger.logMethodEntry(this);
        currentLocale = currentLocale.getLanguage().equals("en") ? Locale.forLanguageTag("es") : Locale.ENGLISH;
        bundle = ResourceBundle.getBundle("internationalization.MessagesBundle", currentLocale);
        updateTexts();
        AssignmentLogger.logMethodExit(this);
    }

    /**
     * Reloads all visible text from the current ResourceBundle.
     */
    private void updateTexts() {
        AssignmentLogger.logMethodEntry(this);
        setTitle(bundle.getString("gui.title") + " " + bundle.getString("gui.titleSuffix"));
        searchButton.setText(bundle.getString("gui.searchButton"));
        playButton.setText(bundle.getString("gui.playButton"));
        langButton.setText(bundle.getString("gui.langButton"));
        // Refresh placeholder text to current language
        placeholderText = computePlaceholder();
        if (usingPlaceholder || inputField.getText().trim().isEmpty()) {
            activatePlaceholder();
        }
        if (currentInstrument != null) {
            // this refreshes language-specific text
            displayInstrument(currentInstrument);
        }
        AssignmentLogger.logMethodExit(this);
    }

    // ---------- UI helpers (with logging) ----------
    private String computePlaceholder() {
        AssignmentLogger.logMethodEntry(this);
        String s = bundle.getString("gui.inputLabel");
        if (s != null && s.endsWith(":")) s = s.substring(0, s.length() - 1);
        AssignmentLogger.logMethodExit(this);
        return s;
    }

    private void activatePlaceholder() {
        AssignmentLogger.logMethodEntry(this);
        usingPlaceholder = true;
        inputField.setForeground(Color.GRAY);
        inputField.setText(placeholderText);
        AssignmentLogger.logMethodExit(this);
    }

    private void setBusy(boolean busy) {
        AssignmentLogger.logMethodEntry(this);
        searchButton.setEnabled(!busy);
        inputField.setEnabled(!busy);
        langButton.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        AssignmentLogger.logMethodExit(this);
    }

    private void showError(String message) {
        AssignmentLogger.logMethodEntry(this);
        errorLabel.setText(message);
        inputField.setToolTipText(message);
        inputField.setBorder(BorderFactory.createLineBorder(Color.RED));
        Toolkit.getDefaultToolkit().beep();
        AssignmentLogger.logMethodExit(this);
    }

    private void clearError() {
        AssignmentLogger.logMethodEntry(this);
        errorLabel.setText(" ");
        inputField.setToolTipText(null);
        if (defaultInputBorder != null) inputField.setBorder(defaultInputBorder);
        AssignmentLogger.logMethodExit(this);
    }

    // ---------- Inner listener classes ----------
    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AssignmentLogger.logMethodEntry(this);
            performSearch();
            AssignmentLogger.logMethodExit(this);
        }
    }

    private class PlayAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AssignmentLogger.logMethodEntry(this);
            clearError();
            if (currentInstrument == null) {
                showError(bundle.getString("error.invalid"));
                AssignmentLogger.logMethodExit(this);
                return;
            }
            if (!isAudioOutputAvailable()) {
                showError(bundle.getString("error.noAudio"));
                AssignmentLogger.logMethodExit(this);
                return;
            }
            if (!hasSoundFilesFor(currentInstrument)) {
                showError(bundle.getString("error.soundsMissing"));
                AssignmentLogger.logMethodExit(this);
                return;
            }
            currentInstrument.playSound();
            AssignmentLogger.logMethodExit(this);
        }
    }

    private class ToggleLanguageAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AssignmentLogger.logMethodEntry(this);
            toggleLanguage();
            AssignmentLogger.logMethodExit(this);
        }
    }

    // ---------- Environment checks ----------
    private boolean isAudioOutputAvailable() {
        AssignmentLogger.logMethodEntry(this);
        try {
            Clip c = AudioSystem.getClip();
            c.close();
            AssignmentLogger.logMethodExit(this);
            return true;
        } catch (LineUnavailableException | IllegalArgumentException ex) {
            logging.AssignmentLogger.logCatchException(ex);
            AssignmentLogger.logMethodExit(this);
            return false;
        }
    }

    private boolean hasSoundFilesFor(Instrument instrument) {
        AssignmentLogger.logMethodEntry(this);
        // soundPath is like "resources/sounds/Guitar"; take as stem
        String stem = instrument.getSoundPath();
        java.io.File dir = new java.io.File("resources/sounds");
        String prefix;
        if (stem != null && stem.contains("/")) {
            prefix = stem.substring(stem.lastIndexOf('/') + 1);
        } else if (stem != null && stem.contains("\\")) {
            prefix = stem.substring(stem.lastIndexOf('\\') + 1);
        } else {
            prefix = stem != null ? stem : "";
        }
        java.io.File[] files = dir.listFiles((_, name) -> name.startsWith(prefix) && name.toLowerCase().endsWith(".wav"));
        boolean ok = files != null && files.length > 0;
        AssignmentLogger.logMethodExit(this);
        return ok;
    }

    public static void main(String[] args) {
        AssignmentLogger.logMain();
        SwingUtilities.invokeLater(() -> {
            InstrumentGUI gui = new InstrumentGUI();
            gui.setVisible(true);
        });
    }
}
