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
public class A {
    @Id
    private Integer id_a;
    @Id
    private Integer id_b;
    @Column(name = "category_id")
    @InnerJoin(entityClass=B.class, entityField="id", lazy=true)
    private B category;
    private String name;
    private boolean isActive;
    private Date createdDate;
}
