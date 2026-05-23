⚛️ Energy Lifecycle & Nuclear Output Analyzer~ 
核分裂エネルギーとLCA（ライフサイクルアセスメント）の統合シミュレーター ~Energy Lifecycle & Nuclear Output Analyzer は、ウラン235（U-235）の核分裂から得られる物理的なエネルギー出力と、その発電プロセス全体における環境負荷（CO2排出量）を同時に解析するJavaベースのシミュレーションツールです。
<img width="985" height="742" alt="スクリーンショット 2026-05-23 190914" src="https://github.com/user-attachments/assets/88de82da-f323-47ff-b9be-aea7e04fc7f7" />
Shutterstock詳しく見る🌟 プロジェクトの概要このアプリケーションは、単なるエネルギー計算機ではありません。
「1gの燃料からどれだけの電力が得られるか」という物理的問いに答えるだけでなく、同等の電力を太陽光発電で補った場合のLCA（建設、運用、廃炉を含む）を算出し、視覚的なグラフで比較します。
エネルギー密度と環境コストのトレードオフを構造的に理解することを目的としています。
✨ 主な機能高精度な核出力計算:BigDecimal クラスを採用し、原子量やアボガドロ定数を用いた微細な物質量計算の精度を担保。
燃焼効率（Efficiency）を考慮した、現実的な発電量（GWh）の算出。LCA（ライフサイクルアセスメント）分析:各電源の「建設・運用・廃棄」フェーズごとのCO2排出量データを内蔵。
同一出力（GWh）における、核エネルギーと再生可能エネルギー（太陽光）の環境負荷を対比。インタラクティブなGUI:Graph Panel: 計算結果をリアルタイムでバーチャート化。
Detailed Logs: 太陽光パネルの枚数換算やコスト推算を含む、詳細なレポートを出力。
物理定数への準拠:$E = mc^2$ 的なアプローチではなく、核分裂あたりのエネルギー（$3.20436 \times 10^{-11} J$）に基づいたボトムアップ計算。
🛠 技術スタックLanguage: Java 17+GUI Framework: Java Swing / AWTCoordinate Systems: Graphics2Dによるカスタムグラフ描画Numerical Precision: java.math.BigDecimal (DECIMAL128相当の精度)🚀 実行方法Java Development Kit (JDK) がインストールされていることを確認します。
ソースファイルをコンパイル・実行します。
Bashjavac energyIntegratedApp/EnergyIntegratedApp.java
java energyIntegratedApp.EnergyIntegratedApp
📝 計算ロジック本アプリでは、以下のプロセスで出力を算出しています。
物質量の算出: $mol = \frac{mass}{U235\_AtomicMass}$原子数の算出: $atoms = mol \times N_A$総エネルギーの算出: $Energy(J) = atoms \times EnergyPerFission \times Efficiency$環境負荷の算出: 各フェーズの $kg-CO2/GWh$ 定数を総発電量に乗算。
📜 ライセンスMIT License「エネルギーの価値は、その出力だけでなく、生涯コストで定義される。」

