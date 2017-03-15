/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.fileupload.impl.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DBFilePK implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column
	private String id;
	@Column(name="chunk_order")
	private long chunkOrder;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getChunkOrder() {
		return chunkOrder;
	}
	public void setChunkOrder(long chunkOrder) {
		this.chunkOrder = chunkOrder;
	}

	public DBFilePK() {

	}
	public DBFilePK(String id, long chunkOrder) {
		super();
		this.id = id;
		this.chunkOrder = chunkOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (chunkOrder ^ (chunkOrder >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBFilePK other = (DBFilePK) obj;
		if (chunkOrder != other.chunkOrder)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
