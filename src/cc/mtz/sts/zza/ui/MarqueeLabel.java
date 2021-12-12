package cc.mtz.sts.zza.ui;

import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JLabel;

public class MarqueeLabel extends JLabel {
   public static final int MARQUEE_SPEED_DIV = 20;
   public static final int REPAINT_WITHIN_MS = 5;
   private static final long serialVersionUID = -7737312573505856484L;

   public MarqueeLabel() {
   }

   public MarqueeLabel(Icon image, int horizontalAlignment) {
      super(image, horizontalAlignment);
   }

   public MarqueeLabel(Icon image) {
      super(image);
   }

   public MarqueeLabel(String text, Icon icon, int horizontalAlignment) {
      super(text, icon, horizontalAlignment);
   }

   public MarqueeLabel(String text, int horizontalAlignment) {
      super(text, horizontalAlignment);
   }

   public MarqueeLabel(String text) {
      super(text);
   }

   protected void paintComponent(Graphics g) {
      g.translate((int)((Long.MAX_VALUE - System.currentTimeMillis()) / MARQUEE_SPEED_DIV % (long)(this.getWidth() * 2)) - this.getWidth(), 0);
      super.paintComponent(g);
      this.repaint(REPAINT_WITHIN_MS);
   }
}
