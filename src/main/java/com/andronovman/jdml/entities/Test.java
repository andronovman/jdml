package com.andronovman.jdml.entities;

import com.andronovman.jdml.lib.annotations.Column;
import com.andronovman.jdml.lib.annotations.Entity;
import com.andronovman.jdml.lib.annotations.Id;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Test {
    @Id
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String text;
    private Double currency;
    @Column(name = "numberrange")
    private Integer range;
    @Column(name = "created_date")
    private Date createdDate;


}
