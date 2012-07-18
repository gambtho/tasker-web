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

	//@Transient
	//private static Logger logger = Logger.getLogger(Task.class.getName());
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
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
    private Long id;
    
    public Long getKey()
    {
    	return key.getId();
    }
    
    public Long getID()
    {
    	return id;
    }
    
    public void setID(Long id)
    {
    	this.id = id;
    }
    
    public Task(String title, String creator, Date createDate)
    {
    	this.title = title;
    	this.creator = creator;
    	this.createDate = createDate;
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
    
    
}
