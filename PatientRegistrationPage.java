import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
class Node {
    String data;
    int priority;
    int insertionOrder;
    int agee;
    int estimatedTime;
    public Node(String data, int priority, int agee, int insertionOrder, int estimatedTime) {
        this.data = data;
        this.priority = priority;
        this.agee = agee;
        this.insertionOrder = insertionOrder;
        this.estimatedTime = estimatedTime;
    }
}
public class PatientRegistrationPage {
    private JFrame frame;
    private JTextField nameField, ageField, contactField, illnessField;
    private JComboBox<String> emergencyDropdown;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private ButtonGroup genderButtonGroup;
    private JButton submitButton;
    private boolean hasEmergencySelected = false;
    private PriorityQueue<Node> priorityQueue;
    private PriorityQueueGUI queueGUI;
    private int insertionOrder = 0;
    private javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateEstimatedTime();
        }
    });
    private int countdown = 0;
    public PatientRegistrationPage() {
        frame = new JFrame("Patient Registration");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel formPanel = new JPanel(new GridLayout(9, 2));
        formPanel.add(new JLabel("Name: "));
        nameField = new JTextField(50);
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age: "));
        ageField = new JTextField(3);
        formPanel.add(ageField);
        formPanel.add(new JLabel("Gender: "));
        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadioButton);
        genderPanel.add(femaleRadioButton);
        formPanel.add(genderPanel);
        formPanel.add(new JLabel("Contact Info: "));
        contactField = new JTextField(20);
        formPanel.add(contactField);
        formPanel.add(new JLabel("Describe your illness: "));
        illnessField = new JTextField(20);
        formPanel.add(illnessField);
        formPanel.add(new JLabel("Emergency Type: ")); 
        String[] emergencies = { "Select an emergency", "Cardiac Arrest", "Severe Bleeding/Trauma", "Stroke",
                "Heart Attack(Myocardial infarction)", "Pregnancy Complications" };
        emergencyDropdown = new JComboBox<>(emergencies);
        formPanel.add(emergencyDropdown);
        submitButton = new JButton("Submit");
        formPanel.add(submitButton);
        frame.add(formPanel, BorderLayout.NORTH);
        priorityQueue = new PriorityQueue<>(10, new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                return Integer.compare(a.priority, b.priority);
            }
        });
        queueGUI = new PriorityQueueGUI();
        submitButton.addActionListener(new ActionListener() {
            @Override
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
        String gender = maleRadioButton.isSelected() ? "Male" : "Female";
        String contactInfo = contactField.getText();
        String selectedEmergency = (String) emergencyDropdown.getSelectedItem();
        String illness = illnessField.getText();
        int agee;
        try {
            agee = Integer.parseInt(age);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Age must be a valid integer.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String selectEmergency = (String) emergencyDropdown.getSelectedItem();
        if (!selectEmergency.equals("Select an emergency")) {
            hasEmergencySelected = true;
        } else {
            hasEmergencySelected = false;
        }
        if (hasEmergencySelected && !illness.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select either Emergency or Illness, not both.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!hasEmergencySelected && selectEmergency.equals("Select an emergency") && illness.isEmpty()) {
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
            if (hasEmergencySelected) {
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
            if (priorityQueue.size() < 10) {
                int compositePriority = priority * 1000 + insertionOrder;
                priorityQueue.offer(new Node(name, compositePriority, agee, insertionOrder++, 30));
                int extraTime = 30; 
                int prevNodeEstTime = 0;
                for (Node node : priorityQueue) {
                    if (node.priority < compositePriority) {
                        prevNodeEstTime = node.estimatedTime;
                    } else if (node.priority == compositePriority) {
                        node.estimatedTime = prevNodeEstTime + extraTime;
                    } else {
                        node.estimatedTime += extraTime;
                    }
                }
            }
            nameField.setText("");
            ageField.setText("");
            maleRadioButton.setSelected(true);
            contactField.setText("");
            illnessField.setText("");
            emergencyDropdown.setSelectedIndex(0);
            hasEmergencySelected = false;
            List<Node> sortedNodes = new ArrayList<>(priorityQueue);
            sortedNodes.sort(new Comparator<Node>() {
                @Override
                public int compare(Node a, Node b) {
                    return Integer.compare(a.priority, b.priority);
                }
            });
            priorityQueue.clear();
            priorityQueue.addAll(sortedNodes);
            queueGUI.updateQueueDisplay(priorityQueue, countdown);
        }

    }
    private void updateEstimatedTime() {
        List<Node> removedNodes = new ArrayList<>();
        for (Node node : priorityQueue) {
            if (node.estimatedTime > 1) {
                node.estimatedTime--;
            } else {
                removedNodes.add(node);
            }
        }
        priorityQueue.removeAll(removedNodes);
        if (!priorityQueue.isEmpty() && countdown == 0) {
            countdown = priorityQueue.peek().estimatedTime;
        } else if (countdown > 0) {
            countdown--;
        }
        queueGUI.updateQueueDisplay(priorityQueue, countdown);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PatientRegistrationPage();
            }
        });
    }
}
class PriorityQueueGUI {
    private JTextArea outputArea;
    public PriorityQueueGUI() {
        JFrame frame = new JFrame("Priority Queue Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(outputArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }
    public void updateQueueDisplay(PriorityQueue<Node> priorityQueue, int countdown) {
        outputArea.setText("Nodes in the Priority Queue:\n");
        for (Node node : priorityQueue) {
            outputArea.append("Node: " + node.data + " Patient Id: " + node.priority + " Age: " + node.agee +
                    " Estimated Time: " + node.estimatedTime + " seconds\n");
        }
    }
    public void appendToOutput(String text) {
        outputArea.append(text);
    }
}