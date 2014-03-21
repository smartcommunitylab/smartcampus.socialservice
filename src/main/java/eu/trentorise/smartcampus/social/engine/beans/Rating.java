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

package eu.trentorise.smartcampus.social.engine.beans;

public class Rating {
	private String userId;
	private String entityURI;
	private double rating;

	public Rating() {

	}

	public Rating(String userId, String entityURI, double rating) {
		this.entityURI = entityURI;
		this.rating = rating;
		this.userId = userId;
	}

	public Rating(String entityURI, double rating) {
		this.entityURI = entityURI;
		this.rating = rating;
	}

	public String getEntityURI() {
		return entityURI;
	}

	public void setEntityURI(String entityURI) {
		this.entityURI = entityURI;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
