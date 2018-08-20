package com.book.member.domain;


import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"createdAt", "updatedAt"})
public class Member  {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Name name;

    @CreationTimestamp
    @Column(name = "created_dt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_dt", nullable = false)
    private Timestamp updatedAt;

    public Member(final Email email, final Password password, final Name name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }


}
