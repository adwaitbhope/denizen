package com.township.manager;

import java.util.ArrayList;

class Notice {

    String noticeId, postedByFirstName, PostedByLastName, postedByDesignation;
    String title, description, timestamp;
    ArrayList<String> wings;
    ArrayList<Comment> comments;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setPostedByLastName(String getPostedByLastName) {
        this.PostedByLastName = getPostedByLastName;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public void setPostedByDesignation(String postedByDesignation) {
        this.postedByDesignation = postedByDesignation;
    }

    public void setPostedByFirstName(String postedByFirstName) {
        this.postedByFirstName = postedByFirstName;
    }

    public void setWings(ArrayList<String> wings) {
        this.wings = wings;
    }

    public String getPostedByLastName() {
        return PostedByLastName;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public String getPostedByDesignation() {
        return postedByDesignation;
    }

    public String getPostedByFirstName() {
        return postedByFirstName;
    }

    public ArrayList<String> getWings() {
        return wings;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public static class Comment {
        String postedByFirstName, postedByLastName, postedByUserId, postedByWing, postedByApartment;
        String content, timestamp;

        public void setPostedByFirstName(String postedByFirstName) {
            this.postedByFirstName = postedByFirstName;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setPostedByApartment(String postedByApartment) {
            this.postedByApartment = postedByApartment;
        }

        public void setPostedByLastName(String postedByLastName) {
            this.postedByLastName = postedByLastName;
        }

        public void setPostedByUserId(String postedByUserId) {
            this.postedByUserId = postedByUserId;
        }

        public void setPostedByWing(String postedByWing) {
            this.postedByWing = postedByWing;
        }

        public String getPostedByFirstName() {
            return postedByFirstName;
        }

        public String getPostedByApartment() {
            return postedByApartment;
        }

        public String getContent() {
            return content;
        }

        public String getPostedByLastName() {
            return postedByLastName;
        }

        public String getPostedByUserId() {
            return postedByUserId;
        }

        public String getPostedByWing() {
            return postedByWing;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}

