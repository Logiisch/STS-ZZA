package cc.mtz.sts.zza;

import cc.mtz.sts.zza.data.RewrittenDetails;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigProvider extends DefaultHandler {
   private Map<String, String> ansageTexte = new HashMap();
   private String ignorePattern;
   private String fernzugPattern;
   private boolean minutengenau;
   private boolean inAnsagen = false;
   private boolean inIgnorePattern = false;
   private boolean inFernzugPattern = false;
   private StringBuilder chars = new StringBuilder();

   public ConfigProvider(File file) throws SAXException, ParserConfigurationException, IOException {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      sp.parse(file, this);
   }

   public void setMinutengenau(boolean minutengenau) {
      this.minutengenau = minutengenau;
   }

   public String getAnsage(String name, RewrittenDetails details) {
      return this.getAnsage(name, details, (ZugFahrplanZeile)null, (String)null, (String)null);
   }

   public String getAnsage(String name, RewrittenDetails details, ZugFahrplanZeile fpl) {
      return this.getAnsage(name, details, fpl, (String)null, (String)null);
   }

   public String getAnsage(String name, RewrittenDetails details, ZugFahrplanZeile fpl, String vias) {
      return this.getAnsage(name, details, fpl, vias, (String)null);
   }

   public String getAnsage(String name, RewrittenDetails details, ZugFahrplanZeile fpl, String vias, String bahnhof) {
      String ansage = (String)this.ansageTexte.get(name);
      if (details != null) {
         ansage = ansage.replace("%ZUG%", Util.ansagenName(details.name));
         if (details.gleis != null) {
            ansage = ansage.replace("%GLEIS%", details.gleis.replaceFirst("[^0-9]*(.*)$", "$1"));
         }

         ansage = ansage.replace("%VON%", details.von);
         ansage = ansage.replace("%NACH%", details.nach);
         ansage = ansage.replace("%VERSPAETUNG%", Util.verspaetungsText(details.verspaetung, this.minutengenau, ""));
         if (vias != null && vias.length() > 0) {
            ansage = ansage.replace("%VIAS%", this.getAnsage("vias", (RewrittenDetails)null).replace("%STATIONEN%", vias));
         } else {
            ansage = ansage.replace("%VIAS%", "");
         }
      }

      if (fpl != null) {
         ansage = ansage.replace("%AN%", fpl.getFormattedAn());
         ansage = ansage.replace("%AB%", fpl.getFormattedAb());
         ansage = ansage.replace("%PLANGLEIS%", fpl.plan.replaceFirst("[^0-9]*(.*)$", "$1"));
         if (details != null) {
            StringBuilder anPrognose = new StringBuilder();
            anPrognose.append((int)((fpl.an + (long)(details.verspaetung * '\uea60')) / 60000L) / 60);
            anPrognose.append(":");
            anPrognose.append((int)((fpl.an + (long)(details.verspaetung * '\uea60')) / 60000L) % 60);
            ansage = ansage.replace("%AN_PROGNOSE%", anPrognose);
            StringBuilder abPrognose = new StringBuilder();
            abPrognose.append((int)((fpl.ab + (long)(details.verspaetung * '\uea60')) / 60000L) / 60);
            abPrognose.append(":");
            abPrognose.append((int)((fpl.ab + (long)(details.verspaetung * '\uea60')) / 60000L) % 60);
            ansage = ansage.replace("%AB_PROGNOSE%", abPrognose);
         }
      }

      if (bahnhof != null) {
         ansage = ansage.replace("%BAHNHOF%", bahnhof);
      }

      return ansage;
   }

   public String getIgnorePattern() {
      return this.ignorePattern;
   }

   public String getFernzugPattern() {
      return this.fernzugPattern;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (this.inAnsagen) {
         if (!qName.equals("ansagen")) {
            this.ansageTexte.put(qName, this.chars.toString().trim());
         } else {
            this.inAnsagen = false;
         }
      } else if (this.inIgnorePattern) {
         this.ignorePattern = this.chars.toString().trim();
         this.inIgnorePattern = false;
      } else if (this.inFernzugPattern) {
         this.fernzugPattern = this.chars.toString().trim();
         this.inFernzugPattern = false;
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.chars.append(ch, start, length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      this.chars = new StringBuilder();
      if (qName.equals("ansagen")) {
         this.inAnsagen = true;
      } else if (qName.equals("ignore-pattern")) {
         this.inIgnorePattern = true;
      } else if (qName.equals("fernzug-pattern")) {
         this.inFernzugPattern = true;
      }

   }
}
