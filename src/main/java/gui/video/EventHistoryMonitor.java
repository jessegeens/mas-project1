package gui.video;

import environment.Environment;
import gui.setup.Setup;
import org.json.JSONObject;
import util.Variables;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EventHistoryMonitor extends JFrame {

    public EventHistoryMonitor() {
        this.table = new JTable(new DefaultTableModel(new Object[]{"Cycle", "Agent ID", "Action"}, 0));
        this.scrollPane = new JScrollPane(this.table);
        this.exportButton = new JButton("Export data");

        this.exportButton.addActionListener(this::export);
        this.eventTracker = new EventTracker(this::addRowTable);
    }


    /**
     * Initialize the GUI components
     */
    public void initialize() {
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(80);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        setSize(450, 300);

        setTitle("Actions");
        scrollPane.getViewport().add(table);
        getContentPane().add(this.exportButton, BorderLayout.SOUTH);

        setLocation(0, 400);
    }


    /**
     * Add a row to the table of occurred events.
     *
     * @param update The agent action that has occurred.
     */
    private void addRowTable(EventTracker.ActionUpdate update) {
        ((DefaultTableModel) this.table.getModel()).addRow(new Object[]{Integer.toString(update.getCycle()),
                Integer.toString(update.getAgentId()), update.toString()});

        // https://stackoverflow.com/questions/5147768/scroll-jscrollpane-to-bottom
        var scrollBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollBar.removeAdjustmentListener(this);
            }
        };
        scrollBar.addAdjustmentListener(downScroller);

        repaint();
    }



    /**
     * Functionality for the export button. Asks the user to choose a file in which to store the history.
     *  If an appropriate file is chosen, the history is written to this file.
     *
     * @param actionEvent The actionEvent originating from clicking the export button.
     */
    private void export(ActionEvent actionEvent) {
        if (!eventTracker.isRunFinished()) {
            var result = JOptionPane.showConfirmDialog(this, "The run is not finished yet. Are you sure you want to proceed?",
                    "Unfinished run", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // File dialog to save the history
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "*.json";
            }
        });

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(Variables.OUTPUT_PATH));
        var resultDialog = fileChooser.showSaveDialog(this);

        if (resultDialog == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();

            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            if (file.exists()) {
                var sure = JOptionPane.showConfirmDialog(this, "File already exists. Overwrite?",
                        "Overwrite file?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (sure != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            this.saveHistoryToFile(file);
        }
    }


    /**
     * Save the history observed so far to the given file in JSON format.
     *
     * @param file The file in which to write the history of actions.
     */
    void saveHistoryToFile(File file) {
        JSONObject content = eventTracker.getHistoryJSON();

        try {
            FileWriter writer = new FileWriter(file);
            content.write(writer, 0, 0);
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write history to file " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }



    private Environment getEnvironment() {
        return Setup.getInstance().getEnvironment();
    }


    public void reset() {
        ((DefaultTableModel) this.table.getModel()).setRowCount(0);
        eventTracker.reset();
    }


    private final JScrollPane scrollPane;
    private final JTable table;
    private final JButton exportButton;
    private final EventTracker eventTracker;
}
