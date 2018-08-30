package canbackendoriginal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.text.DateFormat;
import java.util.Date;


public class CANViewer extends JFrame {

    private javax.swing.JTextArea dataTextArea;
    private javax.swing.JScrollPane jScrollPane1;
	

    public CANViewer() {
        initComponents();
        this.setVisible(true);
    }

    public CANViewer(String ieee) {
        initComponents();
        setTitle(ieee);
    }

	private void initComponents() {
		// TODO Auto-generated method stub
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();
        
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(600, 800));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 800));
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });
        
        dataTextArea.setColumns(40);
        dataTextArea.setEditable(false);
        dataTextArea.setRows(50);
        jScrollPane1.setViewportView(dataTextArea);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(jScrollPane1);
        
        getContentPane().add(panel,java.awt.BorderLayout.SOUTH);
        pack();
	}
	
	public void appendText(String str){
		this.dataTextArea.append(str + "\n");
	}
}
