package cc.mtz.sts.zza;

import cc.mtz.sts.zza.listener.AnsageManager;
import cc.mtz.sts.zza.listener.StsListener;
import cc.mtz.sts.zza.listener.ZzaManager;
import cc.mtz.sts.zza.sound.SoundPlayer;
import cc.mtz.sts.zza.ui.BahnsteigSelector;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Main {
   public static ScheduledExecutorService ses;
   public static PluginClientImpl client;
   private static final String VERSION = "2.0";
   public static ConfigProvider CONFIG;

   public static void init(String host, StellwerkFile config, boolean zza, boolean ansage, boolean einfahrt, boolean anschluesse, boolean verspaetungen, boolean minutengenau, boolean ris) throws SAXException, ParserConfigurationException, IOException {
      SoundPlayer.getInstance().start();
      Rewriter.init(config, ris);
      List<StsListener> listener = new LinkedList<>();
      if (zza) {
         listener.add(new ZzaManager(minutengenau));
      }

      if (ansage) {
         listener.add(new AnsageManager(config, einfahrt, anschluesse, verspaetungen, minutengenau));
      }

      CONFIG.setMinutengenau(minutengenau);
      client = new PluginClientImpl("Zugzielanzeiger + Bahnsteigansage", "Matthias Butz", "2.0", "Dieses Plugin simuliert Zugzielanzeiger und automatische Bahnsteigansagen an einem oder mehreren Bahnhöfen innerhalb eines Stellwerks.", config, listener);
      client.connect(host);
      ses = new ScheduledThreadPoolExecutor(5);
      ses.scheduleWithFixedDelay(new Runnable() {
         public void run() {
            if (Main.client.isReady()) {
               Main.client.requestZuege();
            }

         }
      }, 1L, 1L, TimeUnit.MINUTES);
   }

   public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException var9) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, var9);
      }

      CONFIG = new ConfigProvider(new File("config.xml"));
      final BahnsteigSelector selector = new BahnsteigSelector();
      final ConfigClient client = new ConfigClient("ZZA Konfiguration", "Matthias Butz", "2.0", "Konfigurationsdialog für Zugzielanzeiger + Bahnsteigansage");

      try {
         client.connect("localhost");
         EventQueue.invokeLater(() -> selector.setVisible(true));
         synchronized(client) {
            try {
               client.wait();
            } catch (InterruptedException ignored) {
            }
         }

         EventQueue.invokeLater(new Runnable() {
            public void run() {
               selector.setBahnsteige(client.getBahnsteige());
            }
         });
         client.close();
      } catch (ConnectException var8) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               selector.setVisible(true);
               selector.setBahnsteige(client.getBahnsteige());
            }
         });
      }

   }
}
