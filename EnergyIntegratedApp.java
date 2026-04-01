package energyIntegratedApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Energy Lifecycle & Nuclear Output Analyzer
 * 物理計算、GUI、LCAシミュレーションを統合した完成版
 */
public class EnergyIntegratedApp extends JFrame {
    
    // フィールド: 入力用
    private JTextField fuelMassField, efficiencyField;
    private JTextArea logArea;
    private List<EnergyData> energyResults = new ArrayList<>();

    // 定数
    private static final BigDecimal AVOGADRO = new BigDecimal("6.02214076e23");
    private static final BigDecimal U235_ATOMIC_MASS = new BigDecimal("235.0439");
    private static final BigDecimal ENERGY_PER_FISSION_J = new BigDecimal("3.20436e-11");
    private static final double SOLAR_PANEL_DAY_J = 54000000.0; // 15kWh相当

    public EnergyIntegratedApp() {
        setTitle("Energy & LCA Integrated Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 設定パネル (North) ---
        JPanel configPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("シミュレーション・パラメータ"));
        
        configPanel.add(new JLabel(" U-235 質量 (g):"));
        fuelMassField = new JTextField("1.0");
        configPanel.add(fuelMassField);

        configPanel.add(new JLabel(" 燃焼効率 (0.0-1.0):"));
        efficiencyField = new JTextField("0.05");
        configPanel.add(efficiencyField);

        JButton runBtn = new JButton("シミュレーション実行");
        runBtn.setBackground(new Color(60, 179, 113));
        runBtn.setForeground(Color.WHITE);
        runBtn.addActionListener(e -> runSimulation());
        configPanel.add(runBtn);

        add(configPanel, BorderLayout.NORTH);

        // --- メイン表示エリア (Center: グラフ & ログ) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        GraphPanel graphPanel = new GraphPanel();
        splitPane.setLeftComponent(graphPanel);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        splitPane.setRightComponent(new JScrollPane(logArea));
        
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * 計算とLCAロジックの実行
     */
    private void runSimulation() {
        try {
            double mass = Double.parseDouble(fuelMassField.getText());
            double eff = Double.parseDouble(efficiencyField.getText());

            // 安全ガード
            if (mass <= 0 || mass > 50000) throw new Exception("質量制限(0-50kg)を超えています。");
            
            // 1. 核出力計算 (BigDecimal)
            BigDecimal moles = BigDecimal.valueOf(mass).divide(U235_ATOMIC_MASS, MathContext.DECIMAL128);
            BigDecimal totalJ = moles.multiply(AVOGADRO).multiply(ENERGY_PER_FISSION_J).multiply(BigDecimal.valueOf(eff));
            double gwh = totalJ.divide(new BigDecimal("3.6e12"), MathContext.DECIMAL128).doubleValue();

            // 2. 結果データの生成 (LCA指標を含む)
            energyResults.clear();
            // 核出力データ (建設, 運用, 廃炉, コスト)
            energyResults.add(new EnergyData("核出力", gwh, 8000, 3000, 4000, 150000));
            // 比較対象としての太陽光 (同じGWhを出すのに必要なLCA負荷)
            energyResults.add(new EnergyData("太陽光", gwh, 40000, 5000, 3000, 100000));

            updateLog(mass, eff, totalJ, gwh);
            repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "エラー: " + ex.getMessage());
        }
    }

    private void updateLog(double mass, double eff, BigDecimal totalJ, double gwh) {
        logArea.setText("");
        logArea.append("=== シミュレーション結果レポート ===\n");
        logArea.append(String.format("投入燃料: %.2f g / 効率: %.1f%%\n", mass, eff * 100));
        logArea.append(String.format("総エネルギー出力: %.4e Joules\n", totalJ));
        logArea.append(String.format("総発電量換算: %.4f GWh\n", gwh));
        logArea.append(String.format("太陽光パネル換算: %,.0f 枚の1日分に相当\n\n", totalJ.doubleValue() / SOLAR_PANEL_DAY_J));
        
        for (EnergyData data : energyResults) {
            logArea.append(String.format("[%s] LCA総排出量: %,.0f kg-CO2 / 総コスト: $%,.0f\n", 
                    data.name, data.getTotalCO2(), data.totalCostPerGWh * gwh));
        }
    }

    // データ構造
    static class EnergyData {
        String name;
        double gwh;
        double cCO2, oCO2, dCO2; // kg/GWh
        double totalCostPerGWh;

        public EnergyData(String name, double gwh, double c, double o, double d, double cost) {
            this.name = name; this.gwh = gwh;
            this.cCO2 = c; this.oCO2 = o; this.dCO2 = d;
            this.totalCostPerGWh = cost;
        }
        public double getTotalCO2() { return (cCO2 + oCO2 + dCO2) * gwh; }
    }

    // グラフ描画パネル
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (energyResults.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 100;
            double maxCO2 = energyResults.stream().mapToDouble(EnergyData::getTotalCO2).max().orElse(1);

            for (EnergyData data : energyResults) {
                int barHeight = (int) ((data.getTotalCO2() / maxCO2) * 150);
                g2.setColor(data.name.equals("核出力") ? new Color(70, 130, 180) : new Color(255, 140, 0));
                g2.fillRect(x, 200 - barHeight, 50, barHeight);
                g2.setColor(Color.BLACK);
                g2.drawString(data.name, x, 220);
                g2.drawString(String.format("%.0fkg", data.getTotalCO2()), x, 200 - barHeight - 10);
                x += 200;
            }
            g2.drawString("LCA総CO2排出量の対比 (kg)", 100, 50);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EnergyIntegratedApp::new);
    }
}
