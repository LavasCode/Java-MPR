import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientRegistrationPage {
    private JFrame frame;
    private JTextField nameField, ageField, contactField, illnessField;
    private JComboBox<String> emergencyDropdown;
    private JRadioButton maleRadioButton, femaleRadioButton;
    private ButtonGroup genderButtonGroup;
    private JButton submitButton;
    private boolean hasEmergencySelected = false;

    public PatientRegistrationPage() {
        frame = new JFrame("Patient Registration");
        frame.setLayout(new GridLayout(9, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add components to the frame
        frame.add(new JLabel("Name: "));
        nameField = new JTextField(20);
        frame.add(nameField);

        frame.add(new JLabel("Age: "));
        ageField = new JTextField(3);
        frame.add(ageField);

        frame.add(new JLabel("Gender: "));
        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadioButton);
        genderPanel.add(femaleRadioButton);
        frame.add(genderPanel);

        frame.add(new JLabel("Contact Info: "));
        contactField = new JTextField(20);
        frame.add(contactField);

        frame.add(new JLabel("Describe your illness: "));
        illnessField = new JTextField(50);
        frame.add(illnessField);

        frame.add(new JLabel("Emergency Type: "));
        String[] emergencies = { "Select an emergency", "Cardiac Arrest", "Severe Bleeding/Trauma", "Stroke",
                "Heart Attack(Myocardial infarction)", "Pregnancy Complications" };
        emergencyDropdown = new JComboBox<>(emergencies);
        frame.add(emergencyDropdown);

        submitButton = new JButton("Submit");
        frame.add(submitButton);

        // Add action listener for the Submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitRegistration();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void submitRegistration() {
        // Retrieve patient information
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = maleRadioButton.isSelected() ? "Male" : "Female"; // Get selected gender
        String contactInfo = contactField.getText();
        String selectedEmergency = (String) emergencyDropdown.getSelectedItem();
        String illness = illnessField.getText();

        // To check whether an emergency is selected or not, hasEmergencySelected stores
        // boolean true or false
        String selectEmergency = (String) emergencyDropdown.getSelectedItem();
        if (!selectEmergency.equals("Select an emergency")) {
            // Something other than "Select an emergency" has been selected
            hasEmergencySelected = true;
        } else {
            // "Select an emergency" is selected
            hasEmergencySelected = false;
        }

        // Check if either emergency or illness is selected, not both
        if (hasEmergencySelected && !illness.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select either Emergency or Illness, not both.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if either emergency or illness is filled
        if (!hasEmergencySelected && selectedEmergency.equals("Select an emergency") && illness.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in either Emergency or Illness.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform validation and processing here
        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || contactInfo.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Process the registration data (e.g., store it in a database or perform
            // further actions)
            String registrationInfo = "Name: " + name + "\nAge: " + age + "\nGender: " + gender
                    + "\nContact Info: " + contactInfo;
            if (hasEmergencySelected) {
                registrationInfo += "\nEmergency: " + selectedEmergency;
            } else {
                registrationInfo += "\nIllness Description: " + illness;
            }

            JOptionPane.showMessageDialog(frame,
                    "Registration successful.\n" + registrationInfo,
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            // Clear the input fields for the next registration
            nameField.setText("");
            ageField.setText("");
            maleRadioButton.setSelected(true); // Set default gender to Male
            contactField.setText("");
            illnessField.setText("");
            emergencyDropdown.setSelectedIndex(0);
            hasEmergencySelected = false;
        }
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
