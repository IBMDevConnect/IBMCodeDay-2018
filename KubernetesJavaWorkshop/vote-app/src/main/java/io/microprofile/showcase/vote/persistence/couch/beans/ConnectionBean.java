/**
 * 
 */
package io.microprofile.showcase.vote.persistence.couch.beans;

import java.time.temporal.ChronoUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;

import io.microprofile.showcase.vote.api.PersistenceProvider;
import io.microprofile.showcase.vote.api.PersistenceTypes;
import io.microprofile.showcase.vote.persistence.Persistent;
import io.microprofile.showcase.vote.persistence.couch.CouchConnection;
import io.microprofile.showcase.vote.utils.ConnectException;

/**
 * @author jagraj
 *
 */

@Dependent
public class ConnectionBean {
    private int executionCounter = 0;
	
	private @Inject PersistenceProvider persistenceProvider;
   
	/*@Inject
    CouchConnection couch;*/
    
	@Retry(maxRetries=10,delayUnit=ChronoUnit.SECONDS,delay=15,durationUnit=ChronoUnit.MINUTES,maxDuration=10)
	@Fallback(BooleanFallbackHandler.class)
	public Boolean connect(String dbName, CouchConnection couch) throws ConnectException{
		boolean connected=false;
		executionCounter++;
		System.out.println(
				"Delay Duration: " + "15 seconds" + " main Service called, execution " + executionCounter);
		connected = couch.connect(dbName);
		
		if(connected==true){
			persistenceProvider.setPersistenceType(PersistenceTypes.PERSISTENCE_NO_SEQL_DB);
			persistenceProvider.setAvailable(connected);
		}else{
			persistenceProvider.setPersistenceType(PersistenceTypes.NO_PERSISTENCE_HASH_MAP);
			persistenceProvider.setAvailable(connected);
		}
		return connected;
	}
}
