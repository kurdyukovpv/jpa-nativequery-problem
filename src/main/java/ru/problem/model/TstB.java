package ru.problem.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tst_b")
public class TstB {

    public static Long ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "value")
    private Long value;
}
