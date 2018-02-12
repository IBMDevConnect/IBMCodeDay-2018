package io.microprofile.showcase.vote.api;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersistenceProvider {
	private String persistenceType="hashmap";
	private boolean isAvailable=true;
	/**
	 * @return the persistenceType
	 */
	public String getPersistenceType() {
		return persistenceType;
	}
	/**
	 * @param persistenceType the persistenceType to set
	 */
	public void setPersistenceType(String persistenceType) {
		this.persistenceType = persistenceType;
	}
	/**
	 * @return the isAvailable
	 */
	public boolean isAvailable() {
		return isAvailable;
	}
	/**
	 * @param isAvailable the isAvailable to set
	 */
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	
}
