package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VivaDatabase
import com.example.data.model.User
import com.example.data.repository.VivaRepository
import com.example.ui.components.resolveVideoUri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: VivaDatabase
    private lateinit var repository: VivaRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, VivaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = VivaRepository(db.vivaDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context verifies VIVA branding`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("VIVA", appName)
    }

    @Test
    fun `test video URI resolver handles local raw clips safely`() {
        val uri = resolveVideoUri(context, "raw://viva_clip_dance")
        assertNotNull(uri)
        assertTrue(uri.toString().startsWith("android.resource://"))
    }

    @Test
    fun `test user contact viva and admin reply flow`() = runTest {
        val testUser = User(
            id = "test_user_1",
            username = "music_creator",
            displayName = "Music Creator",
            avatarUrl = "https://example.com/avatar.jpg",
            isCurrentUser = true,
            role = "USER"
        )
        db.vivaDao().insertUser(testUser)

        // 1. User contacts VIVA
        val sendResult = repository.sendContactVivaMessage("Hello VIVA Support, I need help with verification.")
        assertTrue(sendResult.isSuccess)

        // 2. Verify support conversation is open and unread by admin
        val conv = repository.getSupportConversationForUser("test_user_1").first()
        assertNotNull(conv)
        assertEquals("OPEN", conv?.status)
        assertTrue(conv?.unreadByAdmin == true)
        assertEquals("Hello VIVA Support, I need help with verification.", conv?.lastMessage)

        // 3. Verify admin notification generated
        val adminNotifs = repository.getAllAdminNotifications().first()
        val messageNotif = adminNotifs.find { it.type == "MESSAGE" }
        assertNotNull(messageNotif)
        assertTrue(messageNotif?.message?.contains("music_creator") == true)

        // 4. Admin replies to user
        val adminUser = User(
            id = "user_admin",
            username = "admin_viva",
            displayName = "VIVA Staff Moderator",
            avatarUrl = "",
            role = "ADMIN"
        )
        val replyResult = repository.sendAdminReply(conv!!.id, "Hello! We have reviewed your account and will assist you.", adminUser)
        assertTrue(replyResult.isSuccess)

        // 5. Verify user notification generated
        val userNotifs = repository.getAllNotifications().first()
        val userNotif = userNotifs.find { it.recipientId == "test_user_1" }
        assertNotNull(userNotif)
        assertEquals("VIVA Support replied to your message.", userNotif?.title)

        // 6. Verify conversation updated with unreadByUser
        val updatedConv = repository.getSupportConversationForUser("test_user_1").first()
        assertTrue(updatedConv?.unreadByUser == true)
        assertFalse(updatedConv?.unreadByAdmin == true)
    }

    @Test
    fun `test user verification request and admin approval flow`() = runTest {
        val applicant = User(
            id = "applicant_1",
            username = "creator_pro",
            displayName = "Creator Pro",
            avatarUrl = "https://example.com/creator.jpg",
            isCurrentUser = true,
            isVerified = false,
            verificationStatus = "unverified",
            role = "USER"
        )
        db.vivaDao().insertUser(applicant)

        // 1. User submits verification request
        val requestResult = repository.submitVerificationRequest(
            fullName = "Alex Pro",
            category = "Creator",
            country = "United States",
            reason = "Active creator with 50K followers and original dance videos.",
            website = "https://creator.pro",
            instagram = "@creator_pro",
            youtube = "CreatorProOfficial",
            tiktok = "@creator_pro",
            otherLinks = "",
            supportingDocuments = "Press articles & passport"
        )
        assertTrue(requestResult.isSuccess)

        // 2. Admin approves verification
        val req = requestResult.getOrNull()!!
        repository.approveVerificationForUser(
            requestId = req.id,
            targetUserId = applicant.id,
            adminUsername = "admin_viva"
        )

        // 3. Verify user account is now officially verified
        val verifiedUser = db.vivaDao().getUserByIdSync(applicant.id)
        assertNotNull(verifiedUser)
        assertTrue(verifiedUser!!.isVerified)
        assertEquals("approved", verifiedUser.verificationStatus)

        // 4. Verify user notification
        val userNotifs = repository.getAllNotifications().first()
        val approvalNotif = userNotifs.find { it.recipientId == applicant.id && it.title.contains("verified") }
        assertNotNull(approvalNotif)

        // 5. Verify audit log entry
        val auditLogs = repository.allAuditLogs.first()
        val audit = auditLogs.find { it.action == "APPROVED_VERIFICATION" && it.targetId == applicant.id }
        assertNotNull(audit)
    }

    @Test
    fun `test reserved handle protection and role security`() = runTest {
        val normalUser = User(
            id = "normal_1",
            username = "regular_joe",
            displayName = "Joe",
            avatarUrl = "https://example.com/joe.jpg",
            role = "USER",
            isCurrentUser = true
        )
        db.vivaDao().insertUser(normalUser)

        // Trying to rename to reserved handle @VIVA must fail
        val result = repository.updateUserProfile(
            userId = normalUser.id,
            displayName = "Joe",
            username = "VIVA",
            bio = "Fake VIVA",
            website = "",
            avatarUrl = ""
        )
        // Verify failure or prevention
        val updated = db.vivaDao().getUserByIdSync(normalUser.id)
        assertNotEquals("VIVA", updated?.username)
    }
}
