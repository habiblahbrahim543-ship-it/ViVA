package com.example.data.local

import com.example.data.model.*

object SampleData {

    val users = listOf(
        User(
            id = "user_official_viva",
            username = "VIVA",
            displayName = "VIVA Official",
            bio = "Official VIVA Support & Trust Safety. Verified Administrative Communications Channel.",
            website = "https://viva.social/support",
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
            followersCount = 12500000,
            followingCount = 1,
            likesCount = 58000000,
            videosCount = 8,
            isVerified = true,
            verificationStatus = "approved",
            isCurrentUser = false,
            role = "OWNER",
            accountStatus = "ACTIVE",
            email = "support@viva.social"
        ),
        User(
            id = "user_owner",
            username = "viva_owner",
            displayName = "VIVA Founder & Owner",
            bio = "Platform Founder & System Administrator. Managing VIVA global operations and trust.",
            website = "https://viva.social/founder",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            followersCount = 4200000,
            followingCount = 15,
            likesCount = 22000000,
            videosCount = 6,
            isVerified = true,
            verificationStatus = "approved",
            isCurrentUser = false,
            role = "OWNER",
            accountStatus = "ACTIVE",
            email = "owner@viva.social"
        ),
        User(
            id = "user_me",
            username = "alex_viva",
            displayName = "Alex Rivera",
            bio = "Digital Creator & Visual Explorer ✨ Inspiring stories every day. Requesting verification soon!",
            website = "https://viva.social/@alex_viva",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            followersCount = 14200,
            followingCount = 384,
            likesCount = 98500,
            videosCount = 3,
            isVerified = false,
            verificationStatus = "unverified",
            isCurrentUser = true,
            role = "USER",
            accountStatus = "ACTIVE",
            email = "alex@viva.user"
        ),
        User(
            id = "user_admin",
            username = "admin_viva",
            displayName = "VIVA Staff Moderator",
            bio = "Official VIVA Trust & Safety Moderator. Reviewing creator verifications and keeping VIVA safe.",
            website = "https://viva.social/safety",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            followersCount = 2500000,
            followingCount = 12,
            likesCount = 12000000,
            videosCount = 1,
            isVerified = true,
            verificationStatus = "approved",
            isCurrentUser = false,
            role = "ADMIN",
            accountStatus = "ACTIVE",
            email = "moderator@viva.social"
        ),
        User(
            id = "user_1",
            username = "elena_motion",
            displayName = "Elena Rostova",
            bio = "Urban dance choreography & rhythm 💃 Studio based in Tokyo 🗼",
            website = "https://elenadance.com",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            followersCount = 284000,
            followingCount = 152,
            likesCount = 1920000,
            videosCount = 24,
            isVerified = true,
            verificationStatus = "approved",
            isFollowing = true,
            role = "USER",
            accountStatus = "ACTIVE",
            email = "elena@dance.org"
        ),
        User(
            id = "user_2",
            username = "neon_samurai",
            displayName = "Kenji Cyber",
            bio = "Cinematography & 3D Visuals 🎬 Glitch & Cyberpunk Aesthetics ⚡",
            website = "https://kenjiart.io",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            followersCount = 512000,
            followingCount = 420,
            likesCount = 3400000,
            videosCount = 45,
            isVerified = true,
            verificationStatus = "approved",
            isFollowing = true,
            role = "USER",
            accountStatus = "ACTIVE",
            email = "kenji@cyber.jp"
        ),
        User(
            id = "user_3",
            username = "chef_marcus",
            displayName = "Chef Marcus",
            bio = "60-second gourmet recipes anyone can cook 🍳 Michelin trained ⭐️",
            website = "https://marcuscooks.tv",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            followersCount = 890000,
            followingCount = 95,
            likesCount = 5200000,
            videosCount = 38,
            isVerified = false,
            verificationStatus = "pending",
            isFollowing = false,
            role = "USER",
            accountStatus = "ACTIVE",
            email = "marcus@culinary.tv"
        ),
        User(
            id = "user_4",
            username = "wanderlust_maya",
            displayName = "Maya Sky",
            bio = "Solo backpacking across 48 countries ✈️ Hidden gems & drone shots 🏔️",
            website = "https://mayatravels.org",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            followersCount = 1200000,
            followingCount = 210,
            likesCount = 8700000,
            videosCount = 62,
            isVerified = true,
            verificationStatus = "approved",
            isFollowing = false,
            role = "USER",
            accountStatus = "ACTIVE",
            email = "maya@wander.world"
        )
    )

    val followRelations = listOf(
        FollowRelation("user_me", "user_1"),
        FollowRelation("user_me", "user_2"),
        FollowRelation("user_1", "user_me"),
        FollowRelation("user_2", "user_me"),
        FollowRelation("user_3", "user_1"),
        FollowRelation("user_4", "user_1")
    )

