package com.primapp.model

import android.view.View
import com.primapp.model.comment.CommentData
import com.primapp.model.community.CommunityData
import com.primapp.model.members.CommunityMembersData
import com.primapp.model.portfolio.BenefitsData
import com.primapp.model.portfolio.ExperienceData
import com.primapp.model.post.PostListResult
import com.primapp.model.reply.ReplyData
import com.primapp.model.todo.TodoTaskItem
import com.sendbird.android.message.BaseMessage


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
data class CommentMoreOptions(val commentData: CommentData)
data class LikePostMembers(val postData: PostListResult)
data class LikeReply(val replyData: ReplyData)
data class DeleteReply(val replyData: ReplyData)
data class RequestMentor(val membersData: CommunityMembersData)
data class ShowUserProfile(val userId: Int)
data class BookmarkPost(val postData: PostListResult)
data class SharePost(val postData: PostListResult, val view: View)
data class ShowCommunityDetails(val communityData: CommunityData)
data class ShowPostDetails(val communityId: Int, val postId: Int)
data class EditUploadedPost(val postId: Int, val communityId: Int)
data class ShowContractDetails(val contractId: Int)
data class LikeCommentReply(val replyData: ReplyData, val commentId: Int, val commentAdapterPosition: Int)
data class DeleteCommentReply(val replyData: ReplyData, val commentId: Int, val commentAdapterPosition: Int)
data class LoadWebUrl(val url: String)


//Mentorship request action
data class AcceptMetorRequest(val id: Int)
data class RejectMetorRequest(val id: Int)

//Chat
data class MessageLongPressCallback(val message: BaseMessage, val position: Int)
//data class FileMessageLongPressCallback(val message: BaseMessage, val position: Int)

//Reported Post
data class RemoveReportedUser(val postId: Int, val userId: Int)
data class ReportedByMembers(val postId: Int)

//Portfolio
data class EditBenefits(val benefitData: BenefitsData)
data class DeleteItem(val id: Int)
data class EditExperience(val experienceData: ExperienceData)
data class RequestMentorWithCommunityId(val communityId: Int)

//To-do
data class ViewTodoTask(val todoTaskItem: TodoTaskItem)
data class MuteVideo(val position: Int, val muteState: Boolean)
data class CaptionClicked(val caption: String)

data class VideoViewData(val view: View, val position: Int)