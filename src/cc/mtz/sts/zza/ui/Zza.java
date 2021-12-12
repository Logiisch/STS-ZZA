package cc.mtz.sts.zza.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class Zza extends JFrame {
   private JPanel jPanel2;
   private JLabel lblAbfahrt;
   private JLabel lblGleis;
   private JLabel lblInfoText;
   private JLabel lblVias;
   private JLabel lblZug;
   private JLabel lblZugZiel;

   public Zza() {
      this.initComponents();
   }

   public Zza(GraphicsConfiguration gc) {
      super(gc);
      this.initComponents();
   }

   private void initComponents() {
      BindingGroup bindingGroup = new BindingGroup();
      JPanel jPanel1 = new JPanel();
      this.lblZugZiel = new JLabel();
      this.lblGleis = new JLabel();
      this.lblAbfahrt = new JLabel();
      this.lblZug = new JLabel();
      this.lblVias = new JLabel();
      this.jPanel2 = new JPanel();
      this.lblInfoText = new MarqueeLabel();
      this.setDefaultCloseOperation(3);
      this.setTitle("Zugzielanzeiger");
      this.setAlwaysOnTop(true);
      this.setBackground(new Color(102, 102, 255));
      this.getContentPane().setLayout(new AbsoluteLayout());
      jPanel1.setBackground(new Color(34, 35, 117));
      Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${preferredSize}"), jPanel1, BeanProperty.create("preferredSize"));
      bindingGroup.addBinding(binding);
      jPanel1.setLayout(new AbsoluteLayout());
      this.lblZugZiel.setFont(new Font("Tahoma", 1, 24));
      this.lblZugZiel.setForeground(new Color(255, 255, 255));
      this.lblZugZiel.setText("Zugziel");
      jPanel1.add(this.lblZugZiel, new AbsoluteConstraints(86, 63, 317, -1));
      this.lblGleis.setFont(new Font("Tahoma", 0, 48));
      this.lblGleis.setForeground(new Color(255, 255, 255));
      this.lblGleis.setHorizontalAlignment(4);
      this.lblGleis.setText("XX");
      this.lblGleis.setHorizontalTextPosition(4);
      jPanel1.add(this.lblGleis, new AbsoluteConstraints(410, 10, -1, 49));
      this.lblAbfahrt.setFont(new Font("Tahoma", 0, 18));
      this.lblAbfahrt.setForeground(new Color(255, 255, 255));
      this.lblAbfahrt.setText("Abfahrt");
      jPanel1.add(this.lblAbfahrt, new AbsoluteConstraints(10, 11, -1, -1));
      this.lblZug.setFont(new Font("Tahoma", 0, 12));
      this.lblZug.setForeground(new Color(255, 255, 255));
      this.lblZug.setText("Zug");
      jPanel1.add(this.lblZug, new AbsoluteConstraints(10, 37, -1, -1));
      this.lblVias.setFont(new Font("Tahoma", 0, 12));
      this.lblVias.setForeground(new Color(255, 255, 255));
      this.lblVias.setText("Vias");
      jPanel1.add(this.lblVias, new AbsoluteConstraints(86, 37, -1, -1));
      this.jPanel2.setBackground(new Color(255, 255, 255));
      this.lblInfoText.setBackground(new Color(255, 255, 255));
      this.lblInfoText.setText("Infotext");
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addComponent(this.lblInfoText, Alignment.TRAILING, -1, 317, 32767));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.lblInfoText).addContainerGap(1, 32767)));
      jPanel1.add(this.jPanel2, new AbsoluteConstraints(86, 11, -1, 15));
      this.getContentPane().add(jPanel1, new AbsoluteConstraints(0, 0, 480, 123));
      bindingGroup.bind();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setBounds((screenSize.width - 493) / 2, (screenSize.height - 157) / 2, 493, 157);
   }

   public void update(final String zug, final String zugZiel, final String abfahrt, final String vias, final String infoText) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            Zza.this.lblZugZiel.setText(zugZiel);
            Zza.this.lblAbfahrt.setText(abfahrt);
            Zza.this.lblInfoText.setText(infoText);
            Zza.this.jPanel2.setVisible(!infoText.isEmpty());
            Zza.this.lblVias.setText(vias);
            Zza.this.lblZug.setText(zug);
            Zza.this.invalidate();
         }
      });
   }

   public void setGleis(final String gleis) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            Zza.this.lblGleis.setText(gleis);
            Zza.this.invalidate();
         }
      });
   }
}
