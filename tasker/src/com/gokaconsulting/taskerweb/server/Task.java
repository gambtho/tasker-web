package com.gokaconsulting.taskerweb.server;

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
    		String beforePhotoId, String afterPhotoId)
    {
    	this.title = title;
    	this.creator = creator;
    	this.createDate = createDate;
    	this.dueDate = dueDate;
    	this.completor = completor;
    	this.status = status;
    	this.taskDescription = taskDescription;
    	this.beforePhotoId = beforePhotoId;
    	this.afterPhotoId = afterPhotoId;
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
    @Expose
    private String beforePhotoId;
    
    @Persistent
    @Expose
    private String afterPhotoId;
    
    public String getAfterPhotoId() {
		return afterPhotoId;
	}

	public void setAfterPhotoId(String afterPhotoId) {
		this.afterPhotoId = afterPhotoId;
	}

	public String getBeforePhotoId() {
		return beforePhotoId;
	}

	public void setBeforePhotoId(String blobKey) {
		this.beforePhotoId = blobKey;
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
