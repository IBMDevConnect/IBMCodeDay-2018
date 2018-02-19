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

import javax.enterprise.context.Dependent;

@Dependent
public class Credentials {

    private String username;
    private String password;
    private String url;

    public Credentials(String username, String password, String url) {
        this.url = url;
        this.username = username;
        this.password = password;

        if ((this.username == null || this.password == null || this.url == null) || (username.trim().length()==0 || password.trim().length()==0 || url.trim().length()==0 )){
            throw new RuntimeException("Database cannot be accessed at this time, something is null. Passed in variables were "
                                       + "username=" + username + ", password="
                                       + ((password == null) ? "null" : "(non-null password)") + ", url=" + url);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}