export interface User {
  id: string;
  username: string;
  displayName: string;
  avatarUrl: string;
  bio: string;
  website: string;
  followersCount: number;
  followingCount: number;
  likesCount: number;
  isVerified: boolean;
  verificationCategory?: string;
  role: 'USER' | 'ADMIN' | 'OWNER';
  accountStatus: 'ACTIVE' | 'SUSPENDED' | 'BANNED';
  email: string;
  createdAt: number;
}

export interface Video {
  id: string;
  creatorId: string;
  creatorUsername: string;
  creatorDisplayName: string;
  creatorAvatarUrl: string;
  caption: string;
  hashtags: string[];
  soundId: string;
  soundTitle: string;
  soundCreator: string;
  videoUrl: string;
  thumbnailUrl: string;
  likesCount: number;
  commentsCount: number;
  sharesCount: number;
  viewsCount: number;
  createdAt: number;
  category: string;
  allowComments: boolean;
  allowDownloads: boolean;
  isPrivate: boolean;
}

export interface Comment {
  id: string;
  videoId: string;
  userId: string;
  username: string;
  userDisplayName: string;
  userAvatarUrl: string;
  text: string;
  likesCount: number;
  createdAt: number;
  parentId?: string | null;
}

export interface Notification {
  id: string;
  recipientId: string;
  senderId: string;
  senderUsername: string;
  senderAvatar: string;
  type: 'LIKE' | 'COMMENT' | 'FOLLOW' | 'VERIFICATION_UPDATE' | 'SUPPORT_REPLY' | 'SYSTEM';
  title: string;
  message: string;
  timestamp: number;
  isRead: boolean;
  targetId?: string;
}

export interface VerificationRequest {
  id: string;
  userId: string;
  username: string;
  fullName: string;
  category: string;
  country: string;
  reason: string;
  website: string;
  instagram: string;
  youtube: string;
  tiktok: string;
  otherLinks: string;
  supportingDocuments: string;
  status: 'pending' | 'under_review' | 'approved' | 'rejected' | 'more_information_required';
  adminNotes: string;
  createdAt: number;
  updatedAt: number;
}

export interface SupportConversation {
  id: string;
  userId: string;
  username: string;
  userDisplayName: string;
  userAvatar: string;
  lastMessage: string;
  lastTimestamp: number;
  unreadByAdmin: boolean;
  unreadByUser: boolean;
  status: 'OPEN' | 'CLOSED';
}

export interface SupportMessage {
  id: string;
  conversationId: string;
  senderId: string;
  senderName: string;
  senderAvatar: string;
  text: string;
  timestamp: number;
  isRead: boolean;
  attachmentUrl?: string;
}

export interface AdminNotification {
  id: string;
  type: 'VERIFICATION_REQUEST' | 'MESSAGE' | 'REPORT' | 'SECURITY';
  title: string;
  message: string;
  targetId: string;
  timestamp: number;
  isRead: boolean;
}

export interface ModerationReport {
  id: string;
  targetType: 'VIDEO' | 'USER' | 'COMMENT' | 'LIVE';
  targetId: string;
  reporterId: string;
  reporterName: string;
  reason: string;
  notes: string;
  status: 'PENDING' | 'RESOLVED' | 'DISMISSED';
  timestamp: number;
}

export interface LiveStream {
  id: string;
  creatorId: string;
  creatorUsername: string;
  creatorDisplayName: string;
  creatorAvatarUrl: string;
  title: string;
  category: string;
  viewerCount: number;
  likesCount: number;
  streamUrl: string;
  status: 'ACTIVE' | 'ENDED';
  reportsCount: number;
  startedAt: number;
}

export interface AdminAuditLog {
  id: string;
  adminId: string;
  adminUsername: string;
  action: string;
  targetId: string;
  details: string;
  timestamp: number;
}
