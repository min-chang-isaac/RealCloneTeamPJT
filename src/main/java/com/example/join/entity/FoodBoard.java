package com.example.join.entity;

import jakarta.persistence.*;

@Entity
public class FoodBoard {
// public class Book
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    
    //getterSetter
    public Long getId() {
		  return Id;
    }
   
    public void setId(Long Id) {
	    this.Id = Id;
    }
    public String getContent() {
	    return content;
    }
    public void setContent(String content){
	    this.content = content;
    }    
    
    public Profile getProfile() {
    	return profile;
    }
    
    public void setProfile(Profile profile) {
		this.profile = profile;
	}
    
}