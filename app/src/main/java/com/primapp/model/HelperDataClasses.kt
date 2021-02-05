package com.primapp.model

import com.primapp.model.comment.CommentData
import com.primapp.model.post.PostListResult
import com.primapp.model.reply.ReplyData


data class ShowImage(val url: String)
data class ShowVideo(val url: String)
data class LikePost(val communityId: Int, val postId: Int, val isLike: Boolean, val isJoined:Boolean)
data class EditPost(val postData: PostListResult)
data class DeletePost(val postData: PostListResult)
data class HidePost(val postData: PostListResult)
data class ReportPost(val postData: PostListResult)
data class CommentPost(val postData: PostListResult)
data class LikeComment(val commentData: CommentData)
data class LikePostMembers(val postData: PostListResult)
data class LikeReply(val replyData: ReplyData)