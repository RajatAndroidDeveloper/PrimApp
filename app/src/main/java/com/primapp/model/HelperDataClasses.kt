package com.primapp.model

import android.view.View
import com.primapp.model.comment.CommentData
import com.primapp.model.community.CommunityData
import com.primapp.model.members.CommunityMembersData
import com.primapp.model.post.PostListResult
import com.primapp.model.reply.ReplyData
import com.sendbird.android.BaseMessage


data class ShowImage(val url: String)
data class ShowVideo(val url: String)
data class DownloadFile(val url: String)
data class LikePost(val postData: PostListResult)
data class EditPost(val postData: PostListResult)
data class DeletePost(val postData: PostListResult)
data class HidePost(val postData: PostListResult)
data class UnHidePost(val postData: PostListResult)
data class ReportPost(val postData: PostListResult)
data class CommentPost(val postData: PostListResult)
data class LikeComment(val commentData: CommentData)
data class LikePostMembers(val postData: PostListResult)
data class LikeReply(val replyData: ReplyData)
data class RequestMentor(val membersData: CommunityMembersData)
data class ShowUserProfile(val userId: Int)
data class BookmarkPost(val postData: PostListResult)
data class SharePost(val postData: PostListResult, val view: View)
data class ShowCommunityDetails(val communityData: CommunityData)
data class ShowPostDetails(val communityId: Int, val postId: Int)
data class LikeCommentReply(val replyData: ReplyData, val commentId: Int, val commentAdapterPosition: Int)

//Mentorship request action
data class AcceptMetorRequest(val id: Int)
data class RejectMetorRequest(val id: Int)

//Chat
data class MessageLongPressCallback(val message: BaseMessage, val position: Int)
//data class FileMessageLongPressCallback(val message: BaseMessage, val position: Int)

//Reported Post
data class RemoveReportedUser(val postId: Int, val userId: Int)
data class ReportedByMembers(val postId: Int)