package com.eurodyn.qlack2.fuse.workflow.runtime.impl.model;

import java.io.Serializable;

import javax.persistence.*;

@Entity
public class ProcessContent implements Serializable {
	
	private static final long serialVersionUID = 771831700149128164L;

	@Id
	private String processId;
	
	private String content;
	private String checksum;
	
	public ProcessContent() {
    }
	
	public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getId() {
        return processId;
    }
    
    public void setId(String processId) {
        this.processId = processId;
    }
    
    public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public static ProcessContent find(EntityManager em, String processId) {
		return em.find(ProcessContent.class, processId);
	}
}
