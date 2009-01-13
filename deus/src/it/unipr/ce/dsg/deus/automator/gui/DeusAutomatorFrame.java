/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeusAutomator.java
 *
 * Created on 7-gen-2009, 12.41.34
 */

package it.unipr.ce.dsg.deus.automator.gui;

import it.unipr.ce.dsg.deus.automator.DeusAutomatorException;
import it.unipr.ce.dsg.deus.automator.MyObjectEngine;
import it.unipr.ce.dsg.deus.automator.MyObjectGnuplot;
import it.unipr.ce.dsg.deus.automator.MyObjectNode;
import it.unipr.ce.dsg.deus.automator.MyObjectParam;
import it.unipr.ce.dsg.deus.automator.MyObjectProcess;
import it.unipr.ce.dsg.deus.automator.MyObjectResourceParam;
import it.unipr.ce.dsg.deus.automator.MyObjectSimulation;
import it.unipr.ce.dsg.deus.automator.Runner;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author marcopk
 */
public class DeusAutomatorFrame extends javax.swing.JFrame {

    private int simulationCount = 0;
	private JLabel removeSimulationLabel;
	private String originalXmlPath;
	private String outFileName;
	/** Creates new form DeusAutomator */
    public DeusAutomatorFrame() {
        initComponents();
    }

