/*
 * Copyright 2016 Microprofile.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microprofile.showcase.vote.persistence.couch;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.microprofile.showcase.vote.api.PersistenceProvider;
import io.microprofile.showcase.vote.api.PersistenceTypes;
import io.microprofile.showcase.vote.model.Attendee;
import io.microprofile.showcase.vote.persistence.AttendeeDAO;
import io.microprofile.showcase.vote.persistence.NonPersistent;
import io.microprofile.showcase.vote.persistence.Persistent;
import io.microprofile.showcase.vote.persistence.couch.CouchConnection.RequestType;
import io.microprofile.showcase.vote.persistence.couch.beans.ConnectionBean;
import io.microprofile.showcase.vote.utils.ConnectException;

@ApplicationScoped
@Persistent
public class CouchAttendeeDAO implements AttendeeDAO {
	
    @Inject
    CouchConnection couch;
    
    private @Inject PersistenceProvider persistenceProvider;
    private String dbName="attendees";
    private @Inject @NonPersistent AttendeeDAO hashMapAttendeeDAO;
    private @Inject ConnectionBean connectionBean;

    private String allView = "function (doc) {emit(doc._id, 1)}";

    private String designDoc = "{\"views\":{"
                               + "\"all\":{\"map\":\"" + allView + "\"}}}";

    private boolean connected;
    private int executionCounter = 0;
    
    
	/*@PostConstruct
	public void connect() {
		try {
			this.connected = couch.connect("attendees");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (this.connected) {
			String design = couch.request("_design/attendees", RequestType.GET, null, String.class, null, 200, true);
			if (design == null) {
				couch.request("_design/attendees", RequestType.PUT, designDoc, null, null, 201);
			}
		}
	}*/

	@PostConstruct
	public void connect() {
		try {
			this.connected=connectionBean.connect(dbName, couch);
		} catch (ConnectException e) {
			System.out.println("The message from connect Exception : "+e.getMessage() +" AttendeeDAO");
			e.printStackTrace();
		}
		if (persistenceProvider.getPersistenceType().equals(PersistenceTypes.PERSISTENCE_NO_SEQL_DB)
				&& persistenceProvider.isAvailable()) {
			String design = couch.request("_design/attendees", RequestType.GET, null, String.class, null, 200, true);
			if (design == null) {
				couch.request("_design/attendees", RequestType.PUT, designDoc, null, null, 201);
			}
		}else{
			this.connected=false;
		}
	}

    @Override
    public Attendee createNewAttendee(Attendee attendee) {
    	
    	/*original
        CouchID attendeeID = couch.request(null, RequestType.POST, attendee, CouchID.class, null, 201);
        Attendee returnedAttendee= couch.request(null, RequestType.POST, attendee, Attendee.class, null, 201);
        attendee = getAttendee(attendeeID.getId());*/
    	// Do not create new Attendee if attendee exists in the database.
    	
    	//Adding Fault tolerance 
    	
    	Attendee attendeeFromDB = getAttendee(attendee.getId());
    	if((attendeeFromDB==null) || (attendeeFromDB.getId()==null)){
        	CouchAttendee couchAttendee= new CouchAttendee(attendee.getId(),attendee.getId(),attendee.getName());
        	CouchID returnedAttendee= couch.request(null, RequestType.POST, couchAttendee, CouchID.class, null, 201);
            attendee = getAttendee(returnedAttendee.getId());
    	}
        return attendee;
    }

    @Override
    public Attendee updateAttendee(Attendee attendee) {

        Attendee original = getAttendee(attendee.getId());

        couch.request(attendee.getId(), RequestType.PUT, attendee, null, null, 201);

        attendee = getAttendee(attendee.getId());

        return attendee;
    }

    @Override
    public Collection<Attendee> getAllAttendees() {

        AllDocs allDocs = couch.request("_design/attendees/_view/all", RequestType.GET, null, AllDocs.class, null, 200);

        Collection<Attendee> attendees = new ArrayList<Attendee>();
        for (String id : allDocs.getIds()) {
            Attendee attendee = getAttendee(id);
            attendees.add(attendee);
        }

        return attendees;
    }

    @Override
    public void clearAllAttendees() {
        AllDocs allDocs = couch.request("_design/attendees/_view/all", RequestType.GET, null, AllDocs.class, null, 200);

        for (String id : allDocs.getIds()) {
            deleteAttendee(id);
        }
    }

    @Override
    public Attendee getAttendee(String id) {
        Attendee attendee = couch.request(id, RequestType.GET, null, Attendee.class, null, 200, true);
        return attendee;
    }

    @Override
    public void deleteAttendee(String id) {
        Attendee attendee = getAttendee(id);

        couch.request(id, RequestType.DELETE, null, null, null, 200);
    }

    @Override
    public boolean isAccessible() {
        return connected;
    }

}