    val verificationRequests = listOf(
        VerificationRequest(
            id = "vr_sample_1",
            userId = "user_3",
            username = "chef_marcus",
            fullName = "Marcus Vance",
            category = "Creator",
            country = "United States",
            reason = "Certified Michelin star culinary creator with 890K audience across platforms and featured in Food & Wine magazine.",
            website = "https://marcuscooks.tv",
            instagram = "@chefmarcus_official",
            youtube = "ChefMarcusCulinary",
            tiktok = "@chefmarcus_viva",
            otherLinks = "Press article: https://foodandwine.example.com/marcus",
            supportingDocuments = "Government Passport ID & Culinary Certification verified",
            status = "pending",
            createdAt = System.currentTimeMillis() - 7200000
        )
    )

    val adminNotifications = listOf(
        AdminNotification(
            id = "an_1",
            type = "VERIFICATION_REQUEST",
            requestId = "vr_sample_1",
            userId = "user_3",
            username = "chef_marcus",
            message = "@chef_marcus submitted a verification request under Creator category.",
            isRead = false,
            timestamp = System.currentTimeMillis() - 7200000
        )
    )

    val sounds = listOf(
        Sound(
            id = "sound_1",
            title = "Midnight Echoes - VIVA Original Beats",
            creator = "VIVA Sound Studio",
            durationSeconds = 28,
            videoCount = 14200,
            audioUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        ),
        Sound(
            id = "sound_2",
            title = "Tokyo Neon Pulse (Lo-fi Remix)",
            creator = "Kenji Cyber",
            durationSeconds = 15,
            videoCount = 8900
        ),
        Sound(
            id = "sound_3",
            title = "Acoustic Sunset Groove",
            creator = "Elena Rostova",
            durationSeconds = 22,
            videoCount = 31200
        ),
        Sound(
            id = "sound_4",
            title = "Sizzling Butter & Herbs (Original ASMR)",
            creator = "Chef Marcus",
            durationSeconds = 18,
            videoCount = 4500
        )
    )

    val hashtags = listOf(
        Hashtag(tag = "viva", videoCount = 1250000, viewsCount = 98000000, isTrending = true),
        Hashtag(tag = "dance", videoCount = 890000, viewsCount = 45000000, isTrending = true),
        Hashtag(tag = "cyberpunk", videoCount = 430000, viewsCount = 28000000, isTrending = true),
        Hashtag(tag = "cooking", videoCount = 760000, viewsCount = 39000000, isTrending = true),
        Hashtag(tag = "travel", videoCount = 1100000, viewsCount = 67000000, isTrending = true),
        Hashtag(tag = "cinematic", videoCount = 320000, viewsCount = 19000000, isTrending = true),
        Hashtag(tag = "comedy", videoCount = 650000, viewsCount = 33000000, isTrending = false),
        Hashtag(tag = "fitness", videoCount = 290000, viewsCount = 15000000, isTrending = false)
    )

