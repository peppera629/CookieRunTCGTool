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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import dataStructure.Deck;
import dataStructure.CardLoader;
import dataStructure.Card;
import dataStructure.CardList;
import util.CardUtil;
import util.AppPaths;
import util.Config;
import util.CardUtil.CardColor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.io.File;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FilePicker {

    private JDialog window;
    private DefaultListModel<String> fileListModel = new DefaultListModel<>();
    private JList<String> fileList;
    private Map<String, CardUtil.CardColor> colorMap = new HashMap<>();
    private Map<String, String> formattedTextMap = new HashMap<>(); // Stores unformatted text / formatted for display in JList
    private JTextField fileName;
    private String defaultDirectory = AppPaths.userDataDir().resolve("deck").toString();
    private String currentDirectory = AppPaths.userDataDir().resolve("deck").toString();
    private String finalSelectedFile = null;
    private String mode;

    private String iconPathR = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/R.png").toString()).getAbsolutePath() + "\">";
	private String iconPathY = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/Y.png").toString()).getAbsolutePath() + "\">";
	private String iconPathG = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/G.png").toString()).getAbsolutePath() + "\">";
	private String iconPathB = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/B.png").toString()).getAbsolutePath() + "\">";
	private String iconPathP = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/P.png").toString()).getAbsolutePath() + "\">";
	private String iconPathW = "<img src=\"file:" + new File(AppPaths.dataDir().resolve("icons/16px/W.png").toString()).getAbsolutePath() + "\">";

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
                String selectedFile = formattedTextMap.get(fileList.getSelectedValue());
                if (selectedFile != null && !selectedFile.startsWith("// ")) {
                    fileName.setText(selectedFile);
                }
            }
        });
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection(formattedTextMap.get(fileList.getSelectedValue()).replace("// ", ""));
                }
            }
        });
        JScrollPane scrollableFileList = new JScrollPane(fileList);
        scrollableFileList.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        window.add(scrollableFileList, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 3;
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
        buttonPanel.add(fileNamePanel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton confirmButton = new JButton(CardUtil.getTranslation(mode));
        confirmButton.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(confirmButton, "CRnormal");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mode.equals("load")) {
                    confirmSelection(formattedTextMap.get(fileList.getSelectedValue()));
                } else if (mode.equals("save")) {
                    confirmSelection(fileName.getText());
                }
            }
        });
        buttonPanel.add(confirmButton, gbc);
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
                String selectedFile = formattedTextMap.get(fileList.getSelectedValue());
                if (selectedFile != null && !selectedFile.startsWith("// ")) {
                    fileName.setText(selectedFile);
                }
            }
        });
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection(formattedTextMap.get(fileList.getSelectedValue()).replace("// ", ""));
                }
            }
        });
        JScrollPane scrollableFileList = new JScrollPane(fileList);
        scrollableFileList.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
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

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 3;

        JPanel fileNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileNameLabel = new JLabel(CardUtil.getTranslation("filename") + ": ");
        fileNameLabel.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(fileNameLabel, "CRnormal");
        fileNamePanel.add(fileNameLabel);
        fileName = new JTextField();
        fileName.setFont(MainUI.CRnormal);
        fileName.setPreferredSize(new Dimension(400, 30));
		MainUI.componentFontMap.put(fileName, "CRnormal");
        fileNamePanel.add(fileName);
        buttonPanel.add(fileNamePanel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;

        JButton confirmButton = new JButton(CardUtil.getTranslation("compare"));
        confirmButton.setFont(MainUI.CRnormal);
        MainUI.componentFontMap.put(confirmButton, "CRnormal");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmSelection(formattedTextMap.get(fileList.getSelectedValue()));
            }
        });
        buttonPanel.add(confirmButton, gbc);
        masterPanel.add(buttonPanel);
        window.add(masterPanel, BorderLayout.SOUTH);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        System.out.println("Selected file: " + finalSelectedFile);
        return new String[] {finalSelectedFile, compareModeFrom.isSelected() ? "from" : "to"};
    }

    private void reloadFileList(String directoryPath) {
        fileListModel.clear();
        colorMap.clear();
        int folderPointer = 0;
        File dir = new File(directoryPath);
        if (!currentDirectory.equals(defaultDirectory)) {
            fileListModel.addElement("..");
            formattedTextMap.put("..", "..");
        }
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                    String actualFileName = file.getName().substring(0, file.getName().length() - 4);
                    String formattedFileName = "";

                    int[] colorCount = new int[CardColor.values().length];
                    int nonZeroColors = 0;
                    int maxCount = 0;
                    int dominantColorIndex = 0;
                    try {
                        FileInputStream reader = new FileInputStream(file);
                        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8")); 
                        String data;
                        while((data = input.readLine()) != null) {	
                            if (!data.equals("") && !data.startsWith("//")) {
                                Card card = CardList.getInstance().getCardById(data);
                                if (card != null && card.getColor() != CardColor.Pure) {
                                    colorCount[card.getColor().getValue()] += 1;
                                }
                            }
                        } 
                        reader.close();
                        input.close();

                        for (int i=0; i<colorCount.length-1; i++) {
                            if (colorCount[i] > 0) {
                                nonZeroColors++;
                            }
                        }

                        for (int i=0; i<colorCount.length-1; i++) { // Excluding pure
                            if (colorCount[i] > maxCount) {
                                maxCount = colorCount[i];
                                dominantColorIndex = i;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    CardColor dominantColor = (nonZeroColors >= 1 ? CardColor.fromValue(dominantColorIndex) : CardColor.Pure);

                    colorMap.put(actualFileName, dominantColor);
                    switch (dominantColor) {
                        case Red:
                            formattedFileName = "<html>" + iconPathR + "&nbsp;" + actualFileName + "</html>";
                            break;
                        case Yellow:
                            formattedFileName = "<html>" + iconPathY + "&nbsp;" + actualFileName + "</html>";
                            break;
                        case Green:
                            formattedFileName = "<html>" + iconPathG + "&nbsp;" + actualFileName + "</html>";
                            break;
                        case Blue:
                            formattedFileName = "<html>" + iconPathB + "&nbsp;" + actualFileName + "</html>";
                            break;
                        case Purple:
                            formattedFileName = "<html>" + iconPathP + "&nbsp;" + actualFileName + "</html>";
                            break;
                        case Pure:
                            formattedFileName = "<html>" + iconPathW + "&nbsp;" + actualFileName + "</html>";
                            break;
                        default:
                            break;
                    }
                    fileListModel.addElement(formattedFileName);
                    formattedTextMap.put(formattedFileName, actualFileName);
                } else if (file.isDirectory()) {
                    fileListModel.add(folderPointer, "// " + file.getName());
                    formattedTextMap.put("// " + file.getName(), file.getName());
                    folderPointer++;
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
