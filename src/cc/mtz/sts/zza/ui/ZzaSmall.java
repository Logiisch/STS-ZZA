package cc.mtz.sts.zza.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class ZzaSmall extends Zza {
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JLabel lblAbfahrt;
   private JLabel lblGleis;
   private JLabel lblInfoText;
   private JLabel lblVias;
   private JLabel lblZug;
   private JLabel lblZugZiel;

   public ZzaSmall() {
      this.initComponents();
   }

   public ZzaSmall(GraphicsConfiguration gc) {
      super(gc);
      this.initComponents();
   }

   private void initComponents() {
      BindingGroup bindingGroup = new BindingGroup();
      this.jPanel1 = new JPanel();
      this.lblZugZiel = new JLabel();
      this.lblGleis = new JLabel();
      this.lblAbfahrt = new JLabel();
      this.lblZug = new JLabel();
      this.lblVias = new JLabel();
      this.jPanel2 = new JPanel();
      this.lblInfoText = new MarqueeLabel();
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setTitle("Zugzielanzeiger");
      this.setAlwaysOnTop(true);
      this.setBackground(new Color(102, 102, 255));
      this.setResizable(false);
      this.getContentPane().setLayout(new AbsoluteLayout());
      this.jPanel1.setBackground(new Color(34, 35, 117));
      Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${preferredSize}"), this.jPanel1, BeanProperty.create("preferredSize"));
      bindingGroup.addBinding(binding);
      this.jPanel1.setLayout(new AbsoluteLayout());
      this.lblZugZiel.setFont(new Font("Tahoma", 1, 14));
      this.lblZugZiel.setForeground(new Color(255, 255, 255));
      this.lblZugZiel.setText("Zugziel");
      this.jPanel1.add(this.lblZugZiel, new AbsoluteConstraints(70, 35, 317, -1));
      this.lblGleis.setFont(new Font("Tahoma", 0, 24));
      this.lblGleis.setForeground(new Color(255, 255, 255));
      this.lblGleis.setHorizontalAlignment(4);
      this.lblGleis.setText("XX");
      this.lblGleis.setHorizontalTextPosition(4);
      this.jPanel1.add(this.lblGleis, new AbsoluteConstraints(280, 0, -1, 49));
      this.lblAbfahrt.setFont(new Font("Tahoma", 0, 12));
      this.lblAbfahrt.setForeground(new Color(255, 255, 255));
      this.lblAbfahrt.setText("Abfahrt");
      this.jPanel1.add(this.lblAbfahrt, new AbsoluteConstraints(10, 5, -1, -1));
      this.lblZug.setFont(new Font("Tahoma", 0, 10));
      this.lblZug.setForeground(new Color(255, 255, 255));
      this.lblZug.setText("Zug");
      this.jPanel1.add(this.lblZug, new AbsoluteConstraints(10, 20, -1, -1));
      this.lblVias.setFont(new Font("Tahoma", 0, 8));
      this.lblVias.setForeground(new Color(255, 255, 255));
      this.lblVias.setText("Vias");
      this.jPanel1.add(this.lblVias, new AbsoluteConstraints(70, 22, -1, -1));
      this.jPanel2.setBackground(new Color(255, 255, 255));
      this.lblInfoText.setBackground(new Color(255, 255, 255));
      this.lblInfoText.setFont(new Font("Tahoma", 0, 8));
      this.lblInfoText.setText("Infotext");
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.lblInfoText, -2, 200, -2).addGap(0, 0, 32767)));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.lblInfoText, -1, -1, 32767).addContainerGap()));
      this.jPanel1.add(this.jPanel2, new AbsoluteConstraints(70, 8, 200, 10));
      this.getContentPane().add(this.jPanel1, new AbsoluteConstraints(0, 0, 320, 60));
      bindingGroup.bind();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setBounds((screenSize.width - 331) / 2, (screenSize.height - 92) / 2, 331, 92);
   }

   public void update(final String zug, final String zugZiel, final String abfahrt, final String vias, final String infoText) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            ZzaSmall.this.lblZugZiel.setText(zugZiel);
            ZzaSmall.this.lblAbfahrt.setText(abfahrt);
            ZzaSmall.this.lblInfoText.setText(infoText);
            ZzaSmall.this.jPanel2.setVisible(!infoText.isEmpty());
            ZzaSmall.this.lblVias.setText(vias);
            ZzaSmall.this.lblZug.setText(zug);
            ZzaSmall.this.invalidate();
         }
      });
   }

   public void setGleis(final String gleis) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            ZzaSmall.this.lblGleis.setText(gleis);
            ZzaSmall.this.invalidate();
         }
      });
   }
}
