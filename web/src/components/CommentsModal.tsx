import React, { useState } from 'react';
import { X, Send, Heart } from 'lucide-react';
import { Comment } from '../types';
import { useAuth } from '../contexts/AuthContext';

interface CommentsModalProps {
  videoId: string;
  isOpen: boolean;
  onClose: () => void;
  commentsCount: number;
}

const SAMPLE_COMMENTS: Comment[] = [
  {
    id: 'c1',
    videoId: 'v1',
    userId: 'u_dance_1',
    username: 'maya_motion',
    userDisplayName: 'Maya Motion',
    userAvatarUrl: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80',
    text: 'The transition at 0:04 was completely unreal! 🔥 Doing this tomorrow.',
    likesCount: 342,
    createdAt: Date.now() - 3600000 * 4
  },
  {
    id: 'c2',
    videoId: 'v1',
    userId: 'u_sound_2',
    username: 'beats_creator',
    userDisplayName: 'Echo Beats',
    userAvatarUrl: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&auto=format&fit=crop&q=80',
    text: 'Using this sound in my next studio session. VIVA audio quality is top notch.',
    likesCount: 129,
    createdAt: Date.now() - 3600000 * 2
  }
];

export const CommentsModal: React.FC<CommentsModalProps> = ({
  videoId,
  isOpen,
  onClose,
  commentsCount
}) => {
  const { currentUser } = useAuth();
  const [comments, setComments] = useState<Comment[]>(SAMPLE_COMMENTS);
  const [text, setText] = useState('');
  const [likedComments, setLikedComments] = useState<Record<string, boolean>>({});

  if (!isOpen) return null;

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!text.trim() || !currentUser) return;

    const newComment: Comment = {
      id: `c_${Date.now()}`,
      videoId,
      userId: currentUser.id,
      username: currentUser.username,
      userDisplayName: currentUser.displayName,
      userAvatarUrl: currentUser.avatarUrl,
      text: text.trim(),
      likesCount: 0,
      createdAt: Date.now()
    };

    setComments(prev => [newComment, ...prev]);
    setText('');
  };

  const toggleCommentLike = (commentId: string) => {
    setLikedComments(prev => ({ ...prev, [commentId]: !prev[commentId] }));
    setComments(prev => prev.map(c => {
      if (c.id === commentId) {
        const isLiked = !likedComments[commentId];
        return { ...c, likesCount: c.likesCount + (isLiked ? 1 : -1) };
      }
      return c;
    }));
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/60 backdrop-blur-sm animate-fade-in">
      <div 
        className="w-full max-w-lg bg-[#121217] rounded-t-3xl border-t border-white/10 flex flex-col max-h-[75dvh] h-[550px] shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-3.5 border-b border-white/10">
          <div className="w-6"></div>
          <span className="text-sm font-bold text-white tracking-wide">
            {comments.length} Comments
          </span>
          <button 
            onClick={onClose}
            className="p-1 text-neutral-400 hover:text-white rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Comments List */}
        <div className="flex-1 overflow-y-auto px-4 py-3 space-y-4">
          {comments.map((comment) => (
            <div key={comment.id} className="flex gap-3 items-start">
              <img
                src={comment.userAvatarUrl}
                alt={comment.username}
                className="w-9 h-9 rounded-full object-cover flex-shrink-0 border border-white/10"
              />
              <div className="flex-1 min-w-0">
                <span className="text-xs font-bold text-neutral-400">
                  @{comment.username}
                </span>
                <p className="text-sm text-white mt-0.5 leading-snug break-words">
                  {comment.text}
                </p>
                <div className="flex items-center gap-3 mt-1.5 text-[11px] text-neutral-500">
                  <span>Just now</span>
                  <button className="font-semibold hover:text-neutral-300">Reply</button>
                </div>
              </div>
              <button 
                onClick={() => toggleCommentLike(comment.id)}
                className="flex flex-col items-center gap-0.5 pt-1 text-neutral-400 hover:text-white transition-colors"
              >
                <Heart 
                  className={`w-4 h-4 transition-colors ${
                    likedComments[comment.id] ? 'fill-[#FE2C55] text-[#FE2C55]' : 'stroke-2'
                  }`} 
                />
                <span className="text-[10px] font-semibold">{comment.likesCount}</span>
              </button>
            </div>
          ))}
        </div>

        {/* Input Bar */}
        <form onSubmit={handleSend} className="p-3 border-t border-white/10 bg-black/40 safe-bottom">
          <div className="flex items-center gap-2">
            <img
              src={currentUser?.avatarUrl || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80'}
              alt="My Avatar"
              className="w-8 h-8 rounded-full object-cover border border-white/10"
            />
            <input
              type="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Add comment on VIVA..."
              className="flex-1 bg-[#1E1E26] text-white text-xs px-3.5 py-2.5 rounded-full border border-white/10 focus:outline-none focus:border-[#FE2C55] placeholder-neutral-500"
            />
            <button
              type="submit"
              disabled={!text.trim()}
              className="w-8 h-8 rounded-full bg-[#FE2C55] disabled:opacity-40 flex items-center justify-center text-white transition-transform active:scale-95"
            >
              <Send className="w-3.5 h-3.5 ml-0.5" />
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
