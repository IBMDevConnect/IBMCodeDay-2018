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
import io.microprofile.showcase.vote.model.SessionRating;
import io.microprofile.showcase.vote.persistence.Persistent;
import io.microprofile.showcase.vote.persistence.SessionRatingDAO;
import io.microprofile.showcase.vote.persistence.couch.CouchConnection.RequestType;
import io.microprofile.showcase.vote.persistence.couch.beans.ConnectionBean;
import io.microprofile.showcase.vote.utils.ConnectException;

@ApplicationScoped
@Persistent
public class CouchSessionRatingDAO implements SessionRatingDAO {

    @Inject
    CouchConnection couch;
    private @Inject PersistenceProvider persistenceProvider;
    private String dbName="ratings";
    private @Inject ConnectionBean connectionBean;

    private String allView = "function (doc) {emit(doc._id, 1)}";

    private String sessionView = "function (doc) {emit(doc.session, doc._id)}";
    private String attendeeView = "function (doc) {emit(doc.attendeeId, doc._id)}";

    private String designDoc = "{\"views\":{"
                               + "\"all\":{\"map\":\"" + allView + "\"},"
                               + "\"session\":{\"map\":\"" + sessionView + "\"},"
                               + "\"attendee\":{\"map\":\"" + attendeeView + "\"}}}";

    private boolean connected;
    private int executionCounter = 0;
    
    /*@PostConstruct
    public void connect() {
    	try {
			this.connected = couch.connect("ratings");
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (this.connected) {
    		String design = couch.request("_design/ratings", RequestType.GET, null, String.class, null, 200, true);
    		if(design == null){
    			couch.request("_design/ratings", RequestType.PUT, designDoc, null, null, 201);
    		}
    	}
    	
    }*/
   
    
    /**
     * This PostConstruct demonstrate fault tolerance API- Retry and Fallback
     */
    @PostConstruct
    public void connect() {
    	
		try {
			this.connected=connectionBean.connect(dbName,couch);
		} catch (ConnectException e) {
			System.out.println("The message from connect Exception : "+e.getMessage() +" SessionRatingDAO");
			e.printStackTrace();
		}
    	
		if (persistenceProvider.getPersistenceType().equals(PersistenceTypes.PERSISTENCE_NO_SEQL_DB)
				&& persistenceProvider.isAvailable()) {
            String design = couch.request("_design/ratings", RequestType.GET, null, String.class, null, 200, true);
            if (design == null) {
                couch.request("_design/ratings", RequestType.PUT, designDoc, null, null, 201);
            }
        }else{
			this.connected=false;
		}
    }

    @Override
    public SessionRating rateSession(SessionRating sessionRating) {

        CouchID ratingID = couch.request(null, RequestType.POST, sessionRating, CouchID.class, null, 201);
        sessionRating = getSessionRating(ratingID.getId());

        return sessionRating;

    }

    private SessionRating getSessionRating(String id) {
        SessionRating sessionRating = couch.request(id, RequestType.GET, null, SessionRating.class, null, 200);
        return sessionRating;
    }

    @Override
    public SessionRating updateRating(SessionRating newRating) {
        SessionRating original = getSessionRating(newRating.getId());

        couch.request(newRating.getId(), RequestType.PUT, newRating, null, null, 201);

        newRating = getSessionRating(newRating.getId());
        return newRating;
    }

    @Override
    public void deleteRating(String id) {

        SessionRating original = getSessionRating(id);

        couch.request(id, RequestType.DELETE, null, null, null, 200);
    }

    @Override
    public SessionRating getRating(String id) {
        SessionRating sessionRating = couch.request(id, RequestType.GET, null, SessionRating.class, null, 200, true);
        return sessionRating;
    }

    @Override
    public Collection<SessionRating> getRatingsBySession(String sessionId) {
        return querySessionRating("session", sessionId);
    }

    @Override
    public Collection<SessionRating> getRatingsByAttendee(String attendeeId) {
        return querySessionRating("attendee", attendeeId);
    }

    private Collection<SessionRating> querySessionRating(String query, String value) {

        AllDocs allDocs = couch.request("_design/ratings/_view/" + query, "key", "\"" + value + "\"", RequestType.GET, null, AllDocs.class, null, 200);

        Collection<SessionRating> ratings = new ArrayList<SessionRating>();
        for (String id : allDocs.getIds()) {
            SessionRating rating = getSessionRating(id);
            ratings.add(rating);
        }

        return ratings;
    }

    @Override
    public Collection<SessionRating> getAllRatings() {

        AllDocs allDocs = couch.request("_design/ratings/_view/all", RequestType.GET, null, AllDocs.class, null, 200);

        Collection<SessionRating> sessionRatings = new ArrayList<SessionRating>();
        for (String id : allDocs.getIds()) {
            SessionRating sessionRating = getSessionRating(id);
            sessionRatings.add(sessionRating);
        }

        return sessionRatings;
    }

    @Override
    public void clearAllRatings() {
        AllDocs allDocs = couch.request("_design/ratings/_view/all", RequestType.GET, null, AllDocs.class, null, 200);

        for (String id : allDocs.getIds()) {
            deleteSessionRating(id);
        }

    }

    private void deleteSessionRating(String id) {
        SessionRating sessionRating = getSessionRating(id);

        couch.request(id, RequestType.DELETE, null, null, null, 200);
    }

}
