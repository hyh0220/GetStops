/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package getstops;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author yanhaohu
 */
public class GetStops {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.net.MalformedURLException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public static void main(String[] args) throws IOException, MalformedURLException, ParserConfigurationException, SAXException {
        //getit("uiowa","red");
        //getit("uiowa","blue");
        addit("uiowa","red");
    }
    private static void getit(String agent,String route) throws MalformedURLException, IOException, ParserConfigurationException, SAXException{
        try (PrintWriter writer = new PrintWriter ("/Users/yanhaohu/Documents/CS/EBONGO/STOPS/"+agent+"."+route+".txt")) {
            URL stopsURL = new URL ("http://api.ebongo.org/route?agency="+agent+"&route="+route+"&api_key=xApBvduHbU8SRYvc74hJa7jO70Xx4XNO");
            InputStream xml = stopsURL.openStream();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xml);
            NodeList nList = doc.getElementsByTagName("stop");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    
                    Element eElement = (Element) nNode;
                    String number = eElement.getAttribute("number");
                    String title = eElement.getAttribute("title");
                    String lat = eElement.getAttribute("lat");
                    String lng = eElement.getAttribute("lng");
                    System.out.println("Stop number: " + eElement.getAttribute("number"));
                    writer.println(number+","+title+","+lat+","+lng);
                }
            }
        }
    }
    private static void addit(String agent, String route) throws FileNotFoundException, IOException{
        BufferedReader brLoc = null;
        BufferedReader brStop = null;
        String lineLoc = "";
        String lineStop = "";
        String cvsSplitBy = ",";
        String last = "";
        int i = 1;
        float a1 = 0, b1 = 0, a2 = 0, b2=0, s1 = 0, s2 = 0;
        PrintWriter writer = new PrintWriter ("/Users/yanhaohu/Documents/CS/EBONGO/Morepoints/"+"addStop"+agent+route+".txt");
        brLoc = new BufferedReader(new FileReader("/Users/yanhaohu/Documents/CS/EBONGO/Morepoints/More"+route+"RoutePoints2.txt"));
        //brLoc = new BufferedReader(new FileReader("/Users/yanhaohu/Documents/CS/EBONGO/"+route+"RoutePoints2.txt"));
        
        while ((lineLoc = brLoc.readLine()) != null) {
            brStop = new BufferedReader(new FileReader("/Users/yanhaohu/Documents/CS/EBONGO/STOPS/"+agent+"."+route+".txt"));
            System.out.println("here");
            String[] Locln = lineLoc.split(cvsSplitBy);
            float l1 = Float.parseFloat(Locln[1]);
            float l2 = Float.parseFloat(Locln[2]);
            while((lineStop = brStop.readLine()) != null){
                String[] Stopln = lineStop.split(cvsSplitBy);
                s1 = Float.parseFloat(Stopln[2]);
                s2 = Float.parseFloat(Stopln[3]);
                String s3 = Stopln[1];
                //System.out.println(String.valueOf((a1-s1)*(a1-l1)<=0 && (a2-s2)*(a2-l2)<=0));
                //when first occurs
                if (a1==0 && b1==0) {
                    break;
                }
                //stop within the location
                else if ((s1-a1)*(s1-l1)<=0 && (s2-a2)*(s2-l2)<=0) {
                     if (!last.equals(s1+","+s2+","+s3)) {
                        writer.println(i+","+s1+","+s2+","+s3);
                        last = s1+","+s2+","+s3;
                        i++;
                      }
                    break;
                }
            }
            //
            if (!((s1==a1&&s2==a2)||(s1==l1&&s2==l2))) {
                         writer.println(i+","+l1+","+l2+",0");
                         i++;
                     }
            a1 = l1;
            a2 = l2;
            
        }
        brLoc.close();
        writer.close();
        System.out.println(a1+","+s1+","+s2);
        System.out.println(i);
    }
}
