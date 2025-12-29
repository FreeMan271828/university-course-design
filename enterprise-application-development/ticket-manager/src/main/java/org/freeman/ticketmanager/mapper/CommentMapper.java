package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.freeman.ticketmanager.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 插入评论
    int insert(Comment comment);

    // 查询评论列表
    // isInternal = true: 查所有 (PUBLIC + INTERNAL)
    // isInternal = false: 只查 PUBLIC
    List<Comment> selectByTicketId(@Param("ticketId") Long ticketId,
                                   @Param("isInternal") boolean isInternal);
}