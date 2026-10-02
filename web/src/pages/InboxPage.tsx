import React, { useState } from 'react';
import { Bell, MessageSquare, Heart, UserCheck, ShieldCheck, ChevronRight, MessageCircle } from 'lucide-react';
import { VerifiedBadge } from '../components/VerifiedBadge';
import { useAuth } from '../contexts/AuthContext';
import { Notification, SupportConversation } from '../types';

interface InboxPageProps {
  onOpenSupportChat: () => void;
}

const SAMPLE_NOTIFICATIONS: Notification[] = [
  {
    id: 'n1',
    recipientId: 'user_me',
    senderId: 'user_official_viva',
    senderUsername: 'VIVA',
    senderAvatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
    type: 'VERIFICATION_UPDATE',
    title: 'Verification Request Update',
    message: 'Your creator verification profile has been officially reviewed and approved by VIVA Trust & Safety!',
    timestamp: Date.now() - 3600000 * 2,
    isRead: false
  },
  {
    id: 'n2',
    recipientId: 'user_me',
    senderId: 'user_sarah',
    senderUsername: 'sarah_dance',
    senderAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
    type: 'LIKE',
    title: 'New Like',
    message: 'sarah_dance liked your video "Neon choreography in central downtown"',
    timestamp: Date.now() - 3600000 * 5,
    isRead: true
  },
  {
    id: 'n3',
    recipientId: 'user_me',
    senderId: 'user_sarah',
    senderUsername: 'sarah_dance',
    senderAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
    type: 'FOLLOW',
    title: 'New Follower',
    message: 'sarah_dance started following you.',
    timestamp: Date.now() - 3600000 * 12,
    isRead: true
  }
];

export const InboxPage: React.FC<InboxPageProps> = ({ onOpenSupportChat }) => {
  const { currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState<'messages' | 'notifications'>('messages');
  const [notifications, setNotifications] = useState<Notification[]>(SAMPLE_NOTIFICATIONS);

  const markAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  return (
    <div className="h-full w-full bg-[#0B0B0E] overflow-y-auto safe-top pb-24 text-white">
      {/* Top Header */}
      <div className="px-4 py-3 sticky top-0 bg-[#0B0B0E]/95 backdrop-blur-md z-10 border-b border-white/5 flex items-center justify-between">
        <h1 className="text-base font-extrabold tracking-wide">Inbox & Direct Messages</h1>
        {activeTab === 'notifications' && (
          <button
            onClick={markAllRead}
            className="text-xs text-[#00F2FE] hover:underline font-semibold"
          >
            Mark all read
          </button>
        )}
      </div>

      {/* Segmented Switcher */}
      <div className="p-3 bg-[#111116] border-b border-white/5 flex gap-2">
        <button
          onClick={() => setActiveTab('messages')}
          className={`flex-1 py-2 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
            activeTab === 'messages'
              ? 'bg-[#FE2C55] text-white shadow-md shadow-[#FE2C55]/20'
              : 'bg-white/5 text-neutral-400 hover:text-white'
          }`}
        >
          <MessageSquare className="w-3.5 h-3.5" />
          <span>Messages & Support</span>
        </button>
        <button
          onClick={() => setActiveTab('notifications')}
          className={`flex-1 py-2 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 ${
            activeTab === 'notifications'
              ? 'bg-[#FE2C55] text-white shadow-md shadow-[#FE2C55]/20'
              : 'bg-white/5 text-neutral-400 hover:text-white'
          }`}
        >
          <Bell className="w-3.5 h-3.5" />
          <span>Notifications</span>
        </button>
      </div>

      {activeTab === 'messages' ? (
        <div className="p-3 space-y-2">
          {/* Pinned Official VIVA Support Desk Banner */}
          <div
            onClick={onOpenSupportChat}
            className="p-3.5 rounded-2xl bg-gradient-to-r from-neutral-900 via-neutral-900 to-purple-950/40 border border-[#00F2FE]/40 hover:border-[#00F2FE] cursor-pointer transition-all shadow-lg flex items-center justify-between"
          >
            <div className="flex items-center gap-3">
              <div className="relative">
                <img
                  src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80"
                  alt="VIVA Official"
                  className="w-12 h-12 rounded-full object-cover border-2 border-[#00F2FE]"
                />
                <span className="absolute bottom-0 right-0 w-3.5 h-3.5 bg-green-500 rounded-full border-2 border-[#121218]"></span>
              </div>
              <div>
                <div className="flex items-center gap-1.5">
                  <span className="text-sm font-bold text-white">VIVA Official Support Desk</span>
                  <VerifiedBadge size={14} />
                </div>
                <p className="text-xs text-[#00F2FE] font-medium mt-0.5">
                  Contact VIVA • Verified Administrative Communications
                </p>
                <p className="text-[11px] text-neutral-400 mt-1 line-clamp-1">
                  How can our trust & safety team help you today?
                </p>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-[10px] bg-[#FE2C55] text-white font-black px-2 py-0.5 rounded-full">
                OFFICIAL
              </span>
              <ChevronRight className="w-4 h-4 text-neutral-400" />
            </div>
          </div>

          {/* Sarah Jenkins chat thread */}
          <div
            onClick={() => alert('Direct Chat with @sarah_dance is ready! Real-time message sockets connected.')}
            className="p-3 rounded-2xl bg-[#14141D] border border-white/5 hover:border-white/20 cursor-pointer transition-all flex items-center justify-between"
          >
            <div className="flex items-center gap-3">
              <img
                src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80"
                alt="Sarah Jenkins"
                className="w-11 h-11 rounded-full object-cover"
              />
              <div>
                <div className="flex items-center gap-1">
                  <span className="text-xs font-bold text-white">Sarah Jenkins</span>
                  <VerifiedBadge size={12} />
                </div>
                <p className="text-[11px] text-neutral-400 mt-0.5">
                  Loved your new video reel! Can we collaborate?
                </p>
              </div>
            </div>
            <span className="text-[10px] text-neutral-500">2h ago</span>
          </div>
        </div>
      ) : (
        <div className="p-3 space-y-2">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`p-3.5 rounded-2xl border transition-all flex items-start gap-3 ${
                n.isRead
                  ? 'bg-[#12121A] border-white/5 text-neutral-300'
                  : 'bg-[#181824] border-[#FE2C55]/30 text-white'
              }`}
            >
              <div className="w-9 h-9 rounded-full bg-white/10 flex items-center justify-center flex-shrink-0 mt-0.5">
                {n.type === 'VERIFICATION_UPDATE' ? (
                  <ShieldCheck className="w-5 h-5 text-[#20D5EC]" />
                ) : n.type === 'LIKE' ? (
                  <Heart className="w-5 h-5 text-[#FE2C55] fill-[#FE2C55]" />
                ) : (
                  <UserCheck className="w-5 h-5 text-purple-400" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <h4 className="text-xs font-bold text-white">{n.title}</h4>
                  <span className="text-[10px] text-neutral-500">Recently</span>
                </div>
                <p className="text-xs text-neutral-300 mt-1 leading-snug">{n.message}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
