package it.unipr.ce.dsg.example.googleearth.kml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * 
 * @author Marco Picone (picone.m@gmail.com)
 *
 */
public class KmlManager {

	private ArrayList<PlaceMark> pmList = new ArrayList<PlaceMark>();
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private DOMImplementation impl;
	private Document doc;
	private LookAt lookAt = null;
	private Style styleObj = null;
	private Folder folder = null;
	
	 public KmlManager() throws ParserConfigurationException
	 {
		  factory = DocumentBuilderFactory.newInstance();
	      builder = factory.newDocumentBuilder();
	      impl = builder.getDOMImplementation();  
	      doc = impl.createDocument(null,null,null);
	 }
	
	 public void generateKMLDocument(String name, String description)
	 { 
		   //Create main kml tag
	      Element kml = doc.createElement("kml");
	      kml.setAttribute("xmls","http://earth.google.com/kml/2.2");
	      doc.appendChild(kml);
	      
	      Element document = doc.createElement("Document");
	      kml.appendChild(document);
	      
	      Element nameTag = doc.createElement("name");
	      nameTag.setTextContent(name);
	      document.appendChild(nameTag);
	      
	      Element open = doc.createElement("open");
	      open.setTextContent("1");
	      document.appendChild(open);
	      
	      Element desc = doc.createElement("description");
	      desc.setTextContent(description);
	      document.appendChild(desc);
	      
	      //Check and create LookAt tag
	      if(this.lookAt != null)
	      {
	    	  
	    	 Element la = this.addNewElement(document, "LookAt", null, null, null);
	    	 this.addNewElement(la, "longitude", new Double(this.lookAt.getGp().getLon()).toString(), null, null);
	    	 this.addNewElement(la, "latitude", new Double(this.lookAt.getGp().getLat()).toString(), null, null);
	    	 this.addNewElement(la, "altitude", new Double(this.lookAt.getGp().getAltitude()).toString(), null, null);
	    	 this.addNewElement(la, "range", lookAt.getRange(), null, null);
	    	 this.addNewElement(la, "tilt", lookAt.getTilt(), null, null);
	    	 this.addNewElement(la, "heading", lookAt.getHeading(), null, null);
	      
	      }
	    
	      if(this.styleObj != null)
	      {
	    	  Element style = this.addNewElement(document, "Style", null,"id", this.styleObj.getId());
	    	  Element iconStyle = this.addNewElement(style, "IconStyle", null,null,null);
	    	  Element icon = this.addNewElement(iconStyle, "Icon", null,null,null);
	    	  this.addNewElement(icon, "href",this.styleObj.getIconHref(),null,null);
	      
	      }
	      
	      if(this.folder != null)
	      {
	    	  Element folderTag = this.addNewElement(document, "Folder", null,null,null);
	    	  this.addNewElement(folderTag, "name", this.folder.getName(),null,null);
	    	  this.addNewElement(folderTag, "open", this.folder.getOpen(),null,null);
	    	  this.addNewElement(folderTag,"description", this.folder.getDescription(),null,null);
	    	 
	    	  
	    	  if(this.folder != null && this.folder.getLookAt() != null)
	    	  {
		    	  Element la = this.addNewElement(folderTag, "LookAt", null, null, null);
			      this.addNewElement(la, "longitude", new Double(this.folder.getLookAt().getGp().getLon()).toString(), null, null);
			      this.addNewElement(la, "latitude", new Double(this.folder.getLookAt().getGp().getLat()).toString(), null, null);
			      this.addNewElement(la, "altitude", new Double(this.folder.getLookAt().getGp().getAltitude()).toString(), null, null);
			      this.addNewElement(la, "range", this.folder.getLookAt().getRange(), null, null);
			      this.addNewElement(la, "tilt", this.folder.getLookAt().getTilt(), null, null);
			      this.addNewElement(la, "heading", this.folder.getLookAt().getHeading(), null, null);
			        
	    	  }
	    	  
		      if(this.folder != null && this.folder.getListItemType() != null)
		      {
		    	  Element style = this.addNewElement(folderTag, "Style", null, null, null);
			      Element listStyle = this.addNewElement(style, "ListStyle", null, null, null);
			      this.addNewElement(listStyle, "listItemType", this.folder.getListItemType(), null, null);
		      }
		      
		      
		      for(int index=0; index<this.folder.getPlmList().size();index++)
		      {
		    	  PlaceMark placeMark = this.folder.getPlmList().get(index);
		    	  
		    	  //Create PlaceMark Tag
		    	  Element plm = this.addNewElement(folderTag, "Placemark", null, null, null);
		    	  
		    	  //Set Name
		    	  this.addNewElement(plm, "name", placeMark.getName(), null, null);
		    	  
		    	  //TimeStamp for PlaceMark
		    	  Element timeStamp = this.addNewElement(plm, "TimeStamp", null, null, null);
		    	  this.addNewElement(timeStamp, "when", placeMark.getDateString(), null, null);
		    	  
		    	  //Add StyleUrl tag
		    	  if(this.styleObj != null)
		    	  {
		    		  this.addNewElement(plm, "styleUrl", "#" + this.styleObj.getId(), null, null);
		    	  }
		    	  else
		    		  System.err.println("Error Style not available !");
		    	  
		    	  //Add Point tag with coordinates
		    	  Element point = this.addNewElement(plm, "Point", null, null, null);
		    	      
		    	  this.addNewElement(point, "coordinates", placeMark.getGp().getLon()+","+placeMark.getGp().getLat(), null, null);
		      }
	    	  
	      }
	      
		 
	 }
	 
