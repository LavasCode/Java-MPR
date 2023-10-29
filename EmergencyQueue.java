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

    public Node(String namee, int finalPriority, int agee, int order, int estTime) {
        this.namee = namee;
        this.priority = finalPriority;
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
    private PriorityQueue<Node> queue1;
    private PriorityQueue<Node> queue2;
    private PatientQueueGUI queueGUI1;
    private PatientQueueGUI queueGUI2;
    private int order1 = 0;
    private int order2 = 0;
    private javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            List<Node> removedNodes = new ArrayList<>();
            for (Node node : queue1) {
                if (node.estTime > 1) {
                    node.estTime--;
                } else {
                    removedNodes.add(node);
                }
            }
            for (Node node : queue2) {
                if (node.estTime > 1) {
                    node.estTime--;
                } else {
                    removedNodes.add(node);
                }
            }
            queue1.removeAll(removedNodes);
            queue2.removeAll(removedNodes);
            queueGUI1.updateQueueDisplay(queue1);
            queueGUI2.updateQueueDisplay(queue2);

        }
    });

    public EmergencyQueue() {
        frame = new JFrame("Patient Registration Form");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel titleLabel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleText = new JLabel("Please fill out this form and you will be assigned a number in the queue:");
        titleText.setPreferredSize(new Dimension(450, 50));
        titleLabel.add(titleText);
        frame.add(titleLabel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(7, 2));
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
        formPanel.add(new JLabel("Contact Info: +91"));
        contactField = new JTextField(40);
        formPanel.add(contactField);
        formPanel.add(new JLabel("Describe your illness: "));
        illnessField = new JTextField(40);
        formPanel.add(illnessField);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel("OR"));
        formPanel.add(new JLabel(""));
        String[] emergencies = { "Select an emergency", "Cardiac Arrest", "Severe Bleeding/Trauma", "Stroke",
                "Heart Attack(Myocardial infarction)", "Pregnancy Complications" };
        emDropdown = new JComboBox<>(emergencies);
        formPanel.add(emDropdown);
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(300, 50));
        submitPanel.add(submitButton);
        frame.add(submitPanel, BorderLayout.SOUTH);
        frame.add(formPanel, BorderLayout.CENTER);
        queue1 = new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node a, Node b) {
                return Integer.compare(a.priority, b.priority);
            }
        });
        queue2 = new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node a, Node b) {
                return Integer.compare(a.priority, b.priority);
            }
        });
        queueGUI1 = new PatientQueueGUI("Queue 1");
        queueGUI2 = new PatientQueueGUI("Queue 2");
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
        long number;
        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || contactInfo.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            agee = Integer.parseInt(age);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Age must be a valid number.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ((agee > 120) || (agee < 0)) {
            JOptionPane.showMessageDialog(frame, "Enter a valid age.", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        contactInfo = contactInfo.trim();
        try {
            number = Long.parseLong(contactInfo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter only Numbers in the Contact Information field.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        number = contactInfo.length();
        if (number > 10) {
            JOptionPane.showMessageDialog(frame, "Please recheck your Contact Information.", "Registration Error",
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
        {
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

            // Determine which queue to enqueue the patient based on estimated wait time
            int waitTime1 = queue1.isEmpty() ? 0 : queue1.peek().estTime;
            int waitTime2 = queue2.isEmpty() ? 0 : queue2.peek().estTime;
            if (waitTime1 <= waitTime2) {
                int finalPriority = priority * 1000 + order1;
                queue1.offer(new Node(name, finalPriority, agee, order1++, 40));
                int extraTime = 40;
                int prevNodeEstTime = 0;
                for (Node node : queue1) {
                    if (node.priority < finalPriority) {
                        prevNodeEstTime = node.estTime;
                    } else if (node.priority == finalPriority) {
                        node.estTime = prevNodeEstTime + extraTime;
                    } else if (node.priority > finalPriority) {
                        node.estTime += extraTime;
                    }
                }
                List<Node> removedNodes = new ArrayList<>();
                for (Node node : queue1) {
                    if (node.estTime > 1) {
                        node.estTime--;
                    } else {
                        removedNodes.add(node);
                    }
                }
                queue1.removeAll(removedNodes);
                queueGUI1.updateQueueDisplay(queue1);
                queueGUI2.updateQueueDisplay(queue2);
            } else {
                int finalPriority = priority * 1000 + order2;
                queue2.offer(new Node(name, finalPriority, agee, order2++, 40));
                int extraTime = 40;
                int prevNodeEstTime = 0;
                for (Node node : queue2) {
                    if (node.priority < finalPriority) {
                        prevNodeEstTime = node.estTime;
                    } else if (node.priority == finalPriority) {
                        node.estTime = prevNodeEstTime + extraTime;
                    } else if (node.priority > finalPriority) {
                        node.estTime += extraTime;
                    }
                }
                List<Node> removedNodes = new ArrayList<>();
                for (Node node : queue2) {
                    if (node.estTime > 1) {
                        node.estTime--;
                    } else {
                        removedNodes.add(node);
                    }
                }
                queue2.removeAll(removedNodes);
                queueGUI1.updateQueueDisplay(queue1);
                queueGUI2.updateQueueDisplay(queue2);
            }

            nameField.setText("");
            ageField.setText("");
            male.setSelected(true);
            contactField.setText("");
            illnessField.setText("");
            emDropdown.setSelectedIndex(0);
            emSelect = false;
            List<Node> sortedNodes1 = new ArrayList<>(queue1);
            List<Node> sortedNodes2 = new ArrayList<>(queue2);
            sortedNodes1.sort(new Comparator<Node>() {
                public int compare(Node a, Node b) {
                    return Integer.compare(a.priority, b.priority);
                }
            });
            sortedNodes2.sort(new Comparator<Node>() {
                public int compare(Node a, Node b) {
                    return Integer.compare(a.priority, b.priority);
                }
            });
            queue1.clear();
            queue2.clear();
            queue1.addAll(sortedNodes1);
            queue2.addAll(sortedNodes2);
            queueGUI1.updateQueueDisplay(queue1);
            queueGUI2.updateQueueDisplay(queue2);
        }
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
    private String queueName;

    public PatientQueueGUI(String queueName) {
        this.queueName = queueName;
        JFrame frame = new JFrame(queueName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(outputArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateQueueDisplay(PriorityQueue<Node> priorityQueue) {
        outputArea.setText(queueName + " - Patients in the Queue:\n");
        for (Node node : priorityQueue) {
            outputArea.append("Name: " + node.namee + " Patient Id: " + node.priority + " Age: " + node.agee +
                    " Estimated Time: " + node.estTime + " seconds\n");
        }
    }

    public void updateOutput(String text) {
        outputArea.append(text);
    }
}
