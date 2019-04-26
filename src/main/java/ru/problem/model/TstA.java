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
@Table(name = "tst_a")
public class TstA  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Basic
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_id", referencedColumnName = "id")
    private TstB tstB;
}
