package io.microprofile.showcase.vote.persistence.couch.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import io.microprofile.showcase.vote.api.PersistenceProvider;
import io.microprofile.showcase.vote.api.PersistenceTypes;
import io.microprofile.showcase.vote.persistence.Persistent;

@Dependent
public class BooleanFallbackHandler implements FallbackHandler<Boolean> {
	
	private @Inject PersistenceProvider persistenceProvider;

	@Override
    public Boolean handle(ExecutionContext context) {
		persistenceProvider.setPersistenceType(PersistenceTypes.NO_PERSISTENCE_HASH_MAP);
		persistenceProvider.setAvailable(true);
		System.out.println("Failed to connect to Cloudant service falling back to HashMap persistence :"+PersistenceTypes.NO_PERSISTENCE_HASH_MAP);
		return false;
    }
}
