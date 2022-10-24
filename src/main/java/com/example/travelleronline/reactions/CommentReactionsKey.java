package com.example.travelleronline.reactions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class CommentReactionsKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(name = "comment_id", nullable = false)
    Integer commentId;

}