	 public Element addNewElement(Element rootElement, String tagName, String content, String attributeName, String attributeValue)
	 {
		  //System.out.println("Adding new Element ...");
		 
		  Element ele = doc.createElement(tagName);
	      
		  if(content != null)
			  ele.setTextContent(content);
	      
		  if(attributeName != null && attributeValue != null)
			  ele.setAttribute(attributeName, attributeValue);
		  
		  rootElement.appendChild(ele);
		  
		  return ele;
	 }
	 
	 public String createKMLString()
	     throws javax.xml.parsers.ParserConfigurationException,
	            javax.xml.transform.TransformerException,
	            javax.xml.transform.TransformerConfigurationException
	 {     
	
		   // transform the Document into a String
	       DOMSource domSource = new DOMSource(doc);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	       transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	       transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	       transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	       java.io.StringWriter sw = new java.io.StringWriter();
	       StreamResult sr = new StreamResult(sw);
	       transformer.transform(domSource, sr);
	       String xml = sw.toString();
	       return xml;
   }
	 
	 public void writeKMLFile(String fileName)
	 {
	      try{
	    	  // Create file 
	    	  FileWriter fstream = new FileWriter(fileName);
	    	  BufferedWriter out = new BufferedWriter(fstream);
	    	  this.generateKMLDocument("name","KML description");
	    	  out.write( this.createKMLString());
	    	  //Close the output stream
	    	  out.close();
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	  } 
	 
   public static void main(String args[]){
		 try {
			
			 KmlManager kmlManager = new KmlManager();
			 kmlManager.setLookAt(new LookAt(new GeographicPoint(4.5, 7.8), "range", "tilt", "heading"));
			 kmlManager.setStyle(new Style("geoKadStyle","http://www.ce.unipr.it/~picone/marker26.png"));
			 kmlManager.generateKMLDocument("name","KML description");
			 	
			 System.out.println(kmlManager.createKMLString());
		
		 } catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

   public boolean addPlaceMark(PlaceMark pm)
   {
	  return this.pmList.add(pm);   
   }
   
	public ArrayList<PlaceMark> getPmList() {
		return pmList;
	}

	public void setPmList(ArrayList<PlaceMark> pmList) {
		this.pmList = pmList;
	}

	public DocumentBuilderFactory getFactory() {
		return factory;
	}

	public void setFactory(DocumentBuilderFactory factory) {
		this.factory = factory;
	}

	public DocumentBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(DocumentBuilder builder) {
		this.builder = builder;
	}

	public DOMImplementation getImpl() {
		return impl;
	}

	public void setImpl(DOMImplementation impl) {
		this.impl = impl;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public LookAt getLookAt() {
		return lookAt;
	}

	public void setLookAt(LookAt la) {
		this.lookAt = la;
	}

	public Style getStyle() {
		return styleObj;
	}

	public void setStyle(Style style) {
		this.styleObj = style;
	}

	public Style getStyleObj() {
		return styleObj;
	}

	public void setStyleObj(Style styleObj) {
		this.styleObj = styleObj;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}
}
