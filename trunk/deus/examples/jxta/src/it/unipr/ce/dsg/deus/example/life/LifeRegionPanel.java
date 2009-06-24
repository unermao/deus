package it.unipr.ce.dsg.deus.example.life;

import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LifeRegionPanel extends javax.swing.JPanel {
	
	private ArrayList<javax.swing.JPanel> cells = null;	
	
	public LifeRegionPanel(int regionSide) {
		
		cells = new ArrayList<javax.swing.JPanel>();
		
		
        org.jdesktop.layout.GroupLayout LifeRegionPanelLayout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(LifeRegionPanelLayout);
        LifeRegionPanelLayout.setHorizontalGroup(
        	LifeRegionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        LifeRegionPanelLayout.setVerticalGroup(
        	LifeRegionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
		
		java.awt.GridBagConstraints gridBagConstraints;

		this.setBackground(new java.awt.Color(0, 0, 0));
		this.setLayout(new java.awt.GridBagLayout());
		
        for(int j=0; j<regionSide*regionSide; ++j){
        	JPanel cellPanel = new javax.swing.JPanel();
		
        	cellPanel.setBackground(new java.awt.Color(255, 255, 255));

		
        	org.jdesktop.layout.GroupLayout cellPanelLayout = new org.jdesktop.layout.GroupLayout(cellPanel);
        	cellPanel.setLayout(cellPanelLayout);
        	cellPanelLayout.setHorizontalGroup(
        		cellPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        		.add(0, 3, Short.MAX_VALUE)
        	);
        	cellPanelLayout.setVerticalGroup(
        		cellPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        		.add(0, 3, Short.MAX_VALUE)
        	);

	        gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridx = j%regionSide;
	        gridBagConstraints.gridy = j/regionSide;
	        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
	        this.add(cellPanel, gridBagConstraints);
	        cells.add(cellPanel);
        }
	}
	
	public void updateGrid(int grid[]) {
		for(int i=0; i<grid.length; ++i) {
			if(grid[i] == 1)
				cells.get(i).setBackground(new java.awt.Color(0, 0, 0));
			else
				cells.get(i).setBackground(new java.awt.Color(255, 255, 255));
		}
	}

}
