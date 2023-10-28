package gui.windows;

import data.Correspondence;
import data.StaticData.CorrespondenceType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CreateRedactMessageEmailWindow extends JPanel {

    private final JTextField title;
    private final JTextArea text;

    private final JPanel allFiles;

    private final JFileChooser fileChooser;

    public CreateRedactMessageEmailWindow(JDialog dialog) {
        this.setLayout(new GridBagLayout());

        this.fileChooser = new JFileChooser();
        setFileChooserSettings();

        this.title = new JTextField();
        this.text = new JTextArea();
        this.allFiles = new JPanel();
        this.allFiles.setLayout(new GridBagLayout());

        JTextField titleN = new JTextField("Заголовок:");
        JTextField textN = new JTextField("Текст:");
        textN.setPreferredSize(new Dimension(50, 50));

        titleN.setEditable(false);
        textN.setEditable(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 5, 5, 5);


        JButton clearAll = new JButton("Очистить все");
        JButton saveAll = new JButton("Сохранить");
        saveAll.addActionListener(e -> dialog.setVisible(false));

        JPanel saveClearButtons = new JPanel(new GridLayout(1,2));
        saveClearButtons.add(clearAll);
        saveClearButtons.add(saveAll);



        JPanel filesTotal = new JPanel();
        filesTotal.setLayout(new BoxLayout(filesTotal, BoxLayout.Y_AXIS));
        filesTotal.setPreferredSize(new Dimension(200, 200));

        JButton addNewFile = new JButton("Добавить файл");
        final boolean[] isZero = {true};

        addNewFile.addActionListener(e -> {
            File file = getFile();
            if(file != null) {
                if(isZero[0]) {
                    constraints.gridx = 0;
                    isZero[0] = false;
                }
                else {
                    constraints.gridx = 1;
                    isZero[0] = true;
                }

                constraints.gridy = (int) allFiles.getComponentCount()/2;
                constraints.gridwidth = 1;
                constraints.gridheight = 1;
                allFiles.add(new FilesClass(allFiles, file), constraints);
                allFiles.updateUI();
            }
            this.repaint();
        });

        filesTotal.add(addNewFile);
        filesTotal.add(new JScrollPane(allFiles));

        clearAll.addActionListener(e -> {
            title.setText("");
            text.setText("");
            removeAllComponents(allFiles);

            this.updateUI();
        });


        // clearAll, saveAll, titleN, title, textN, text, filesTotal
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(clearAll, constraints);

        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(saveAll, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(titleN, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        title.setPreferredSize(new Dimension(200, 25));
        this.add(title, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        textN.setPreferredSize(new Dimension(50,25));
        this.add(textN, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        text.setPreferredSize(new Dimension(150, 300));
        this.add(text, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        filesTotal.setPreferredSize(new Dimension(300, 300));
        this.add(filesTotal, constraints);
    }

    public Correspondence[] getCorrespondence() {
        if(text.getText().length() == 0)
            return null;

        Correspondence title = new Correspondence(CorrespondenceType.NONE, null, this.title.getText(), null);
        Correspondence text = new Correspondence(CorrespondenceType.NONE, null, this.text.getText(), null);

        List<Correspondence> filesList = new ArrayList<>();

        for(int i = 0; i < allFiles.getComponentCount(); i++) {
            File file = ((FilesClass) allFiles.getComponent(i)).getFile();

            try {
                Correspondence correspondence = new Correspondence(CorrespondenceType.FILE, file, null, Files.readAllBytes(Path.of(file.getAbsolutePath())));
                filesList.add(correspondence);
            } catch (IOException ignored) {}
        }

        Correspondence[] correspondenceArray = new Correspondence[filesList.size() + 2];
        correspondenceArray[0] = title;
        correspondenceArray[1] = text;

        for (int i = 0; i < filesList.size(); i++)
            correspondenceArray[i + 2] = filesList.get(i);


        return correspondenceArray;
    }



    private File getFile() {
        int result = fileChooser.showOpenDialog(null); // Показываем диалог выбора файла

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null && selectedFile.exists()) {
                return selectedFile;
            }
        }

        return null;
    }

    private void setFileChooserSettings() {

        FileFilter textFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".txt") ||
                        file.getName().toLowerCase().endsWith(".pdf") | file.getName().toLowerCase().endsWith(".docx") ||
                        file.getName().toLowerCase().endsWith(".doc") || file.getName().toLowerCase().endsWith(".xls") ||
                        file.getName().toLowerCase().endsWith(".xlsx") || file.getName().toLowerCase().endsWith(".pptx") ||
                        file.getName().toLowerCase().endsWith(".rtf");
            }

            @Override
            public String getDescription() {
                return "Текстовые файлы (*.txt, *.pdf, *.docx, *.doc, *.xls, *.xlsx, *.pptx, *.rtf)";
            }
        };

        FileFilter photoFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".jpeg") ||
                        file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "Формат изображений (*.jpeg, *.jpg, *.png)";
            }
        };

        FileFilter videoFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".mp4") ||
                        file.getName().toLowerCase().endsWith(".avi") || file.getName().toLowerCase().endsWith(".mkv") ||
                        file.getName().toLowerCase().endsWith(".mov") || file.getName().toLowerCase().endsWith(".wmv") ||
                        file.getName().toLowerCase().endsWith(".flv") || file.getName().toLowerCase().endsWith(".webm") ||
                        file.getName().toLowerCase().endsWith(".mpeg");
            }

            @Override
            public String getDescription() {
                return "Формат видео (*.mp4, *.avi, *.mkv, *.mov, *.wmv, *.flv, *.webm, *.mpeg)";
            }
        };


        fileChooser.addChoosableFileFilter(textFileFilter);
        fileChooser.addChoosableFileFilter(photoFileFilter);
        fileChooser.addChoosableFileFilter(videoFileFilter);
    }


    private synchronized void removeAllComponents(JPanel panel) {
        Component[] components = panel.getComponents();

        for (Component component : components) {
            panel.remove(component);
        }

        panel.updateUI();
    }





    private static class FilesClass extends JPanel {
        private final File file;

        public FilesClass(JPanel allFiles, File file) {
            this.file = file;
            this.setPreferredSize(new Dimension(240, 30));

            this.setLayout(new GridLayout(1,2));

            JButton delete = new JButton("Удалить");
            delete.addActionListener(e -> {
                allFiles.remove(this);
                allFiles.updateUI();
            });

            JTextField fileName = new JTextField(file.getName());
            fileName.setEditable(false);

            this.add(fileName);
            this.add(delete);
        }

        public File getFile() {
            return file;
        }
    }
}
