package com.app.warrantychecker.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "user_entity")
public class User {

    @Id
    private String email;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date modifiedDate;
}
