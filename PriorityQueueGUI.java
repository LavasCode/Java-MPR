
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Node {
    String data;
    int priority;

    public Node(String data, int priority) {
        this.data = data;
        this.priority = priority;
    }
}

public class PriorityQueueGUI {
    private PriorityQueue<Node> priorityQueue;
    private JTextField nodeField, priorityField;
    private JTextArea queueDisplayArea;

    public PriorityQueueGUI() {
        priorityQueue = new PriorityQueue<>(6, Comparator.comparingInt(node -> node.priority));

        JFrame frame = new JFrame("Priority Queue Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        nodeField = new JTextField(20);
        priorityField = new JTextField(10);
        JButton addButton = new JButton("Add Node");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        });

        inputPanel.add(new JLabel("Node: "));
        inputPanel.add(nodeField);
        inputPanel.add(new JLabel("Priority: "));
        inputPanel.add(priorityField);
        inputPanel.add(addButton);
        frame.add(inputPanel, BorderLayout.NORTH);

        queueDisplayArea = new JTextArea(10, 30);
        queueDisplayArea.setEditable(false);
        frame.add(new JScrollPane(queueDisplayArea), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private void addNode() {
        String nodeData = nodeField.getText();
        String priorityStr = priorityField.getText();

        try {
            int priority = Integer.parseInt(priorityStr);
            priorityQueue.offer(new Node(nodeData, priority));
            nodeField.setText("");
            priorityField.setText("");
            updateQueueDisplay();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid priority. Please enter a valid integer.");
        }
    }

    private void updateQueueDisplay() {
        queueDisplayArea.setText("Nodes in the Priority Queue:\n");
        for (Node node : priorityQueue) {
            queueDisplayArea.append("Node: " + node.data + " Priority: " + node.priority + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PriorityQueueGUI();
            }
        });
    }
}