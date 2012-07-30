package com.gokaconsulting.taskerweb.server;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

//import java.util.logging.*;

import javax.persistence.Transient;

@PersistenceCapable
public class Task implements Serializable {
	@Transient
	private static final long serialVersionUID = -5660588353160363359L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
	public Key getKey()
    {
    	return key;
    }
    
	public Task(String title, String creator, Date createDate, 
    		String taskDescription, Date dueDate, String completor, String status, 
    		String beforePhotoUrl, BlobKey beforeKey)
    {
    	this.title = title;
    	this.creator = creator;
    	this.createDate = createDate;
    	this.dueDate = dueDate;
    	this.completor = completor;
    	this.status = status;
    	this.taskDescription = taskDescription;
    	this.beforePhotoUrl = beforePhotoUrl;
    	this.beforeKey = beforeKey;
    }
    
    @Persistent
    @Expose
    private String title;
    
    @Persistent
    @Expose
    private String creator;
    
    @Persistent
    @Expose
    private Date createDate;
    
    @Persistent
    @Expose
    private String taskDescription;
    
	@Persistent
    @Expose
    private Date dueDate;
    
    @Persistent
    @Expose
    private Date completedDate;
    
    @Persistent
    @Expose
    private String completor;
    
    @Persistent
    @Expose
    private String status;
    
    @Persistent
    @Expose
    private Long taskID;
    
	@Persistent
    private BlobKey beforeKey;
    
    @Persistent 
    private BlobKey afterKey;
    
	@Persistent
    @Expose
    private String beforePhotoUrl;
    
    @Persistent
    @Expose
    private String afterPhotoUrl;

    public BlobKey getBeforeKey() {
		return beforeKey;
	}

	public void setBeforeKey(BlobKey beforeKey) {
		this.beforeKey = beforeKey;
	}

	public BlobKey getAfterKey() {
		return afterKey;
	}

	public void setAfterKey(BlobKey afterKey) {
		this.afterKey = afterKey;
	}
    
    public String getBeforePhotoUrl() {
		return beforePhotoUrl;
	}

	public void setBeforePhotoUrl(String beforePhotoUrl) {
		this.beforePhotoUrl = beforePhotoUrl;
	}

	public String getAfterPhotoUrl() {
		return afterPhotoUrl;
	}

	public void setAfterPhotoUrl(String afterPhotoUrl) {
		this.afterPhotoUrl = afterPhotoUrl;
	}
    
    public Long getTaskID()
    {
    	return taskID;
    }
    
    public void setTaskID(Long taskID)
    {
    	this.taskID = taskID;
    }
        
    public String getTitle() {
    	return title;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
	
    public String getCreator() {
    	return creator;
    }
    
    public void setCreator(String creator) {
    	this.creator = creator;
    }
    
    public Date getCreateDate() {
    	return createDate;
    }
    
    public void setCreateDate(Date createDate)  {
    	this.createDate = createDate;
    }
    
    public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getCompletor() {
		return completor;
	}

	public void setCompletor(String completor) {
		this.completor = completor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
