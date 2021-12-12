package cc.mtz.sts.zza.ui;

import cc.mtz.sts.zza.Main;
import cc.mtz.sts.zza.SimpleConfig;
import cc.mtz.sts.zza.StellwerkFile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class BahnsteigSelector extends JFrame {
    private JCheckBox chkAnsage;
   private JCheckBox chkAnsageAnschluesse;
   private JCheckBox chkAnsageEinfahrt;
   private JCheckBox chkAnsageVerspaetungen;
   private JCheckBox chkMinutengenau;
   private JCheckBox chkRis;
   private JCheckBox chkZza;
   private JList<String> lstBahnsteige;
   private JRadioButton radAdvanced;
   private JRadioButton radSimple;
   private JTextField txtBahnhofsname;
   private JTextField txtFile;
   private JTextField txtHost;

   public BahnsteigSelector() {
      this.initComponents();
   }

   private void initComponents() {
       ButtonGroup buttonGroup1 = new ButtonGroup();
       JScrollPane jScrollPane1 = new JScrollPane();
      this.lstBahnsteige = new JList<>();
       JButton btnStart = new JButton();
      this.radAdvanced = new JRadioButton();
      this.radSimple = new JRadioButton();
      this.txtFile = new JTextField();
       JButton btnBrowse = new JButton();
       JLabel jLabel1 = new JLabel();
       JLabel jLabel2 = new JLabel();
       JPanel jPanel1 = new JPanel();
      this.chkZza = new JCheckBox();
      this.chkAnsage = new JCheckBox();
      this.chkAnsageEinfahrt = new JCheckBox();
      this.chkAnsageAnschluesse = new JCheckBox();
      this.chkAnsageVerspaetungen = new JCheckBox();
      this.chkRis = new JCheckBox();
      this.chkMinutengenau = new JCheckBox();
       JLabel jLabel3 = new JLabel();
       JLabel jLabel4 = new JLabel();
      this.txtBahnhofsname = new JTextField();
       JLabel jLabel5 = new JLabel();
       JButton btnCancel = new JButton();
       JPanel jPanel2 = new JPanel();
      this.txtHost = new JTextField();
       JLabel jLabel6 = new JLabel();
       JLabel jLabel7 = new JLabel();
      this.setDefaultCloseOperation(2);
      this.setTitle("Zugzielanzeiger + Bahnsteigansage für StellwerkSim");
      jScrollPane1.setViewportView(this.lstBahnsteige);
      btnStart.setText("Start");
      btnStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.btnStartActionPerformed(evt);
         }
      });
      buttonGroup1.add(this.radAdvanced);
      this.radAdvanced.setSelected(true);
      this.radAdvanced.setText("Erweiterter Modus");
      this.radAdvanced.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.radAdvancedActionPerformed(evt);
         }
      });
      buttonGroup1.add(this.radSimple);
      this.radSimple.setText("Einfacher Modus");
      this.txtFile.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.txtFileActionPerformed(evt);
         }
      });
      btnBrowse.setText("Durchsuchen...");
      btnBrowse.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.btnBrowseActionPerformed(evt);
         }
      });
      jLabel1.setText("<html>Auswahl einer Konfigurationsdatei für das aktuelle Stellwerk mit Definition<br/> der Bahnhöfe und optionalen erweiterten Zuglaufdefinitionen im XML-Format");
      jLabel2.setText("<html>Definition eines einzelnen Bahnhofs<br/>durch Auswahl der Bahnsteige in<br/>rechtsseitiger Liste.<br/>(STRG gedrückt halten zur Auswahl<br/>mehrerer Bahnsteige)");
      jPanel1.setBorder(BorderFactory.createTitledBorder("Optionen"));
      this.chkZza.setSelected(true);
      this.chkZza.setText("Zugzielanzeiger aktivieren");
      this.chkZza.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.chkZzaActionPerformed(evt);
         }
      });
      this.chkAnsage.setSelected(true);
      this.chkAnsage.setText("Ansagemodul aktivieren");
      this.chkAnsage.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            BahnsteigSelector.this.chkAnsageStateChanged(evt);
         }
      });
      this.chkAnsage.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.chkAnsageActionPerformed(evt);
         }
      });
      this.chkAnsageEinfahrt.setSelected(true);
      this.chkAnsageEinfahrt.setText("Einfahrt ansagen");
      this.chkAnsageEinfahrt.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.chkAnsageEinfahrtActionPerformed(evt);
         }
      });
      this.chkAnsageAnschluesse.setSelected(true);
      this.chkAnsageAnschluesse.setText("Anschlüsse ansagen");
      this.chkAnsageVerspaetungen.setSelected(true);
      this.chkAnsageVerspaetungen.setText("Verspätungen ansagen");
      this.chkRis.setText("Zugläufe aus RIS beziehen");
      this.chkRis.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.chkRisActionPerformed(evt);
         }
      });
      this.chkMinutengenau.setSelected(true);
      this.chkMinutengenau.setText("Minutengenaue Verspätungen");
      GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(this.chkZza).addComponent(this.chkAnsage).addComponent(this.chkAnsageEinfahrt).addComponent(this.chkAnsageAnschluesse).addComponent(this.chkAnsageVerspaetungen).addComponent(this.chkRis).addComponent(this.chkMinutengenau)).addContainerGap(94, 32767)));
      jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.chkZza).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.chkAnsage).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.chkAnsageEinfahrt).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.chkAnsageAnschluesse).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.chkAnsageVerspaetungen).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.chkMinutengenau).addGap(1, 1, 1).addComponent(this.chkRis).addContainerGap(9, 32767)));
      jLabel3.setText("Datei:");
      jLabel4.setText("Bahnsteige:");
      jLabel5.setText("Bahnhofsname:");
      btnCancel.setText("Abbrechen");
      btnCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            BahnsteigSelector.this.btnCancelActionPerformed(evt);
         }
      });
      jPanel2.setBorder(BorderFactory.createTitledBorder("Verbindung"));
      this.txtHost.setText("localhost");
      jLabel6.setText("Stellwerksrechner:");
      jLabel7.setText("<html>(Änderung nur bei Nutzung mehrerer Rechner<br/>notwendig.)");
      GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel6).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.txtHost, -2, 137, -2)).addComponent(jLabel7, -2, -1, -2)).addContainerGap(30, 32767)));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE).addComponent(jLabel6).addComponent(this.txtHost, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(jLabel7, -2, -1, -2).addContainerGap(25, 32767)));
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addGap(22, 22, 22).addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.radSimple).addGroup(layout.createSequentialGroup().addGap(21, 21, 21).addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel5).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.txtBahnhofsname, -2, 106, -2)).addComponent(jLabel2, -2, -1, -2)))).addGap(31, 31, 31).addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(jScrollPane1, -2, 146, -2).addComponent(jLabel4))).addGroup(layout.createSequentialGroup().addGap(21, 21, 21).addComponent(jLabel1, -2, -1, -2)).addComponent(this.radAdvanced)).addGap(20, 20, 20)).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(jLabel3).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.txtFile, -2, 142, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnBrowse).addGap(66, 66, 66))).addGap(4, 4, 4).addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(btnStart, -2, 130, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnCancel, -1, 146, 32767)).addComponent(jPanel2, -1, -1, 32767).addComponent(jPanel1, -2, -1, -2)).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.radAdvanced).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(jLabel1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(this.txtFile, -2, -1, -2).addComponent(btnBrowse).addComponent(jLabel3)).addGroup(layout.createParallelGroup(Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.radSimple).addPreferredGap(ComponentPlacement.RELATED).addComponent(jLabel2, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED, -1, 32767).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(jLabel5).addComponent(this.txtBahnhofsname, -2, -1, -2)).addGap(66, 66, 66)).addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addComponent(jLabel4).addPreferredGap(ComponentPlacement.RELATED).addComponent(jScrollPane1, -2, 178, -2)))).addGroup(layout.createSequentialGroup().addGap(19, 19, 19).addComponent(jPanel1, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(jPanel2, -2, -1, -2).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(btnStart, -1, -1, 32767).addComponent(btnCancel)))).addContainerGap(17, -2)));
      this.pack();
   }

   private void radAdvancedActionPerformed(ActionEvent evt) {
   }

   private void btnBrowseActionPerformed(ActionEvent evt) {
      JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(this) == 0) {
         try {
            this.txtFile.setText(chooser.getSelectedFile().getCanonicalPath());
         } catch (IOException var4) {
            Logger.getLogger(BahnsteigSelector.class.getName()).log(Level.SEVERE, (String)null, var4);
         }
      }

   }

   private void txtFileActionPerformed(ActionEvent evt) {
   }

   private void chkZzaActionPerformed(ActionEvent evt) {
   }

   private void chkAnsageEinfahrtActionPerformed(ActionEvent evt) {
   }

   private void chkRisActionPerformed(ActionEvent evt) {
   }

   private void chkAnsageActionPerformed(ActionEvent evt) {
   }

   private void chkAnsageStateChanged(ChangeEvent evt) {
      if (this.chkAnsage.isSelected()) {
         this.chkAnsageAnschluesse.setEnabled(true);
         this.chkAnsageEinfahrt.setEnabled(true);
         this.chkAnsageVerspaetungen.setEnabled(true);
      } else {
         this.chkAnsageAnschluesse.setEnabled(false);
         this.chkAnsageEinfahrt.setEnabled(false);
         this.chkAnsageVerspaetungen.setEnabled(false);
      }

   }

   private void btnStartActionPerformed(ActionEvent evt) {
      if (this.radAdvanced.isSelected()) {
         try {
            Main.init(this.txtHost.getText(), new StellwerkFile(new File(this.txtFile.getText())), this.chkZza.isSelected(), this.chkAnsage.isSelected(), this.chkAnsage.isSelected() ? this.chkAnsageEinfahrt.isSelected() : false, this.chkAnsage.isSelected() ? this.chkAnsageAnschluesse.isSelected() : false, this.chkAnsage.isSelected() ? this.chkAnsageVerspaetungen.isSelected() : false, this.chkMinutengenau.isSelected(), this.chkRis.isSelected());
         } catch (SAXException | ParserConfigurationException | IOException var7) {
            Logger.getLogger(BahnsteigSelector.class.getName()).log(Level.SEVERE, (String)null, var7);
         }
      } else if (this.radSimple.isSelected()) {
         try {
            List<String> bahnsteige = new LinkedList<>();
            Object[] var3 = this.lstBahnsteige.getSelectedValuesList().toArray();
            int var4 = var3.length;

             for (Object selected : var3) {
                 bahnsteige.add((String) selected);
             }

            Main.init(this.txtHost.getText(), new SimpleConfig(this.txtBahnhofsname.getText(), bahnsteige), this.chkZza.isSelected(), this.chkAnsage.isSelected(), this.chkAnsage.isSelected() ? this.chkAnsageEinfahrt.isSelected() : false, this.chkAnsage.isSelected() ? this.chkAnsageAnschluesse.isSelected() : false, this.chkAnsage.isSelected() ? this.chkAnsageVerspaetungen.isSelected() : false, this.chkMinutengenau.isSelected(), this.chkRis.isSelected());
         } catch (SAXException | ParserConfigurationException | IOException var10) {
            Logger.getLogger(BahnsteigSelector.class.getName()).log(Level.SEVERE, (String)null, var10);
         }
      }

      this.setVisible(false);
   }

   private void btnCancelActionPerformed(ActionEvent evt) {
      System.exit(0);
   }

   public void setBahnsteige(Collection<String> bahnsteige) {
       String[] buffer = new String[bahnsteige.size()];
       bahnsteige.toArray(buffer);
       this.lstBahnsteige.setListData(buffer);
   }
}
