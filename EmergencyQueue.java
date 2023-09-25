import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Node {
    String namee;
    int priority;
    int order;
    int agee;
    int estTime;

    public Node(String namee, int priority, int agee, int order, int estTime) {
        this.namee = namee;
        this.priority = priority;
        this.agee = agee;
        this.order = order;
        this.estTime = estTime;
    }
}

public class EmergencyQueue {
    private JFrame frame;
    private JTextField nameField, ageField, contactField, illnessField;
    private JComboBox<String> emDropdown;
    private JRadioButton male, female;
    private ButtonGroup BtnGrp;
    private JButton submitButton;
    private boolean emSelect = false;
    private PriorityQueue<Node> PQ;
    private PatientQueueGUI queueGUI;
    private int order = 0;
    private javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateestTime();
        }
    });
    public EmergencyQueue() {
        frame = new JFrame("Patient Registration Form");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel formPanel = new JPanel(new GridLayout(9, 2));
        formPanel.add(new JLabel("Name: "));
        nameField = new JTextField(20);
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age: "));
        ageField = new JTextField(3);
        formPanel.add(ageField);
        formPanel.add(new JLabel("Gender: "));
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");
        BtnGrp = new ButtonGroup();
        BtnGrp.add(male);
        BtnGrp.add(female);
        JPanel genderPanel = new JPanel();
        genderPanel.add(male);
        genderPanel.add(female);
        formPanel.add(genderPanel);
        formPanel.add(new JLabel("Contact Info: "));
        contactField = new JTextField(40);
        formPanel.add(contactField);
        formPanel.add(new JLabel("Describe your illness: "));
        illnessField = new JTextField(40);
        formPanel.add(illnessField);
        formPanel.add(new JLabel("Emergency Type: "));
        String[] emergencies = { "Select an emergency", "Cardiac Arrest", "Severe Bleeding/Trauma", "Stroke",
                "Heart Attack(Myocardial infarction)", "Pregnancy Complications" };
        emDropdown = new JComboBox<>(emergencies);
        formPanel.add(emDropdown);
        submitButton = new JButton("Submit");
        formPanel.add(submitButton);
        frame.add(formPanel, BorderLayout.NORTH);
        PQ = new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node a, Node b) {
                return Integer.compare(a.priority, b.priority);
            }
        });
        queueGUI = new PatientQueueGUI();
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitRegistration();
            }
        });
        timer.start();
        frame.pack();
        frame.setVisible(true);
    }

    public void submitRegistration() {
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = male.isSelected() ? "Male" : "Female";
        String contactInfo = contactField.getText();
        String selectedEmergency = (String) emDropdown.getSelectedItem();
        String illness = illnessField.getText();
        int agee;
        try {
            agee = Integer.parseInt(age);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Age must be a valid integer.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String selectEm = (String) emDropdown.getSelectedItem();
        if (!selectEm.equals("Select an emergency")) {
            emSelect = true;
        } else {
            emSelect = false;
        }
        if (emSelect && !illness.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select either Emergency or Illness, not both.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!emSelect && selectEm.equals("Select an emergency") && illness.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in either Emergency or Illness.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int priority = 0;
        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || contactInfo.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            String registrationInfo = "Name: " + name + "\nAge: " + age + "\nGender: " + gender
                    + "\nContact Info: " + contactInfo;
            if (emSelect) {
                registrationInfo += "\nEmergency: " + selectedEmergency;
                if (selectedEmergency.equals("Cardiac Arrest")) {
                    priority = 1;
                } else if (selectedEmergency.equals("Severe Bleeding/Trauma")) {
                    priority = 2;
                } else if (selectedEmergency.equals("Stroke")) {
                    priority = 3;
                } else if (selectedEmergency.equals("Heart Attack(Myocardial infarction)")) {
                    priority = 4;
                } else if (selectedEmergency.equals("Pregnancy Complications")) {
                    priority = 5;
                }
            } else {
                registrationInfo += "\nIllness Description: " + illness;
                if (agee > 80) {
                    priority = 6;
                } else {
                    priority = 7;
                }
            }
            JOptionPane.showMessageDialog(frame, "Registration successful.\n" + registrationInfo,
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            if (PQ.size() < 10) {
                int compositePriority = priority * 1000 + order;
                PQ.offer(new Node(name, compositePriority, agee, order++, 30));
                int extraTime = 30;
                int prevNodeEstTime = 0;
                for (Node node : PQ) {
                    if (node.priority < compositePriority) {
                        prevNodeEstTime = node.estTime;
                    } else if (node.priority == compositePriority) {
                        node.estTime = prevNodeEstTime + extraTime;
                    } else if (node.priority > compositePriority) {
                        node.estTime += extraTime;
                    }
                }
            }
            nameField.setText("");
            ageField.setText("");
            male.setSelected(true);
            contactField.setText("");
            illnessField.setText("");
            emDropdown.setSelectedIndex(0);
            emSelect = false;
            List<Node> sortedNodes = new ArrayList<>(PQ);
            sortedNodes.sort(new Comparator<Node>() {

                public int compare(Node a, Node b) {
                    return Integer.compare(a.priority, b.priority);
                }
            });
            PQ.clear();
            PQ.addAll(sortedNodes);
            queueGUI.updateQueueDisplay(PQ);
        }

    }

    private void updateestTime() {
        List<Node> removedNodes = new ArrayList<>();
        for (Node node : PQ) {
            if (node.estTime > 1) {
                node.estTime--;
            } else {
                removedNodes.add(node);
            }
        }
        PQ.removeAll(removedNodes);
        queueGUI.updateQueueDisplay(PQ);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EmergencyQueue();
            }
        });
    }
}

class PatientQueueGUI {
    private JTextArea outputArea;

    public PatientQueueGUI() {
        JFrame frame = new JFrame("Patient Queue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(outputArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateQueueDisplay(PriorityQueue<Node> priorityQueue) {
        outputArea.setText("Patients in the Queue:\n");
        for (Node node : priorityQueue) {
            outputArea.append("Name: " + node.namee + " Patient Id: " + node.priority + " Age: " + node.agee +
                    " Estimated Time: " + node.estTime + " seconds\n");
        }
    }

    public void updateOutput(String text) {
        outputArea.append(text);
    }
}