    val videos = listOf(
        Video(
            id = "video_1",
            creatorId = "user_1",
            creatorUsername = "elena_motion",
            creatorDisplayName = "Elena Rostova",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            caption = "Late night practice at Shibuya crosswalk! Tried a new freestyle combo 🔥 What do you think of this transition? #dance #viva #tokyo",
            hashtags = "#dance #viva #tokyo",
            soundId = "sound_1",
            soundTitle = "Midnight Echoes - VIVA Original Beats",
            soundCreator = "VIVA Sound Studio",
            videoUrl = "raw://viva_clip_dance",
            thumbnailUrl = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=800&auto=format&fit=crop&q=80",
            durationSeconds = 15,
            viewsCount = 184000,
            likesCount = 28400,
            commentsCount = 612,
            sharesCount = 1420,
            savesCount = 3900,
            isLiked = false,
            isSaved = false,
            isFollowingCreator = true,
            category = "Dance"
        ),
        Video(
            id = "video_2",
            creatorId = "user_2",
            creatorUsername = "neon_samurai",
            creatorDisplayName = "Kenji Cyber",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            caption = "Rainy neon reflections in Shinjuku alleys 🌧️ 4K anamorphic lens test. Sound on for the atmosphere! #cyberpunk #cinematic #viva",
            hashtags = "#cyberpunk #cinematic #viva",
            soundId = "sound_2",
            soundTitle = "Tokyo Neon Pulse (Lo-fi Remix)",
            soundCreator = "Kenji Cyber",
            videoUrl = "raw://viva_clip_cyberpunk",
            thumbnailUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&auto=format&fit=crop&q=80",
            durationSeconds = 12,
            viewsCount = 412000,
            likesCount = 59100,
            commentsCount = 1120,
            sharesCount = 3890,
            savesCount = 8900,
            isLiked = true,
            isSaved = true,
            isFollowingCreator = true,
            category = "Cinematic"
        ),
        Video(
            id = "video_3",
            creatorId = "user_3",
            creatorUsername = "chef_marcus",
            creatorDisplayName = "Chef Marcus",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            caption = "Crispy garlic butter ribeye with rosemary sear 🥩 Save this recipe for date night! Ready in 8 minutes. #cooking #foodie #viva",
            hashtags = "#cooking #foodie #viva",
            soundId = "sound_4",
            soundTitle = "Sizzling Butter & Herbs (Original ASMR)",
            soundCreator = "Chef Marcus",
            videoUrl = "raw://viva_clip_cooking",
            thumbnailUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80",
            durationSeconds = 15,
            viewsCount = 680000,
            likesCount = 84200,
            commentsCount = 1890,
            sharesCount = 5400,
            savesCount = 14200,
            isLiked = false,
            isSaved = false,
            isFollowingCreator = false,
            category = "Cooking"
        ),
        Video(
            id = "video_4",
            creatorId = "user_4",
            creatorUsername = "wanderlust_maya",
            creatorDisplayName = "Maya Sky",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            caption = "Waking up above the cloud inversion in Swiss Alps 🏔️ Tell me who you would camp here with! #travel #wanderlust #viva",
            hashtags = "#travel #wanderlust #viva",
            soundId = "sound_3",
            soundTitle = "Acoustic Sunset Groove",
            soundCreator = "Elena Rostova",
            videoUrl = "raw://viva_clip_travel",
            thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&auto=format&fit=crop&q=80",
            durationSeconds = 14,
            viewsCount = 920000,
            likesCount = 132000,
            commentsCount = 2410,
            sharesCount = 8900,
            savesCount = 22100,
            isLiked = false,
            isSaved = false,
            isFollowingCreator = false,
            category = "Travel"
        )
    )

    val comments = listOf(
        Comment(
            id = "c_1",
            videoId = "video_1",
            userId = "user_2",
            username = "neon_samurai",
            userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            text = "That reverse slide at 0:08 was insanely clean! ⚡",
            likesCount = 342,
            replyCount = 1,
            isLiked = true,
            timestamp = System.currentTimeMillis() - 3600000
        ),
        Comment(
            id = "c_1_rep",
            videoId = "video_1",
            parentCommentId = "c_1",
            userId = "user_1",
            username = "elena_motion",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            text = "Thanks Kenji! Took 15 takes to get the footwork right haha ❤️",
            likesCount = 89,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 3000000
        ),
        Comment(
            id = "c_2",
            videoId = "video_1",
            userId = "user_me",
            username = "alex_viva",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            text = "VIVA trending feed algorithm brought me here and I am NOT disappointed! 🔥",
            likesCount = 120,
            replyCount = 0,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 1800000
        ),
        Comment(
            id = "c_3",
            videoId = "video_2",
            userId = "user_4",
            username = "wanderlust_maya",
            userAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            text = "Blade Runner vibes are real. What color grade lut did you use? 🎨",
            likesCount = 95,
            replyCount = 0,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 7200000
        )
    )

    val notifications = listOf(
        Notification(
            id = "notif_1",
            recipientId = "user_me",
            senderId = "user_1",
            senderUsername = "elena_motion",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            type = "FOLLOW",
            title = "New Follower",
            message = "elena_motion started following your VIVA profile.",
            timestamp = System.currentTimeMillis() - 1800000
        ),
        Notification(
            id = "notif_2",
            recipientId = "user_me",
            senderId = "user_2",
            senderUsername = "neon_samurai",
            senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            type = "LIKE",
            title = "Liked Your Post",
            message = "neon_samurai liked your latest reel video.",
            targetVideoId = "video_2",
            timestamp = System.currentTimeMillis() - 7200000
        ),
        Notification(
            id = "notif_3",
            recipientId = "user_me",
            senderId = "user_3",
            senderUsername = "chef_marcus",
            senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            type = "COMMENT",
            title = "New Comment",
            message = "chef_marcus commented: 'Awesome camera angle!'",
            targetVideoId = "video_1",
            timestamp = System.currentTimeMillis() - 14400000
        ),
        Notification(
            id = "notif_4",
            recipientId = "user_me",
            senderId = "system",
            senderUsername = "viva_official",
            senderAvatar = "",
            type = "SYSTEM",
            title = "Welcome to VIVA!",
            message = "Explore trending creators, upload short videos, and connect worldwide on VIVA.",
            timestamp = System.currentTimeMillis() - 86400000
        )
    )

