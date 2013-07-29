/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.social.managers;

import org.springframework.security.oauth2.provider.ClientDetails;

import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;
import eu.trentorise.smartcampus.social.model.User;

/**
 * @author raman
 *
 */
public class MockAuthServices implements AuthServices {

	@Override
	public String loadResourceAuthorityByResourceUri(String resourceUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User loadUserByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User loadUserBySocialId(String socialId) {
		// TODO Auto-generated method stub
		return null;
	}

}
