import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankersAlgorithm extends JFrame implements ActionListener {
    private JTextField[][] allocationTF, maxTF;
    private JTextField[] availableTF;
    private JButton calculateButton;
    private int n, m;
    private int[][] allocation, max, need;
    private int[] available;

    public BankersAlgorithm() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setTitle("Banker's Algorithm");

        // Get process and resource count
        try {
            n = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of processes:"));
            m = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of resources:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
            System.exit(0);
        }

        allocationTF = new JTextField[n][m];
        maxTF = new JTextField[n][m];
        availableTF = new JTextField[m];

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(n * 2 + 2, m + 1, 5, 5));
        JPanel availablePanel = new JPanel(new GridLayout(2, m + 1, 5, 5));
        JPanel bottomPanel = new JPanel();

        inputPanel.add(new JLabel("Process"));
        for (int j = 0; j < m; j++) inputPanel.add(new JLabel("Alloc R" + j));
        for (int j = 0; j < m; j++) inputPanel.add(new JLabel("Max R" + j));

        for (int i = 0; i < n; i++) {
            inputPanel.add(new JLabel("P" + i));
            for (int j = 0; j < m; j++) {
                allocationTF[i][j] = new JTextField(3);
                inputPanel.add(allocationTF[i][j]);
            }
            for (int j = 0; j < m; j++) {
                maxTF[i][j] = new JTextField(3);
                inputPanel.add(maxTF[i][j]);
            }
        }

        availablePanel.add(new JLabel("Available Resources:"));
        for (int j = 0; j < m; j++) {
            availableTF[j] = new JTextField(3);
            availablePanel.add(availableTF[j]);
        }

        calculateButton = new JButton("Calculate Safe Sequence");
        calculateButton.addActionListener(this);
        bottomPanel.add(calculateButton);

        mainPanel.add(availablePanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            allocation = new int[n][m];
            max = new int[n][m];
            need = new int[n][m];
            available = new int[m];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    allocation[i][j] = Integer.parseInt(allocationTF[i][j].getText().trim());
                    max[i][j] = Integer.parseInt(maxTF[i][j].getText().trim());
                    need[i][j] = max[i][j] - allocation[i][j];
                    if (need[i][j] < 0) {
                        JOptionPane.showMessageDialog(this, "Error: Need for P" + i + " R" + j + " is negative.");
                        return;
                    }
                }
            }

            for (int j = 0; j < m; j++) {
                available[j] = Integer.parseInt(availableTF[j].getText().trim());
            }

            // Banker's Algorithm
            boolean[] finish = new boolean[n];
            int[] safeSequence = new int[n];
            int count = 0;

            while (count < n) {
                boolean found = false;
                for (int i = 0; i < n; i++) {
                    if (!finish[i]) {
                        int j;
                        for (j = 0; j < m; j++) {
                            if (need[i][j] > available[j])
                                break;
                        }
                        if (j == m) {
                            for (int k = 0; k < m; k++) {
                                available[k] += allocation[i][k];
                            }
                            safeSequence[count++] = i;
                            finish[i] = true;
                            found = true;
                        }
                    }
                }
                if (!found) break;
            }

            if (count == n) {
                JOptionPane.showMessageDialog(this,
                        "Safe Sequence Found:\n" + formatSequence(safeSequence) +
                                "\n\nNeed Matrix:\n" + formatNeedMatrix());
            } else {
                JOptionPane.showMessageDialog(this, "System is in an unsafe state.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers in all fields.");
        }
    }

    private String formatSequence(int[] seq) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seq.length; i++) {
            sb.append("P").append(seq[i]);
            if (i != seq.length - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private String formatNeedMatrix() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("P").append(i).append(": ");
            for (int j = 0; j < m; j++) {
                sb.append(need[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankersAlgorithm::new);
    }
}
