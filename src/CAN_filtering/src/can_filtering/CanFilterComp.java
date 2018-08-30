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

package can_filtering;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.LoociComponent;
import looci.osgi.serv.impl.PayloadBuilder;
import looci.osgi.serv.util.Utils;
import looci.osgi.serv.impl.property.PropertyInteger;
import looci.osgi.serv.impl.property.PropertyString;

import de.entropia.can.CanSocket;
import de.entropia.can.CanSocket.Mode;
import de.entropia.can.CanSocket.CanFrame;
import de.entropia.can.CanSocket.CanId;
import de.entropia.can.CanSocket.CanInterface;


/**
 * 
 *
 * @author 
 * @version 1.0
 * @since 2012-01-01
 *
 */

public class CanFilterComp extends LoociComponent {
	
    /**
     * Holds the parent codebase
     */
    private CanFilter _parent;
    private CanReceiver canRecv;
    private PayloadBuilder pb;
    private PropertyInteger filterOn;
    private PropertyInteger filterAdd;
    private PropertyInteger filterDel;
    private PropertyInteger filtersClr;


	/**
	 * LooCIComponent(<name>, <provided interfaces>, <required interfaces>);
	 */
    public CanFilterComp(CanFilter parent) {
        _parent = parent;
        canRecv = new CanReceiver(this);
        filterOn = new PropertyInteger((short)1,"Fitler on/off: 1 is on, 0 is off",0);
        filterAdd = new PropertyInteger((short)2,"Add a filter CANId to the list",-1);
        filterDel = new PropertyInteger((short)3,"Delete a filter CANId",-1);
        filtersClr = new PropertyInteger((short)4,"Clear CAN Filters, setting to 1 clears all filters",0);
        this.addProperty(filterOn);
        this.addProperty(filterAdd);
        this.addProperty(filterDel);
        this.addProperty(filtersClr);
    }

    @Override
    public void receive(short event_id, byte[] payload) {		
        // handle receptacles
    }
    
    @Override
    public void componentStart() {
        // called by looci:activate
    	canRecv.start();
    }

    @Override
    public void componentStop() {
    	// called by looci:deactivate
    	try {
			canRecv.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public void sendCanMessage(CanFrame recvMessage) {
		pb = new PayloadBuilder();
		pb.addString(String.valueOf(System.currentTimeMillis()));
		pb.addString(""+ (recvMessage.getCanId().isSetEFFSFF()?recvMessage.getCanId().getCanId_EFF():recvMessage.getCanId().getCanId_SFF()));
		pb.addString(Arrays.toString(recvMessage.getData()));
		this.publish(CanFilter.CAN_EVENT, pb.getPayload());
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
			canRecv.addFilter(filterAdd.getVal());
		}
		if(propertyId == filterDel.getPropertyId()){
			canRecv.addFilter(filterDel.getVal());
		}
		if(propertyId == filtersClr.getPropertyId()){
			canRecv.clearFilters();
			filtersClr.setVal(0);
		}
	}


}
