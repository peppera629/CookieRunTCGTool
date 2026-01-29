package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;
import javax.swing.BorderFactory;
import java.awt.Dimension;

import util.CardUtil;
import util.AppPaths;
import util.Config;

import java.io.File;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FilePicker {

    private JDialog window;
    private DefaultListModel<String> fileListModel = new DefaultListModel<>();
    private JList<String> fileList;
    private JTextField fileName;
    private String defaultDirectory = AppPaths.userDataDir().resolve("deck").toString();
    private String currentDirectory = AppPaths.userDataDir().resolve("deck").toString();
    private String finalSelectedFile = null;
    private String mode;

    public String show(String mode) { // Mode can be "save", "load"
        this.mode = mode;
        window = new JDialog((JFrame) null, CardUtil.getTranslation("dialog.title.filepicker"), true);
        window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        window.setSize(800, 600);
        window.setLayout(new BorderLayout());
        reloadFileList(currentDirectory);
        fileList = new JList<>(fileListModel);
        fileList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(fileList, "CRnormal");
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null && !selectedFile.startsWith("// ")) {
                    fileName.setText(selectedFile);
                }
            }
        });
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection(fileList.getSelectedValue().replace("// ", ""));
                }
            }
        });
        JScrollPane scrollableFileList = new JScrollPane(fileList);
        window.add(scrollableFileList, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        JPanel fileNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileNameLabel = new JLabel(CardUtil.getTranslation("filename") + ": ");
        fileNameLabel.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(fileNameLabel, "CRnormal");
        fileNamePanel.add(fileNameLabel);
        fileName = new JTextField();
        fileName.setFont(MainUI.CRnormal);
        fileName.setPreferredSize(new Dimension(300, 30));
		MainUI.componentFontMap.put(fileName, "CRnormal");
        fileNamePanel.add(fileName);
        buttonPanel.add(fileNamePanel);

        JButton confirmButton = new JButton(CardUtil.getTranslation(mode));
        confirmButton.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(confirmButton, "CRnormal");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("load")) {
                    confirmSelection(fileList.getSelectedValue());
                } else if (mode.equals("save")) {
                    confirmSelection(fileName.getText());
                }
            }
        });
        buttonPanel.add(confirmButton);
        window.add(buttonPanel, BorderLayout.SOUTH);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        System.out.println("Selected file: " + finalSelectedFile);
        return finalSelectedFile;
    }

    public String[] showForCompare() {
        this.mode = "compare";
        window = new JDialog((JFrame) null, CardUtil.getTranslation("dialog.title.filepicker"), true);
        window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        window.setSize(800, 600);
        window.setLayout(new BorderLayout());
        reloadFileList(currentDirectory);
        fileList = new JList<>(fileListModel);
        fileList.setFont(MainUI.CRnormal);
		MainUI.componentFontMap.put(fileList, "CRnormal");
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null && !selectedFile.startsWith("// ")) {
                    fileName.setText(selectedFile);
                }
            }
        });
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection(fileList.getSelectedValue().replace("// ", ""));
                }
            }
        });
        JScrollPane scrollableFileList = new JScrollPane(fileList);
        window.add(scrollableFileList, BorderLayout.CENTER);

        JPanel masterPanel = new JPanel(new GridLayout(0, 1));
        window.add(masterPanel, BorderLayout.SOUTH);

        JPanel compareModePanel = new JPanel(new GridLayout(1, 0));
        compareModePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ButtonGroup compareModeGroup = new ButtonGroup();
        JRadioButton compareModeFrom = new JRadioButton(CardUtil.getTranslation("deck.compare.from"));
        compareModeFrom.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(compareModeFrom, "CRnormal");
        compareModeGroup.add(compareModeFrom);
        JRadioButton compareModeTo = new JRadioButton(CardUtil.getTranslation("deck.compare.to"));
        compareModeTo.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(compareModeTo, "CRnormal");
        compareModeGroup.add(compareModeTo);
        compareModeFrom.setSelected(true);
        compareModePanel.add(compareModeFrom);
        compareModePanel.add(compareModeTo);
        masterPanel.add(compareModePanel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        JPanel fileNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileNameLabel = new JLabel(CardUtil.getTranslation("filename") + ": ");
        fileNameLabel.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(fileNameLabel, "CRnormal");
        fileNamePanel.add(fileNameLabel);
        fileName = new JTextField();
        fileName.setFont(MainUI.CRnormal);
        fileName.setPreferredSize(new Dimension(300, 30));
		MainUI.componentFontMap.put(fileName, "CRnormal");
        fileNamePanel.add(fileName);
        buttonPanel.add(fileNamePanel);

        JButton confirmButton = new JButton(CardUtil.getTranslation("compare"));
        confirmButton.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(confirmButton, "CRnormal");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmSelection(fileList.getSelectedValue());
            }
        });
        buttonPanel.add(confirmButton);
        masterPanel.add(buttonPanel);
        window.add(masterPanel, BorderLayout.SOUTH);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        System.out.println("Selected file: " + finalSelectedFile);
        return new String[] {finalSelectedFile, compareModeFrom.isSelected() ? "from" : "to"};
    }

    private void reloadFileList(String directoryPath) {
        fileListModel.clear();
        File dir = new File(directoryPath);
        if (!currentDirectory.equals(defaultDirectory)) {
            fileListModel.addElement("..");
        }
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                    fileListModel.addElement(file.getName().substring(0, file.getName().length() - 4));
                } else if (file.isDirectory()) {
                    fileListModel.addElement("// " + file.getName());
                }
            }
        }
    }

    private void confirmSelection(String fileName) {
        if (fileName.equals("..")) {
            File currentDirFile = new File(currentDirectory);
            File parentDir = currentDirFile.getParentFile();
            if (parentDir != null) {
                currentDirectory = parentDir.getAbsolutePath();
                reloadFileList(currentDirectory);
            }
        } else {
            File selectedFile = new File(currentDirectory, fileName);
            File selectedFileWithExt = new File(currentDirectory, fileName + ".txt");
            if (selectedFile.isDirectory()) {
                currentDirectory = selectedFile.getAbsolutePath();
                reloadFileList(currentDirectory);
            } else {
                if (mode.equals("load") || mode.equals("compare")) {
                    if (selectedFileWithExt.isFile()) {
                        finalSelectedFile = selectedFileWithExt.getAbsolutePath();
                        window.dispose();
                    }
                } else if (mode.equals("save")) {
                    finalSelectedFile = new File(currentDirectory, this.fileName.getText() + ".txt").getAbsolutePath();
                    window.dispose();
                }
            }
        }
    }
}
