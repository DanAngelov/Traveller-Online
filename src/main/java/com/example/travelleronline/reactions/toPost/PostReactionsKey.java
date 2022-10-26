package com.example.travelleronline.reactions.toPost;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class PostReactionsKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(name = "post_id", nullable = false)
    Integer postId;

}