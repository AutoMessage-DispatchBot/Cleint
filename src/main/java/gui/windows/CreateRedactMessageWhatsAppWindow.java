package gui.windows;

import data.Correspondence;
import data.StaticData.CorrespondenceType;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CreateRedactMessageWhatsAppWindow extends JPanel {

    private final JComboBox<CorrespondenceType> correspondenceType;
    private final JPanel allMessages;


    public CreateRedactMessageWhatsAppWindow(JDialog dialog) {

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 5, 5, 5);


        this.allMessages = new JPanel();
        this.allMessages.setLayout(new GridBagLayout());

        this.correspondenceType = new JComboBox<>();
        for(CorrespondenceType c: CorrespondenceType.values())
            this.correspondenceType.addItem(c);
        this.correspondenceType.setEditable(false);

        JButton saveButton = new JButton("Сохранить");
        JButton clearButton = new JButton("Очистить все");

        saveButton.addActionListener(e -> dialog.setVisible(false));
        clearButton.addActionListener(e -> {
            removeAllComponents(allMessages);
            allMessages.updateUI();
        });

        JButton addNewMessageButton = new JButton("Добавить сообщение");
        addNewMessageButton.addActionListener(e -> {
            constraints.gridx = 0;
            constraints.gridy = allMessages.getComponentCount();
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            allMessages.add(new MessageClass(allMessages, (CorrespondenceType) correspondenceType.getSelectedItem()), constraints);
            allMessages.updateUI();
        });







        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(saveButton, constraints);

        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(clearButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 5;
        constraints.gridheight = 5;
        JScrollPane scrollPane = new JScrollPane(allMessages);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        this.add(scrollPane, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        this.add(correspondenceType, constraints);

        constraints.gridx = 2;
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        this.add(addNewMessageButton, constraints);
    }



    private synchronized void removeAllComponents(JPanel panel) {
        Component[] components = panel.getComponents();

        for (Component component : components) {
            panel.remove(component);
        }

        panel.updateUI();
    }

    public synchronized Correspondence[] getCorrespondence() {
        List<Correspondence> correspondenceList = new ArrayList<>();

        for(int i = 0; i < allMessages.getComponentCount(); i++)
            correspondenceList.add(((MessageClass) allMessages.getComponent(i)).getCorrespondence());

        if(correspondenceList.size() == 0)
            return null;

        return correspondenceList.toArray(new Correspondence[0]);
    }






    private static class MessageClass extends JPanel {
        CorrespondenceType type;

        JTextField number;
        JTextArea text;

        File file;

        JFileChooser fileChooser;
        JPanel allMessages;


        public MessageClass(JPanel allMessages, CorrespondenceType type) {
            this.type = type;
            this.allMessages = allMessages;

            this.number = new JTextField();
            this.text = new JTextArea();
            this.number.setPreferredSize(new Dimension(200, 30));
            text.setPreferredSize(new Dimension(350, 100));

            this.fileChooser = new JFileChooser();

            JButton delete = new JButton("Удалить");
            delete.addActionListener(e -> {
                allMessages.remove(this);
                allMessages.updateUI();
            });

            JButton setFile = new JButton("Выбрать файл");
            setFile.addActionListener(e -> {
                File f = getFile();
                if(f != null) {
                    file = f;
                    this.number.setText(f.getName());
                }
            });

            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 5, 5, 5);

            this.setBorder(new LineBorder(Color.black, 1));


            JTextField title = new JTextField(type.toString());
            title.setEditable(false);

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            this.add(title, constraints);


            switch (type) {
                case NONE -> {
                    constraints.gridx = 0;
                    constraints.gridy = 1;
                    constraints.gridwidth = 2;
                    constraints.gridheight = 2;

                    this.add(text, constraints);

                    constraints.gridx = 2;
                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(delete, constraints);

                }

                case FILE, PHOTO_VIDEO -> {
                    if(type == CorrespondenceType.PHOTO_VIDEO)
                        setPhotoVideo();
                    else
                        setTextFiles();


                    this.number.setEditable(false);

                    constraints.gridx = 0;
                    constraints.gridy = 1;
                    constraints.gridwidth = 2;
                    constraints.gridheight = 2;

                    this.add(text, constraints);

                    constraints.gridx = 2;
                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(delete, constraints);

                    constraints.gridx = 0;
                    constraints.gridy = 3;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(number, constraints);

                    constraints.gridx = 1;
                    constraints.gridy = 3;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(setFile, constraints);
                }

                case CONTACT -> {
                    this.number.setEditable(true);

                    constraints.gridx = 0;
                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(this.number, constraints);

                    constraints.gridx = 1;
                    constraints.gridy = 1;
                    constraints.gridwidth = 1;
                    constraints.gridheight = 1;
                    this.add(delete, constraints);
                }
            }
        }

        public synchronized Correspondence getCorrespondence() {
            switch (type) {
                case NONE -> {
                    if(text.getText().length() == 0)
                        return null;

                    return new Correspondence(type, null, text.getText(), null);
                }

                case PHOTO_VIDEO, FILE -> {
                    if(text.getText().length() == 0 && file == null)
                        return null;

                    File f = null;
                    String s = null;
                    byte[] bytes = null;

                    if(file.exists()) {
                        try {
                            bytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
                            f = file;
                        } catch (IOException ignored) {}
                    }

                    if(text.getText().length() > 0)
                        s = text.getText();

                    return new Correspondence(type, f, s, bytes);
                }

                case CONTACT -> {
                    if(number.getText().length() == 0)
                        return null;

                    return new Correspondence(type, null, number.getText().replaceAll("\\D", ""), null);
                }

                default -> {
                    return null;
                }
            }
        }


        private void setPhotoVideo() {
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

            fileChooser.addChoosableFileFilter(photoFileFilter);
            fileChooser.addChoosableFileFilter(videoFileFilter);
        }

        private synchronized void setTextFiles() {
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

            fileChooser.addChoosableFileFilter(textFileFilter);
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
    }
}
