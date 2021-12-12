package cc.mtz.sts.zza;

import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.ZugRewrite;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StellwerkFile extends DefaultHandler {
   protected List<ZugRewrite> rewrites = new LinkedList();
   private ZugRewrite currentRewrite;
   protected Map<String, String> soundReplaces;
   protected List<Bahnhof> bahnhoefe = new LinkedList();
   private String crFind;
   private String crReplace;
   private StringBuilder chars = new StringBuilder();

   public StellwerkFile(File file) throws SAXException, ParserConfigurationException, IOException {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      sp.parse(file, this);
   }

   protected StellwerkFile() {
      this.soundReplaces = new LinkedHashMap();
   }

   public List<ZugRewrite> getRewrites() {
      return this.rewrites;
   }

   public List<Bahnhof> getBahnhoefe() {
      return this.bahnhoefe;
   }

   public Map<String, String> getSoundReplaces() {
      return this.soundReplaces;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("rewrite")) {
         this.rewrites.add(this.currentRewrite);
      } else if (qName.equals("via")) {
         this.currentRewrite.getVias().add(this.chars.toString());
      } else if (qName.equals("replace")) {
         this.crReplace = this.chars.toString();
         this.soundReplaces.put(this.crFind, this.crReplace);
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.chars.append(ch, start, length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      this.chars = new StringBuilder();
      if (qName.equals("rewrite")) {
         this.currentRewrite = new ZugRewrite(attributes.getValue(uri, "zug"), attributes.getValue(uri, "simstart"), attributes.getValue(uri, "simende"), attributes.getValue(uri, "start"), attributes.getValue(uri, "ende"), new LinkedList());
      } else if (qName.equals("soundreplaces")) {
         this.soundReplaces = new LinkedHashMap();
      } else if (qName.equals("replace")) {
         this.crFind = attributes.getValue(uri, "find");
      } else if (qName.equals("bahnhof")) {
         this.bahnhoefe.add(new Bahnhof(attributes.getValue(uri, "name"), attributes.getValue(uri, "gleise"), attributes.getValue(uri, "ende")));
      }

   }
}
