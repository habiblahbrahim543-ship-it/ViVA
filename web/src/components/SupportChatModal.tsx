import React, { useState, useEffect, useRef } from 'react';
import { X, Send, Paperclip, Shield, CheckCheck, Clock } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { VerifiedBadge } from './VerifiedBadge';
import { SupportMessage } from '../types';

interface SupportChatModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialTopic?: string;
}

const SUPPORT_TOPICS = [
  'Verification Status',
  'Report Issue',
  'Account Security',
  'Creator Tools',
  'Audio & Copyright'
];

export const SupportChatModal: React.FC<SupportChatModalProps> = ({
  isOpen,
  onClose,
  initialTopic
}) => {
  const { currentUser } = useAuth();
  const [messages, setMessages] = useState<SupportMessage[]>([
    {
      id: 'msg_welcome',
      conversationId: 'conv_1',
      senderId: 'user_official_viva',
      senderName: 'VIVA Official Support',
      senderAvatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
      text: 'Hello! Welcome to the official VIVA Help & Verification Desk. How can our administrative team help you today?',
      timestamp: Date.now() - 1000 * 60 * 30,
      isRead: true
    }
  ]);
  const [inputText, setInputText] = useState(initialTopic ? `Hi VIVA team, I have a question regarding ${initialTopic}.` : '');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (initialTopic) {
      setInputText(`Hi VIVA team, I have a question regarding ${initialTopic}.`);
    }
  }, [initialTopic]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  if (!isOpen) return null;

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim() || !currentUser) return;

    const userMsg: SupportMessage = {
      id: `msg_${Date.now()}`,
      conversationId: 'conv_1',
      senderId: currentUser.id,
      senderName: currentUser.displayName,
      senderAvatar: currentUser.avatarUrl,
      text: inputText.trim(),
      timestamp: Date.now(),
      isRead: false
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputText('');

    // Simulate official automated receipt confirmation if not already answered
    setTimeout(() => {
      const replyMsg: SupportMessage = {
        id: `msg_reply_${Date.now()}`,
        conversationId: 'conv_1',
        senderId: 'user_official_viva',
        senderName: 'VIVA Official Support',
        senderAvatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
        text: 'Thank you for contacting VIVA! A verified support team member has received your request and will respond directly to your notification center.',
        timestamp: Date.now(),
        isRead: true
      };
      setMessages((prev) => [...prev, replyMsg]);
    }, 1200);
  };

  const formatTime = (ts: number) => {
    const d = new Date(ts);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-2 sm:p-4 animate-fade-in">
      <div 
        className="w-full max-w-lg h-[92dvh] bg-[#121218] rounded-3xl border border-white/10 flex flex-col shadow-2xl overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-4 py-3.5 bg-[#181822] border-b border-white/10 flex items-center justify-between flex-shrink-0">
          <div className="flex items-center gap-3">
            <div className="relative">
              <img
                src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80"
                alt="VIVA Support"
                className="w-10 h-10 rounded-full border border-[#00F2FE]/50 object-cover"
              />
              <span className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-[#181822]"></span>
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-sm font-bold text-white">Contact VIVA</span>
                <VerifiedBadge size={14} />
              </div>
              <p className="text-[11px] text-[#00F2FE] flex items-center gap-1 font-medium">
                <Shield className="w-3 h-3" /> Official Support Desk • Online
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-neutral-400 hover:text-white rounded-full hover:bg-white/5 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Quick Topic Chips */}
        <div className="px-3 py-2 bg-[#14141E] border-b border-white/5 flex gap-2 overflow-x-auto no-scrollbar flex-shrink-0">
          {SUPPORT_TOPICS.map((topic) => (
            <button
              key={topic}
              onClick={() => setInputText(`Hi VIVA team, regarding ${topic}: `)}
              className="text-[11px] px-3 py-1.5 rounded-full bg-white/5 hover:bg-white/10 text-neutral-300 hover:text-white border border-white/10 whitespace-nowrap transition-colors flex-shrink-0"
            >
              {topic}
            </button>
          ))}
        </div>

        {/* Messages List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3.5">
          {messages.map((msg) => {
            const isMe = msg.senderId === currentUser?.id;
            return (
              <div
                key={msg.id}
                className={`flex gap-2.5 max-w-[85%] ${isMe ? 'ml-auto flex-row-reverse' : 'mr-auto'}`}
              >
                {!isMe && (
                  <img
                    src={msg.senderAvatar}
                    alt={msg.senderName}
                    className="w-7 h-7 rounded-full object-cover flex-shrink-0 mt-0.5 border border-white/10"
                  />
                )}
                <div>
                  <div
                    className={`rounded-2xl px-4 py-2.5 text-xs leading-relaxed shadow-md ${
                      isMe
                        ? 'bg-gradient-to-r from-[#FE2C55] to-[#A825FF] text-white rounded-tr-none'
                        : 'bg-[#22222E] text-neutral-100 rounded-tl-none border border-white/10'
                    }`}
                  >
                    {!isMe && (
                      <span className="text-[10px] text-[#00F2FE] font-bold block mb-1">
                        {msg.senderName}
                      </span>
                    )}
                    {msg.text}
                  </div>
                  <div className={`flex items-center gap-1 mt-1 text-[9px] text-neutral-500 ${isMe ? 'justify-end' : 'justify-start'}`}>
                    <span>{formatTime(msg.timestamp)}</span>
                    {isMe && (
                      <span className="text-[#00F2FE] flex items-center">
                        <CheckCheck className="w-3 h-3" />
                      </span>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Bar */}
        <form onSubmit={handleSend} className="p-3 bg-[#181822] border-t border-white/10 flex items-center gap-2 safe-bottom flex-shrink-0">
          <button
            type="button"
            className="p-2.5 text-neutral-400 hover:text-white rounded-xl hover:bg-white/5 transition-colors"
            title="Attach Screenshot"
            onClick={() => alert('Attachments can be linked directly or uploaded with video proof.')}
          >
            <Paperclip className="w-4 h-4" />
          </button>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            placeholder="Type your message to VIVA Support..."
            className="flex-1 bg-[#22222E] text-white text-xs px-4 py-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#00F2FE] placeholder-neutral-500"
          />
          <button
            type="submit"
            disabled={!inputText.trim()}
            className="p-3 rounded-xl bg-gradient-to-r from-[#FE2C55] to-[#00F2FE] text-white font-bold disabled:opacity-40 transition-transform active:scale-95 shadow-md"
          >
            <Send className="w-4 h-4" />
          </button>
        </form>
      </div>
    </div>
  );
};
