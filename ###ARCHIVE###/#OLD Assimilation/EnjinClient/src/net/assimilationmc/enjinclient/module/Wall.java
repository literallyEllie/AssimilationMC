package net.assimilationmc.enjinclient.module;

import org.json.simple.JSONObject;

/**
 * Created by Ellie on 31/12/2016 for Assimilation.
 * Affiliated with www.minevelop.com
 */
public class Wall {

    public static JSONObject getFeed(String sessionId, int lastPostId, int afterPostId, int id, int limit,
                                     int limitComments, int limitLikes, boolean withReplies, String commentsOrder) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Feed.getFeed").addParam("with_replies", withReplies).addParam("session_id", sessionId);
        if(lastPostId > 0) requestBuilder.addParam("last_post_id", lastPostId);
        if (afterPostId > 0) requestBuilder.addParam("after_post_id", afterPostId);
        if (id > 0) requestBuilder.addParam("id", id);
        if (limit > 0) requestBuilder.addParam("limit", limit);
        if (limitComments > 0) requestBuilder.addParam("limit_comments", limitComments);
        if (limitLikes > 0) requestBuilder.addParam("limit_likes", limitLikes);
        if (!commentsOrder.isEmpty()) requestBuilder.addParam("comments_order", commentsOrder);
        return requestBuilder.build();
    }

    public static JSONObject getComments(String sessionId, int postId, int limit,
                                     int offset, boolean withReplies, String order, int commentId, int lastCommentId, int afterCommentId) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Feed.getComments").addParam("with_replies", withReplies).addParam("session_id", sessionId);
        if(postId > 0) requestBuilder.addParam("post_ids", postId);
        if (limit > 0) requestBuilder.addParam("limit", limit);
        if (offset > 0) requestBuilder.addParam("offset", offset);
        if (limit > 0) requestBuilder.addParam("limit", limit);
        if (!order.isEmpty()) requestBuilder.addParam("order", order);
        if (commentId > 0) requestBuilder.addParam("comment_ids", commentId);
        if(lastCommentId > 0) requestBuilder.addParam("last_comment_id", lastCommentId);
        if (afterCommentId > 0) requestBuilder.addParam("after_comment_id", afterCommentId);
        return requestBuilder.build();
    }

    public static JSONObject likePost(String sessionId, int postId, String type) {
        return new RequestBuilder().setMethod("Feed.likePost").addParam("post_id", postId).addParam("type", type).addParam("session_id", sessionId).build();
    }

    public static JSONObject deletePost(String sessionId, int postId) {
        return new RequestBuilder().setMethod("Feed.deletePost").addParam("post_id", postId).addParam("session_id", sessionId).build();
    }

    public static JSONObject deleteComment(String sessionId, int commentId) {
        return new RequestBuilder().setMethod("Feed.deleteComment").addParam("comment_id", commentId).addParam("session_id", sessionId).build();
    }

    /**
     *
     * @param sessionId Session ID
     * @param wallUserId The user ID of the wall to post
     * @param postType The type of post: "text", "photos", "link", video"
     * @param message The message to post
     * @param access Who can view it
     * @param url The embedded URL
     * @param title The embedded URL's title to display
     * @param description The embedded URL's description to display
     * @param thumbnail The embedded URL's thumbnail to display
     * @param embedWidth The width of the embed
     * @param embedHeight The heigh of the embed
     * @param videoTitle The title of the video
     * @param videoDescription The description of the video
     * @return The constructed request
     */
    public static JSONObject postMessage(String sessionId, int wallUserId, String postType,
                                         String message, String access, String url, String title, String description, String thumbnail,
                                         int embedWidth, int embedHeight, String videoTitle, String videoDescription) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Feed.postMessage").addParam("wall_user_id", wallUserId)
                .addParam("session_id", sessionId).addParam("post_type", postType).addParam("message", message);
        if (!access.isEmpty()) requestBuilder.addParam("access", access);
        if (!url.isEmpty()) requestBuilder.addParam("embed_url", url);
        if (!title.isEmpty()) requestBuilder.addParam("embed_title", title);
        if (!description.isEmpty()) requestBuilder.addParam("embed_description", description);
        if (!thumbnail.isEmpty()) requestBuilder.addParam("embed_thumbnail ", thumbnail);
        if (embedWidth > 0) requestBuilder.addParam("embed_width", embedWidth);
        if (embedHeight > 0) requestBuilder.addParam("embed_height", embedHeight);
        if (!videoTitle.isEmpty()) requestBuilder.addParam("embed_video_title", videoTitle);
        if (!videoDescription.isEmpty()) requestBuilder.addParam("embed_video_description", videoDescription);
        return requestBuilder.build();
    }

    public static JSONObject editMessage(String sessionId, int postId, String message) {
        return new RequestBuilder().setMethod("Feed.editMessage").addParam("post_id", postId).addParam("message", message).addParam("session_id", sessionId).build();
    }

    public static JSONObject uploadImage(String sessionId) {
        return new RequestBuilder().setMethod("Feed.uploadImage").addParam("session_id", sessionId).build();
    }

    public static JSONObject getEmbed(String sessionId, String url) {
        return new RequestBuilder().setMethod("Feed.getEmbed").addParam("session_id", sessionId).addParam("url", url).build();
    }

    public static JSONObject likeComment(String sessionId, int commentId, String type) {
        return new RequestBuilder().setMethod("Feed.likeComment").addParam("session_id", sessionId).addParam("commentId", commentId).addParam("type", type).build();
    }

    public static JSONObject postComment(String sessionId, int postId, String message, String type, int commentId) {
        RequestBuilder requestBuilder = new RequestBuilder().setMethod("Friends.getList").addParam("post_id", postId).
                addParam("message", message).addParam("type", type).addParam("type", type).addParam("session_id", sessionId);
        if (commentId > 0) requestBuilder.addParam("comment_id", commentId);
        return requestBuilder.build();
    }

    public static JSONObject enableComments(String sessionId, int postId) {
        return new RequestBuilder().setMethod("Feed.enableComments").addParam("session_id", sessionId).addParam("post_id", postId).build();
    }

    public static JSONObject disableComments(String sessionId, int postId) {
        return new RequestBuilder().setMethod("Feed.disableComments").addParam("session_id", sessionId).addParam("post_id", postId).build();
    }

    public static JSONObject deleteAllComments(String sessionId, int postId) {
        return new RequestBuilder().setMethod("Feed.deleteAllComments").addParam("session_id", sessionId).addParam("post_id", postId).build();
    }

}
