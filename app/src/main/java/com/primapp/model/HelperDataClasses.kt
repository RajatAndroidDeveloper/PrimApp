package com.primapp.model

import com.primapp.model.comment.CommentData
import com.primapp.model.post.PostListResult


data class ShowImage(val url: String)
data class ShowVideo(val url: String)
data class LikePost(val communityId: Int, val postId: Int, val isLike: Boolean, val isJoined:Boolean)
data class EditPost(val postData: PostListResult)
data class DeletePost(val postData: PostListResult)
data class HidePost(val postData: PostListResult)
data class ReportPost(val postData: PostListResult)
data class CommentPost(val postData: PostListResult)
data class LikeComment(val commentData: CommentData)