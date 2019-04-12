package com.pixo.models;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "userdata")
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String password;
    private String name;
    private String email;
    private String username;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", 
    	joinColumns = @JoinColumn(name = "user_id"), 
    	inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @Value("TRUE")
    private boolean enabled;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
//    private Set<FileItemEntity> mediaitems;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
//    private Set<Comment> comments;
    
    @ManyToMany
    @JoinTable(name="likes",
            joinColumns=
            @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="media_id", referencedColumnName="id")
    )
    private Set<Image> likes;

    @ManyToMany
    @JoinTable(name="follow",
            joinColumns=
            @JoinColumn(name="follower_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="followed_id", referencedColumnName="id")
    )
    private Set<User> followsList;

    @ManyToMany
    @JoinTable(name="follow",
            joinColumns=
            @JoinColumn(name="followed_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="follower_id", referencedColumnName="id")
    )
    private Set<User> followerList;
    
    public User() {}

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
		return roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<User> getFollowsList() {
        return followsList;
    }

    public void setFollowsList(Set<User> followsList) {
        this.followsList = followsList;
    }

    public Set<User> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(Set<User> followerList) {
        this.followerList = followerList;
    }

    public Set<Image> getLikes() {
        return likes;
    }

    public void setLikes(Set<Image> likes) {
        this.likes = likes;
    }

    public boolean equals(User user2){
        return user2.getId() == this.id;
    }

//    public Set<Comment> getComments() {
//        return comments;
//    }
//
//    public void setComments(Set<Comment> comments) {
//        this.comments = comments;
//    }
//
//	public Set<FileItemEntity> getMediaitems() {
//		return mediaitems;
//	}
//
//	public void setMediaitems(Set<FileItemEntity> mediaitems) {
//		this.mediaitems = mediaitems;
//	}
}
