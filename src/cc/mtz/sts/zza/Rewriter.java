package cc.mtz.sts.zza;

import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.RewrittenDetails;
import cc.mtz.sts.zza.data.ZugRewrite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DateFormatter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import js.java.stspluginlib.PluginClient.ZugDetails;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Rewriter {
   private final boolean fallbackToRis;
   private static Rewriter instance;
   private StellwerkFile config;

   public static void init(StellwerkFile config, boolean fallbackToRis) {
      instance = new Rewriter(config, fallbackToRis);
   }

   public static Rewriter getInstance() {
      if (instance == null) {
         throw new IllegalStateException("Not initialized");
      } else {
         return instance;
      }
   }

   private Rewriter(StellwerkFile config, boolean fallbackToRis) {
      this.config = config;
      this.fallbackToRis = fallbackToRis;
   }

   public RewrittenDetails rewrite(ZugDetails details, Bahnhof currentBhf) {
      RewrittenDetails ret = new RewrittenDetails(details);
      boolean rewritten = false;
      Iterator var5 = this.config.getRewrites().iterator();

      Iterator var8;
      while(var5.hasNext()) {
         ZugRewrite rewrite = (ZugRewrite)var5.next();
         if (rewrite.matches(details)) {
            rewritten = true;
            ret = rewrite.rewrite(details);
            if (rewrite.getVias() != null && rewrite.getVias().size() > 0) {
               List<String> finalVias = new LinkedList();
               var8 = rewrite.getVias().iterator();

               while(var8.hasNext()) {
                  String via = (String)var8.next();
                  finalVias.add(via);
                  if (via.equals(currentBhf.getName())) {
                     finalVias.clear();
                  }
               }

               StringBuilder viaString = new StringBuilder();
               Iterator var24 = finalVias.iterator();

               while(var24.hasNext()) {
                  String via = (String)var24.next();
                  viaString.append(" - ");
                  viaString.append(via);
               }

               if (viaString.length() >= 3) {
                  ret.vias = viaString.substring(3);
               } else {
                  ret.vias = "+++ ohne Halt +++";
               }
            }
            break;
         }
      }

      if (!rewritten && this.fallbackToRis) {
         try {
            RisParser parser = new RisParser();
            processRis(details.name, parser);
            if (parser.getStartBhf() != null && parser.getEndBhf() != null) {
               ret.von = parser.getStartBhf();
               ret.nach = parser.getEndBhf();
               if (parser.getVias().size() > 1) {
                  int startIndex = 0;
                  int i = 0;

                  while(true) {
                     if (i >= parser.getVias().size() - 1) {
                        StringBuilder viaString = new StringBuilder();
                        int interval = (parser.getVias().size() - 1) / 4;

                        for(i = startIndex; i < parser.getVias().size() - 1; i += interval) {
                           viaString.append(" - ");
                           viaString.append((String)parser.getVias().get(i));
                        }

                        if (viaString.length() > 3) {
                           ret.vias = viaString.substring(3);
                        }
                        break;
                     }

                     var8 = this.config.getBahnhoefe().iterator();

                     while(var8.hasNext()) {
                        Bahnhof bahnhof = (Bahnhof)var8.next();
                        if (((String)parser.getVias().get(i)).equals(bahnhof.getName())) {
                           startIndex = i + 1;
                        }
                     }

                     ++i;
                  }
               }

               rewritten = true;
            } else {
               Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS - Parsing not successful");
            }
         } catch (MalformedURLException var11) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var11);
         } catch (SAXException var12) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var12);
         } catch (IOException var13) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var13);
         } catch (TransformerConfigurationException var14) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var14);
         } catch (TransformerException var15) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var15);
         } catch (ParserConfigurationException var16) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var16);
         } catch (ParseException var17) {
            Logger.getLogger(Rewriter.class.getName()).log(Level.WARNING, "Error fetching from RIS", var17);
         }
      }

      ret.rewritten = rewritten;
      return ret;
   }

   private static void processRis(String zug, RisParser risHandler) throws ParseException, MalformedURLException, SAXException, IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
      String productClass;
      if (zug.startsWith("ICE ")) {
         productClass = "1";
      } else if (!zug.startsWith("IC ") && !zug.startsWith("EC ")) {
         if (zug.startsWith("IRE ")) {
            productClass = "8";
         } else if (!zug.startsWith("IR ") && !zug.startsWith("D ")) {
            productClass = "8";
         } else {
            productClass = "4";
         }
      } else {
         productClass = "2";
      }

      DateFormatter df = new DateFormatter(new SimpleDateFormat("dd.MM.yy"));
      Calendar cal = Calendar.getInstance();
      cal.set(6, cal.get(6) + 1);
      URL url = new URL("http://mobile.bahn.de/bin/mobil/trainsearch.exe/dox?ld=96236&rt=1&use_realtime_filter=1&date=" + df.valueToString(cal.getTime()) + "&trainname=" + zug.replaceAll("[a-zA-Z ]", "") + "&stationFilter=80&start=Suchen&productClassFilter=" + productClass);
      URLConnection conn = url.openConnection();
      InputStream is = conn.getInputStream();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int val;
      do {
         val = is.read();
         if (val != -1) {
            bos.write(val);
         }
      } while(val != -1);

      try {
         is.close();
      } catch (Exception var11) {
      }

      Parser parser = new Parser();
      parser.setFeature("http://www.ccil.org/~cowan/tagsoup/features/bogons-empty", false);
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
      parser.setContentHandler(risHandler);
      parser.parse(new InputSource(new ByteArrayInputStream(bos.toByteArray())));
      parser = null;
   }

   private static class RisParser extends DefaultHandler {
      private StringBuilder chars;
      private Mode mode;
      private String startBhf;
      private String endBhf;
      private List<String> vias;

      private RisParser() {
         this.chars = new StringBuilder();
         this.mode = Mode.NONE;
         this.vias = new LinkedList();
      }

      public void characters(char[] ch, int start, int length) throws SAXException {
         this.chars.append(new String(ch, start, length));
      }

      public void endElement(String uri, String localName, String qName) throws SAXException {
         if (qName.equals("td")) {
            switch(this.mode) {
            case START_BHF:
               this.startBhf = this.chars.toString().trim();
               break;
            case END_BHF:
               this.endBhf = this.chars.toString().trim();
               this.vias.add(this.chars.toString().trim());
            }
         }

      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (qName.equals("td")) {
            this.chars = new StringBuilder();
            this.mode = Mode.NONE;
            if (attributes.getValue(uri, "class") != null && attributes.getValue(uri, "class").equals("station tqdetail top")) {
               if (this.startBhf == null) {
                  this.mode = Mode.START_BHF;
               } else {
                  this.mode = Mode.END_BHF;
               }
            }
         }

      }

      public String getEndBhf() {
         return this.endBhf;
      }

      public String getStartBhf() {
         return this.startBhf;
      }

      public List<String> getVias() {
         return this.vias;
      }

      // $FF: synthetic method
      RisParser(Object x0) {
         this();
      }

      private static enum Mode {
         NONE,
         START_BHF,
         END_BHF;
      }
   }
}