    public DeusAutomatorFrame(String originalXmlPath, String outFileName) {
		this.originalXmlPath = originalXmlPath;
		this.outFileName = outFileName;
		
		initComponents();
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

    	this.setTitle("Deus Automator - DSG Parma");
    	
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
        dsgLogoLabel = new javax.swing.JLabel();
        simulationTabbedPane = new javax.swing.JTabbedPane();
        openLabel = new javax.swing.JLabel();
        saveLabel = new javax.swing.JLabel();
        addSimulationLabel = new javax.swing.JLabel();
        simulationProgressBar = new javax.swing.JProgressBar();
        simulationStatusLabel = new javax.swing.JLabel();
        runLabel = new javax.swing.JLabel();
        removeSimulationLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setForeground(java.awt.Color.white);
        setResizable(false);

        dsgLogoLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        dsgLogoLabel.setForeground(new java.awt.Color(204, 0, 0));
        dsgLogoLabel.setIcon(new javax.swing.ImageIcon(("res/dsgLogo_noBack_small.png"))); // NOI18N
        dsgLogoLabel.setText(" - Deus Automator");

      
        simulationTabbedPane.addTab("tab1", new DeusSimulationPanel(simulationTabbedPane));

        openLabel.setIcon(new javax.swing.ImageIcon(("res/open.png"))); // NOI18N
        openLabel.setText("Open");
        openLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                openLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openLabelMouseClicked(evt);
            }
        });

        saveLabel.setIcon(new javax.swing.ImageIcon(("res/save.png"))); // NOI18N
        saveLabel.setText("Save");
        saveLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                saveLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveLabelMouseClicked(evt);
            }
        });

        addSimulationLabel.setIcon(new javax.swing.ImageIcon(("res/add.png"))); // NOI18N
        addSimulationLabel.setText("Add Simulation Tab");
        addSimulationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addSimulationLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addSimulationLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSimulationLabelMouseClicked(evt);
            }
        });

        simulationStatusLabel.setText("Simulation Status :");

        removeSimulationLabel.setIcon(new javax.swing.ImageIcon("res/remove.png")); // NOI18N
        removeSimulationLabel.setText("Remove Simulation Tab");
        removeSimulationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                removeSimulationLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                removeSimulationLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                removeSimulationLabelMouseClicked(evt);
            }
        });
        
        runLabel.setIcon(new javax.swing.ImageIcon(("res/run.png"))); // NOI18N
        runLabel.setText("Run");
        runLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                runLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                runLabelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
					runLabelMouseClicked(evt);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(simulationTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 979, Short.MAX_VALUE)
                    .add(dsgLogoLabel)
                    .add(layout.createSequentialGroup()
                        .add(openLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addSimulationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeSimulationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(runLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 276, Short.MAX_VALUE)
                        .add(simulationStatusLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(simulationProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 261, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(dsgLogoLabel)
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(openLabel)
                            .add(saveLabel)
                            .add(addSimulationLabel)
                            .add(simulationStatusLabel)
                            .add(removeSimulationLabel)
                            .add(runLabel)))
                    .add(simulationProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(simulationTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 510, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected void removeSimulationLabelMouseClicked(MouseEvent evt) {
    	int index = this.simulationTabbedPane.getSelectedIndex();
    	this.simulationTabbedPane.remove(index);
	}

	protected void removeSimulationLabelMouseReleased(MouseEvent evt) {
		removeSimulationLabel.setIcon(new javax.swing.ImageIcon("res/remove.png"));
		
	}

	protected void removeSimulationLabelMousePressed(MouseEvent evt) {
		removeSimulationLabel.setIcon(new javax.swing.ImageIcon("res/remove_BN.png"));
		
	}

	private void openLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openLabelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_openLabelMouseClicked

    private void openLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openLabelMousePressed
    	openLabel.setIcon(new javax.swing.ImageIcon(("res/open_BN.png")));
    }//GEN-LAST:event_openLabelMousePressed

    private void openLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openLabelMouseReleased
    	openLabel.setIcon(new javax.swing.ImageIcon(("res/open.png")));
    }//GEN-LAST:event_openLabelMouseReleased

    private void saveLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveLabelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_saveLabelMouseClicked

    private void saveLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveLabelMousePressed
    	saveLabel.setIcon(new javax.swing.ImageIcon(("res/save_BN.png")));
    }//GEN-LAST:event_saveLabelMousePressed

    private void saveLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveLabelMouseReleased
    	saveLabel.setIcon(new javax.swing.ImageIcon(("res/save.png")));
    }//GEN-LAST:event_saveLabelMouseReleased

    private void addSimulationLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSimulationLabelMouseClicked
      simulationCount++;
   	  simulationTabbedPane.addTab("Sim" + simulationCount, new DeusSimulationPanel(simulationTabbedPane));
    }//GEN-LAST:event_addSimulationLabelMouseClicked

    private void addSimulationLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSimulationLabelMousePressed
    	addSimulationLabel.setIcon(new javax.swing.ImageIcon(("res/add_BN.png")));
    }//GEN-LAST:event_addSimulationLabelMousePressed

    private void addSimulationLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addSimulationLabelMouseReleased
    	addSimulationLabel.setIcon(new javax.swing.ImageIcon(("res/add.png")));
    }//GEN-LAST:event_addSimulationLabelMouseReleased

    private void runLabelMouseClicked(java.awt.event.MouseEvent evt) throws IOException {//GEN-FIRST:event_runLabelMouseClicked
    	int tabCount = simulationTabbedPane.getTabCount();
    	
		String xmlString = "";
		
		xmlString = xmlString + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n\n";
		xmlString = xmlString + "<deusAutomator xmlns=\"http://dsg.ce.unipr.it/software/deus/schema/deusAutomator\""
			 		+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
				 	" xsi:schemaLocation=\"http://dsg.ce.unipr.it/software/deus/schema/deusAutomator ../../schema/automator/deusAutomator.xsd\">" + "\n\n";
		
		for(int i=0; i < tabCount; i++)
			xmlString = xmlString + ((DeusSimulationPanel)simulationTabbedPane.getComponent(i)).createSimulationXML(simulationTabbedPane.getTitleAt(i),i) + "\n";
	
		xmlString = xmlString + "</deusAutomator>";
		
	//	System.out.println(xmlString);
		
		FileOutputStream fos = new FileOutputStream(this.outFileName);
		
		fos.write(xmlString.getBytes());
		
		Runner runner = new Runner(this.originalXmlPath, this.outFileName);
		runner.setSimulationProgressBar(simulationProgressBar);
		
		Thread automatorRunner = new Thread(runner, "Media Controller Listener");
		automatorRunner.start();
    }//GEN-LAST:event_runLabelMouseClicked

    private void runLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runLabelMousePressed
    	runLabel.setIcon(new javax.swing.ImageIcon(("res/run_BN.png")));
    }//GEN-LAST:event_runLabelMousePressed

    private void runLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_runLabelMouseReleased
    	runLabel.setIcon(new javax.swing.ImageIcon(("res/run.png")));
    }//GEN-LAST:event_runLabelMouseReleased

    /**
    * @param args the command line arguments
    */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
            	DeusAutomatorFrame deusAutomatorFrame =  new DeusAutomatorFrame(args[0],args[1]);
            	deusAutomatorFrame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addSimulationLabel;
    private javax.swing.JLabel dsgLogoLabel;
    private javax.swing.JLabel openLabel;
    private javax.swing.JLabel runLabel;
    private javax.swing.JLabel saveLabel;
    private javax.swing.JProgressBar simulationProgressBar;
    private javax.swing.JLabel simulationStatusLabel;
    private javax.swing.JTabbedPane simulationTabbedPane;
    // End of variables declaration//GEN-END:variables
	public String getOriginalXmlPath() {
		return originalXmlPath;
	}

	public void setOriginalXmlPath(String originalXmlPath) {
		this.originalXmlPath = originalXmlPath;
	}

	public String getOutFileName() {
		return outFileName;
	}

	public void setOutFileName(String outFileName) {
		this.outFileName = outFileName;
	}

	/**
	 * Funzione che si occupa di leggere il file .xml per l'automazione delle simulazioni
	 * @param path, percorso del file da leggere
	 * @return un ArrayList<> contenete tutte le varie simulazioni da effettuare
	 * @throws DeusAutomatorException
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	private  void readXML(String path) throws DeusAutomatorException, JAXBException, SAXException, IOException{
		
		
		JAXBContext jc = JAXBContext.newInstance("it.unipr.ce.dsg.deus.schema.automator");
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = schemaFactory
				.newSchema(new File("schema/automator/deusAutomator.xsd"));
		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setSchema(schema);
		unmarshaller.setEventHandler(new ValidationEventHandler() {

			public boolean handleEvent(ValidationEvent ve) {
				if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
						|| ve.getSeverity() == ValidationEvent.ERROR
						|| ve.getSeverity() == ValidationEvent.WARNING) {
					ValidationEventLocator locator = ve.getLocator();
					System.out.println("Invalid configuration file: "
							+ locator.getURL());
					System.out.println("Error at column "
							+ locator.getColumnNumber() + ", line "
							+ locator.getLineNumber());
					System.out.println("Error: " + ve.getMessage());
					return false;
				}
				return true;
			}

		});
				
		unmarshaller.unmarshal(new File(path));
				
		
		try {

			DocumentBuilderFactory factory =
			      DocumentBuilderFactory.newInstance();
			
			 File f = new File(path);
			 
		     DocumentBuilder builder = factory.newDocumentBuilder();
		      
		    Document document = builder.parse(f);		      
			
			document.getDocumentElement().normalize();
			
			//Elemento root
			NodeList simulationLst = document.getElementsByTagName("simulation");
						
			
			//LISTA DELLE SIMULAZIONI
			for (int w = 0; w < simulationLst.getLength(); w++) {																									
				
				Node fstSimulation = simulationLst.item(w);

				if(fstSimulation.getAttributes().getNamedItem("simulationName").getNodeValue() == null || fstSimulation.getAttributes().getNamedItem("simulationNumberSeed").getNodeValue() == null)
					throw new DeusAutomatorException("Errore manca simulationNumberSeed e/o simulationName nel tag simulation");

//				String resultFolder = null;
//				String inputFolder = null;
//				
//				String simulationNumberSeed = fstSimulation.getAttributes().getNamedItem("simulationNumberSeed").getNodeValue();
				
				// NOME DELLA SIMULAZIONE
				String simulationName = fstSimulation.getAttributes().getNamedItem("simulationName").getNodeValue();
				
				
				
//				if(fstSimulation.getAttributes().getNamedItem("resultFolder") != null)
//					resultFolder = fstSimulation.getAttributes().getNamedItem("resultFolder").getNodeValue();
//				
//				if(fstSimulation.getAttributes().getNamedItem("inputFolder") != null)
//					inputFolder = fstSimulation.getAttributes().getNamedItem("inputFolder").getNodeValue();
				
								
								
				
			NodeList nodeLst = document.getElementsByTagName("node");
												
			//LISTA DEI NODI
			for (int s = 0; s < nodeLst.getLength(); s++) {				
				
				Node fstNode = nodeLst.item(s);
				
				if(fstNode.getParentNode().equals(simulationLst.item(w))){									
					
				//NOME DEL NODO
				String messageType = fstNode.getAttributes().getNamedItem("id").getNodeValue();							

				Element fstElmnt = (Element) fstNode;
				NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("paramName");							
				
				//Ricavo tutti i parametri in ParamName di node
				for( int j = 0 ; j < fstNmElmntLst.getLength() ; j++)
				{										

					Element paramElement = (Element)fstNmElmntLst.item(j);

					//NOME DEL PARAMETRO
					String paramName  = ((Node) fstNmElmntLst.item(j)).getAttributes().getNamedItem("name").getNodeValue();															

					NodeList initialValue = paramElement.getElementsByTagName("initialValue");

					NodeList finalValue = paramElement.getElementsByTagName("finalValue");
	
					NodeList stepValue = paramElement.getElementsByTagName("stepValue");

					if(initialValue == null || finalValue == null || stepValue == null)
					{
					throw new DeusAutomatorException("Errore in initalValue , finalValue e stepValue in " + simulationName + " di Node " + messageType + " in " + paramName);	
					}
					
					//VALORE PARAMETRI
					String init = initialValue.item(0).getTextContent();
					String fin = finalValue.item(0).getTextContent();
					String step = stepValue.item(0).getTextContent();									
					
				}
				
				
				NodeList paramName = fstElmnt.getElementsByTagName("resourceParamName");							

				//Ricavo tutti i parametri in resourceParamName di node 
				for( int j = 0 ; j < paramName.getLength() ; j++)
				{				
					
					Element paramElement = (Element)paramName.item(j);

					//NOME HANDLER
					String handlerName  = ((Node) paramName.item(j)).getAttributes().getNamedItem("handlerName").getNodeValue();
					
					//NOME PARAMETRO RESOURCE
					String resParamValueName  = ((Node) paramName.item(j)).getAttributes().getNamedItem("resParamValue").getNodeValue();

					NodeList initialValue = paramElement.getElementsByTagName("initialValue");

					NodeList finalValue = paramElement.getElementsByTagName("finalValue");
					
					NodeList stepValue = paramElement.getElementsByTagName("stepValue");
					
					if(initialValue == null || finalValue == null || stepValue == null)
					{
					throw new DeusAutomatorException("Errore in initalValue , finalValue e stepValue in " + simulationName + " di Node" + messageType + " in " + paramName );	
					}
					
					//VALORE PARAMETRI
					String init = initialValue.item(0).getTextContent();
					String fin = finalValue.item(0).getTextContent();
					String step = stepValue.item(0).getTextContent();						
					}
					
				}
						
			}	
			
			NodeList processLst = document.getElementsByTagName("process");						

			//LISTA DEI PROCESSI
			for (int s = 0; s < processLst.getLength(); s++) {
			
				Node fstNode = processLst.item(s);								
			
				if(fstNode.getParentNode().equals(simulationLst.item(w))){																	
					
				// NOME PROCESSO
				String messageType = fstNode.getAttributes().getNamedItem("id").getNodeValue();

				Element fstElmnt = (Element) fstNode;
				NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("paramName");
								
				//Ricavo tutti i parametri in ParamName di process
				for( int j = 0 ; j < fstNmElmntLst.getLength() ; j++)
				{				
					
					Element paramElement = (Element)fstNmElmntLst.item(j);

					//NOME PARAMETRO PROCESSO
					String paramName  = ((Node) fstNmElmntLst.item(j)).getAttributes().getNamedItem("name").getNodeValue();
					
					NodeList initialValue = paramElement.getElementsByTagName("initialValue");
					
					NodeList finalValue = paramElement.getElementsByTagName("finalValue");
					
					NodeList stepValue = paramElement.getElementsByTagName("stepValue");
					
					//VALORE PARAMETRI
					String init = initialValue.item(0).getTextContent();
					String fin = finalValue.item(0).getTextContent();
					String step = stepValue.item(0).getTextContent();
					
					}
					
				}							
				
			}
			
			NodeList engineLst = document.getElementsByTagName("engine");						

			//ENGINE
			for (int s = 0; s < engineLst.getLength(); s++) {
							
				Node fstNode = engineLst.item(s);
				
				if(fstNode.getParentNode().equals(simulationLst.item(w))){

				String startVt = "";
				String endVt = "";
				String stepVt = "";
								
				boolean vt = true;
				
				if(fstNode.getAttributes().getNamedItem("startVT") != null )	
					startVt = fstNode.getAttributes().getNamedItem("startVT").getNodeValue();
				
				else vt = false;
				
				if(fstNode.getAttributes().getNamedItem("endVT") != null )	
					endVt = fstNode.getAttributes().getNamedItem("endVT").getNodeValue();
				 
				else vt = false;
				
				if(fstNode.getAttributes().getNamedItem("stepVT") != null )
					stepVt = fstNode.getAttributes().getNamedItem("stepVT").getNodeValue();
				
				else vt = false;
				
								
				Element fstElmnt = (Element) fstNode;
				NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("seed");

				//Ricavo tutti i seedValue presenti in seed
				for( int j = 0 ; j < fstNmElmntLst.getLength() ; j++)
				{					
					
					Element paramElement = (Element)fstNmElmntLst.item(j);
								
					NodeList seedValue = paramElement.getElementsByTagName("seedValue");
										
					for( int o = 0 ; o < seedValue.getLength() ; o++)
					{						
						// VALORI DEI SEED
						String seedvalue = seedValue.item(o).getTextContent();										
					}									

				}
				
				}	
			
			}			
						
//			NodeList resultLogLst = document.getElementsByTagName("resultVT");						
//			
//			for (int i = 0; i < resultLogLst.getLength(); i++) {			
//				
//				Node fileLog = resultLogLst.item(i);
//				
//				if(fileLog.getParentNode().equals(simulationLst.item(w))){									
//					
//					sim.setFileLog(fileLog.getAttributes().getNamedItem("outputLogFile").getNodeValue());
//				}			
//		
//			}
//			
			//GNUPLOT
			NodeList GnuPlotLst = document.getElementsByTagName("resultXYFile");						

			for (int i = 0; i < GnuPlotLst.getLength(); i++) {			
				
				Node GnuPlotNode = GnuPlotLst.item(i);
				
				if(GnuPlotNode.getParentNode().equals(simulationLst.item(w))){
																			
					String fileName = GnuPlotNode.getAttributes().getNamedItem("fileName").getNodeValue();
					
					String asseX = GnuPlotNode.getAttributes().getNamedItem("axisX").getNodeValue();
					
					String asseY = GnuPlotNode.getAttributes().getNamedItem("axisY").getNodeValue();
										
				}			
		
			}															
			
			}						
			
	}
	 catch (SAXException e) {

		e.printStackTrace();
	} catch (IOException e) {

		e.printStackTrace();
	} catch (ParserConfigurationException e) {

		e.printStackTrace();
	}

}

	
}
