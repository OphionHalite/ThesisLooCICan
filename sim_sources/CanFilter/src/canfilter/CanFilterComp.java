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

package canfilter;

import java.util.Arrays;
import java.util.HashMap;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.impl.property.PropertyByte;
import looci.osgi.serv.impl.property.PropertyInteger;
import looci.osgi.serv.util.ITimeListener;
import looci.osgi.serv.util.LoociTimer;
import looci.osgi.serv.util.Utils;

/**
 * 
 *
 * @author Mathijs Delaere
 * @version 1.0
 * @since 2018-03-21
 *
 */

public class CanFilterComp extends LoociComponent /*implements ITimeListener*/{
	
    /**
     * Holds the parent codebase
     */
    private CanFilter _parent;
    private CanReceiver canRecv;
    private PayloadBuilder pb;
    private PropertyByte filterOn;
    private PropertyByte filterAdd;
    private PropertyByte filterDel;
    private PropertyByte filtersClr;
    
   	private Thread canthread;
   	boolean isrunningcanthread;
   	int id;

	/**
	 * LooCIComponent(<name>, <provided interfaces>, <required interfaces>);
	 */
    public CanFilterComp(CanFilter parent) {
        _parent = parent;
        canRecv = new CanReceiver(this);
        filterOn = new PropertyByte((short)1, "Filter on/off: 1 is on, 0 is off", (byte)1);
        filterAdd = new PropertyByte((short)2, "Add a filter CANId to the list", (byte)-1);
        filterDel = new PropertyByte((short)3, "Delete a filter CANId", (byte)-1);
		filtersClr = new PropertyByte((short)4, "Clear CAN Filters, set to 1 to clear all filters", (byte)0);
		this.addProperty(filterOn);
        this.addProperty(filterAdd);
        this.addProperty(filterDel);
        this.addProperty(filtersClr);
        
        this.canthread = new Thread(new Runnable() {
			public void run(){
				while(isrunningcanthread){
					byte[] data = canRecv.getData();
					//long ctime = 0;
					System.out.println("[CanFilter] Received Message");
					id = canRecv.checkFilter();
					if(id >= 0) {
						//System.out.println("[CanFilter] Filter found");
						pb = new PayloadBuilder();						
						pb.addString(String.valueOf(System.currentTimeMillis()));
						pb.addInteger(id);	//identifier
						pb.addByte((byte)8);			//length
						pb.addString(Arrays.toString(data));
						publish(CanFilter.CAN_EVENT, pb.getPayload());
					}else {
						//System.out.println("[CanFilter] Filter not found");
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
    }

    @Override
    public void receive(short event_id, byte[] payload) {		
        // handle receptacles
    }
    
    
    public void sendCanMessage(String s) {
    	System.out.println("[CanFilter] Sending Message");
    	pb = new PayloadBuilder();
		pb.addString(s);
    	Event e = new Event(CanFilter.CAN_EVENT, pb.getPayload());
    	publish(e);
    }
    
    @Override
    public void componentStart() {
        // called by looci:activate
    	System.out.println("[CanFilter] Starting");
    	//timer.startRunning();
    	isrunningcanthread = true;
    	canthread.start();		
    }

    @Override
    public void componentStop() {
    	//timer.stopRunning();
    	// called by looci:deactivate
    	canRecv.stop();
    	isrunningcanthread = false;
    }
    
	protected void componentAfterProperty(short propertyId) throws LoociManagementException{
		if(propertyId == filterOn.getPropertyId()){
			if(filterOn.getVal()==1){
				canRecv.setFilterOn();
			}else{
				canRecv.setFilterOff();
			}
		}
		if(propertyId == filterAdd.getPropertyId()){
			canRecv.addFilter((int) filterAdd.getVal());
		}
		if(propertyId == filterDel.getPropertyId()){
			canRecv.addFilter((int) filterDel.getVal());
		}
		if(propertyId == filtersClr.getPropertyId()){
			canRecv.clearFilters();
			filtersClr.setVal((byte)0);
		}
	}
}
