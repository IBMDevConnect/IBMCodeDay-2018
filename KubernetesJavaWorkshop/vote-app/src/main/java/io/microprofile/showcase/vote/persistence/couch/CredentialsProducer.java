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

import java.io.StringReader;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CredentialsProducer {
	
	@Inject @ConfigProperty(name="dbUsername") Optional<String> username;
	@Inject @ConfigProperty(name="dbPassword") Optional<String> password; 
	@Inject @ConfigProperty(name="dbUrl") Optional<String> url;
	@Inject @ConfigProperty(name="CLOUDANT_SERVICE_SERVICE_HOST") Optional<String> dbHost;
	@Inject @ConfigProperty(name="CLOUDANT_SERVICE_SERVICE_PORT") Optional<String> dbPort;
	//private String url;
	
	
    @Produces
    public Credentials newCredentials() {
        Credentials credentials = null;
        String vcap = System.getenv("VCAP_SERVICES");
        if (vcap != null) {
            credentials = parseVcap(vcap);
        } else {
            credentials = useEnv();
        }

        return credentials;
    }

    private Credentials parseVcap(String vcapServices) {

        JsonObject vcapServicesJson = Json.createReader(new StringReader(vcapServices)).readObject();
        JsonArray cloudantObjectArray = vcapServicesJson.getJsonArray("cloudantNoSQLDB");
        if (cloudantObjectArray == null) {
            return useEnv();
        }
        JsonObject cloudantObject = cloudantObjectArray.getJsonObject(0);
        JsonObject cloudantCredentials = cloudantObject.getJsonObject("credentials");
        JsonString cloudantUsername = cloudantCredentials.getJsonString("username");

        JsonString cloudantPassword = cloudantCredentials.getJsonString("password");
        JsonString cloudantUrl = cloudantCredentials.getJsonString("url");

        String username = cloudantUsername.getString();
        String password = cloudantPassword.getString();
        String url = cloudantUrl.getString();

        return new Credentials(username, password, url);
    }

    private Credentials useEnv() {
    	
    	// Introduced Config API to inject DB credentials.
    	
        /*String username = System.getenv("dbUsername");
        String password = System.getenv("dbPassword");
        String url = System.getenv("dbUrl");*/
    	
    	//url="http://"+dbHost+":"+dbPort;
    	System.out.println("The DB URL is : "+url +" dbUsername : "+username +" password: "+password);
    	if(username.isPresent() && password.isPresent() && url.isPresent()){
            return new Credentials(username.get(), password.get(), url.get());
    	}
        else{
            return null;
        }
    }

}
