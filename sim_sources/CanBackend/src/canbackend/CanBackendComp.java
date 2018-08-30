/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
/*
 * Copyright (c) 2012, Katholieke Universiteit Leuven
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Katholieke Universiteit Leuven nor the names of
 *       its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package canbackend;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.util.Utils;

/**
 * 
 *
 * @author 
 * @version 1.0
 * @since 2012-01-01
 *
 */

public class CanBackendComp extends LoociComponent {
	
    /**
     * Holds the parent codebase
     */
    private CanBackend _parent;
    private JTextArea status;
    private String[] addresses = new String[8];
    private DataWindow[] plots = new DataWindow[8];
    private JFrame fr;
    private PayloadBuilder pb;
    private int value = 0;

	/**
	 * LooCIComponent(<name>, <provided interfaces>, <required interfaces>);
	 */
    public CanBackendComp(CanBackend parent) {
        _parent = parent;
    }

	@Override
	public void receive(short eventID, byte[] payload) {
		if(eventID == CanBackend.CAN_EVENT){
			System.out.println("[CanBackend]received CAN event");
		} else{
			System.out.println("received invalid event");
		}
		
		Event event = getReceptionEvent();
		if(event.getEventID() == CanBackend.CAN_EVENT){
	        pb = new PayloadBuilder(event.getPayload());
        	value = pb.getIntegerAt(1);
	        DataWindow dw = findPlot(event.getSourceAddress());
	        long time = System.currentTimeMillis();      // read time of the reading
	        dw.addData(time, value);
		}
	}
	
    private DataWindow findPlot(String addr) {
        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i].equals("")) {                
                status.append("Received packet from : " + addr + "\n");
                addresses[i] = addr;
                plots[i] = new DataWindow(addr);
                final int ii = i;
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        plots[ii].setVisible(true);
                    }
                });
                return plots[i];
            }
            if (addresses[i].equals(addr)) {
                return plots[i];
            }
        }
        return plots[0];
    }
    
	protected void componentCreate(){
		System.out.println("created instance");
	    fr = new JFrame("CanData");
        status = new JTextArea();
        JScrollPane sp = new JScrollPane(status);
        fr.add(sp);
        fr.setSize(360, 200);
        fr.validate();
        fr.setVisible(true);
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = "";
            plots[i] = null;
        }
	}

	protected void componentDestroy(){
		System.out.println("destroying component");
		for(int i =0 ; i < plots.length ; i++){
			if(plots[i] != null){
				plots[i].setVisible(false);
				plots[i].dispose();
	            addresses[i] = "";
				plots[i] = null;
			}
		}
		fr.setVisible(false);
		fr.dispose();
		fr = null;
	}
}
