package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL)
    private List<Course> courses;

    @JsonIgnore
    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL)
    private List<ClassRoom> classRooms;
}
