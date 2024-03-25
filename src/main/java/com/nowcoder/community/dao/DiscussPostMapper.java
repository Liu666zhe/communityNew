package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //分页展示帖子
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //@Param用于给参数取别名，如果只有一个参数，并且在<if>里使用，就需要加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //新增帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost selectDiscussPostById(int id);

    //更新帖子的评论数量
    int updateCommentCount(int id,int commentCount);
}
