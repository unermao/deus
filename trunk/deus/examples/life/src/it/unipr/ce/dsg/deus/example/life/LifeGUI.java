package it.unipr.ce.dsg.deus.example.life;

import it.unipr.ce.dsg.deus.core.Engine;

@SuppressWarnings("serial")
public class LifeGUI extends javax.swing.JFrame {

    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel vtLabelTitle;
    public javax.swing.JLabel vtLabelValue;
    private javax.swing.JLabel comunicationCountTitle;
    public javax.swing.JLabel comunicationCountValue;
    
	public LifeGUI(int numberOfRegions) {
		super();
		initComponents(numberOfRegions);
		setVisible(true);
	}
	
	public static void main(String args[]) {
		LifeGUI l = new LifeGUI(4);
		l.setVisible(true);
	}
	
	private void initComponents(int numberOfRegions) {
		
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        vtLabelTitle = new javax.swing.JLabel();
        vtLabelValue = new javax.swing.JLabel();
        comunicationCountTitle = new javax.swing.JLabel();
        comunicationCountValue = new javax.swing.JLabel();
            
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LIFE Game");

        vtLabelTitle.setText("VT = ");
        vtLabelValue.setText("0");
        comunicationCountTitle.setText("Inter-Node comunications = ");
        comunicationCountValue.setText("0");       
        
        mainPanel.setLayout(new java.awt.GridBagLayout());
        statusPanel.add(vtLabelTitle);
        statusPanel.add(vtLabelValue);
        statusPanel.add(comunicationCountTitle);
        statusPanel.add(comunicationCountValue);
        
        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
        		statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(statusPanelLayout.createSequentialGroup()
	                    .add(vtLabelTitle)
	                    .add(vtLabelValue))
                    .add(statusPanelLayout.createSequentialGroup()
                    	.add(comunicationCountTitle)
                    	.add(comunicationCountValue))))
        );
        statusPanelLayout.setVerticalGroup(
        		statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(vtLabelTitle)
                    .add(vtLabelValue))
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                	.add(comunicationCountTitle)
                    .add(comunicationCountValue)))
        );
        
        
        int gridSide = (int)Math.sqrt(numberOfRegions);
        
		for(int i=0; i< numberOfRegions; ++i) {
			LifeRegionPanel lrp = ((LifeRegion)Engine.getDefault().getNodes().get(i)).regionPanel;
			       
	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = i%gridSide;
	        gridBagConstraints.gridy = i/gridSide;
	        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
	        
	        mainPanel.add(lrp, gridBagConstraints);
		}

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
        		layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
             .add(layout.createSequentialGroup()
            		 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                 .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                 .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                 .addContainerGap())
        );
            
        layout.setVerticalGroup(
        		layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(statusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
	}
}
