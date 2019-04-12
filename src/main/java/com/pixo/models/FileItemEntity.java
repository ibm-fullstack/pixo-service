package com.pixo.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "mediaitems")
public class FileItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "filename")
	private String filename;

	@Column(name = "location")
	private String location;

	@Column(name = "hidden")
	private boolean hidden;
	
    @ManyToOne()
    @JoinColumn(name = "userdata_id")
    private User user;
    
    @OneToMany(mappedBy = "mediaitem", cascade = CascadeType.PERSIST)
    private Set<Comment> comments;
    
    @ManyToMany
    @JoinTable(name="likes",
            joinColumns=
            @JoinColumn(name="media_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id")
    )
    private Set<User> likes;

	public FileItemEntity() {
	}

	public FileItemEntity(User user, String filename, String location, boolean hidden) {
		this.user = user;
		this.filename = filename;
		this.location = location;
		this.hidden = hidden;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Set<User> getLikes() {
		return likes;
	}

	public void setLikes(Set<User> likes) {
		this.likes = likes;
	}
	
}

