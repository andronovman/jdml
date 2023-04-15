package com.andronovman.jdml.entities;

import com.andronovman.jdml.lib.annotations.Column;
import com.andronovman.jdml.lib.annotations.Entity;
import com.andronovman.jdml.lib.annotations.Id;
import com.andronovman.jdml.lib.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(table = "users")
public class User {
    @Id
    private Long tel;
    private String name;
    private String password;
    @Column(name = "is_active")
    @Transient
    private boolean active;
    @Column(name = "user_ref_id")
    private Long userRefId;
    private String role;
    @Column(name = "last_login_date")
    private Date lastLoginDate;
    @Transient
    private String isTransientField;

    @Override
    public String toString() {
        return "User{" +
                "tel=" + tel +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", userRefId=" + userRefId +
                ", role='" + role + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                ", isTransientField='" + isTransientField + '\'' +
                '}';
    }
}
