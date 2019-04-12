package com.pixo.models;

import javax.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "userdata_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "media_id")
    private FileItemEntity mediaitem;

    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int compareTo(Comment comment2){
        return (int)(comment2.getId()- this.id);
    }

	public FileItemEntity getMediaitem() {
		return mediaitem;
	}

	public void setMediaitem(FileItemEntity mediaitem) {
		this.mediaitem = mediaitem;
	}
}