    val messages = listOf(
        Message(
            id = "m_1",
            conversationId = "conv_elena",
            senderId = "user_1",
            senderName = "Elena Rostova",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            receiverId = "user_me",
            text = "Hey Alex! Loved your reaction to the dance video 🙌",
            timestamp = System.currentTimeMillis() - 5400000,
            isRead = true
        ),
        Message(
            id = "m_2",
            conversationId = "conv_elena",
            senderId = "user_me",
            senderName = "Alex Rivera",
            senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            receiverId = "user_1",
            text = "You nailed that choreo! Are you posting another one this week?",
            timestamp = System.currentTimeMillis() - 4800000,
            isRead = true
        ),
        Message(
            id = "m_3",
            conversationId = "conv_elena",
            senderId = "user_1",
            senderName = "Elena Rostova",
            senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            receiverId = "user_me",
            text = "Yes! Dropping tomorrow at 6 PM. Collaboration sound is ready on VIVA ✨",
            timestamp = System.currentTimeMillis() - 3600000,
            isRead = true
        ),
        Message(
            id = "m_4",
            conversationId = "conv_kenji",
            senderId = "user_2",
            senderName = "Kenji Cyber",
            senderAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            receiverId = "user_me",
            text = "Check out the sound track I uploaded for VIVA creators!",
            timestamp = System.currentTimeMillis() - 12000000,
            isRead = true
        )
    )

    val supportConversations = listOf(
        SupportConversation(
            id = "support_user_me",
            userId = "user_me",
            username = "alex_viva",
            userDisplayName = "Alex Rivera",
            userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            lastMessage = "Hello VIVA Support! How long does the creator verification review usually take?",
            lastMessageSenderId = "user_me",
            lastTimestamp = System.currentTimeMillis() - 3600000,
            status = "OPEN",
            unreadByAdmin = true,
            unreadByUser = false
        ),
        SupportConversation(
            id = "support_user_3",
            userId = "user_3",
            username = "chef_marcus",
            userDisplayName = "Chef Marcus",
            userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
            lastMessage = "Thanks for verifying my recipe series! The audience reaction has been incredible.",
            lastMessageSenderId = "user_3",
            lastTimestamp = System.currentTimeMillis() - 86400000,
            status = "CLOSED",
            unreadByAdmin = false,
            unreadByUser = false
        )
    )

    val adminAuditLogs = listOf(
        AdminAuditLog(
            id = "log_1",
            adminId = "user_owner",
            adminUsername = "viva_owner",
            action = "APPROVED_VERIFICATION",
            targetId = "user_1",
            targetType = "USER",
            details = "Approved creator badge for @elena_motion under Dance category.",
            timestamp = System.currentTimeMillis() - 172800000
        ),
        AdminAuditLog(
            id = "log_2",
            adminId = "user_admin",
            adminUsername = "admin_viva",
            action = "RESOLVED_REPORT",
            targetId = "report_sample_1",
            targetType = "REPORT",
            details = "Reviewed and cleared copyright flag after verifying original audio license.",
            timestamp = System.currentTimeMillis() - 86400000
        ),
        AdminAuditLog(
            id = "log_3",
            adminId = "user_owner",
            adminUsername = "viva_owner",
            action = "UPDATED_ADMIN_ROLE",
            targetId = "user_admin",
            targetType = "USER",
            details = "Assigned Trust & Safety Moderator permissions to @admin_viva.",
            timestamp = System.currentTimeMillis() - 43200000
        )
    )

    val liveStreams = listOf(
        LiveStream(
            id = "live_1",
            creatorId = "user_1",
            creatorUsername = "elena_motion",
            creatorDisplayName = "Elena Rostova",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
            title = "🔴 Tokyo Dance Jam & Freestyle Q&A! Drop song requests 🎶",
            viewerCount = 4280,
            reportsCount = 0,
            status = "ACTIVE",
            startedAt = System.currentTimeMillis() - 1800000
        ),
        LiveStream(
            id = "live_2",
            creatorId = "user_2",
            creatorUsername = "neon_samurai",
            creatorDisplayName = "Kenji Cyber",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
            title = "🔴 Cyberpunk Night Walk in Akihabara ⚡ Live 4K Stream",
            viewerCount = 1850,
            reportsCount = 1,
            status = "ACTIVE",
            startedAt = System.currentTimeMillis() - 3600000
        )
    )

    val settings = listOf(
        AppSetting("isPrivateAccount", "false"),
        AppSetting("allowComments", "true"),
        AppSetting("allowMessages", "true"),
        AppSetting("allowDownloads", "true"),
        AppSetting("showActivityStatus", "true"),
        AppSetting("pushNotifications", "true")
    )
}
