package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据实体查询评论详情
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //按实体查询评论数量
    int selectCountByEntity(int entityType, int entityId);

    //新增评论
    int insertComment(Comment comment);
}
