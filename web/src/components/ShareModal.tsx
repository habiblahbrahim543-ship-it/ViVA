import React, { useState } from 'react';
import { X, Copy, Check, Share2, MessageCircle, Twitter, Send } from 'lucide-react';
import { Video } from '../types';

interface ShareModalProps {
  video: Video | null;
  isOpen: boolean;
  onClose: () => void;
}

export const ShareModal: React.FC<ShareModalProps> = ({ video, isOpen, onClose }) => {
  const [copied, setCopied] = useState(false);

  if (!isOpen || !video) return null;

  const shareUrl = `${window.location.origin}/#video_${video.id}`;

  const handleCopyLink = () => {
    navigator.clipboard.writeText(shareUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleNativeShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: `VIVA - @${video.creatorUsername}`,
          text: video.caption,
          url: shareUrl
        });
        onClose();
      } catch (err) {}
    } else {
      handleCopyLink();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/60 backdrop-blur-sm animate-fade-in">
      <div 
        className="w-full max-w-lg bg-[#14141B] rounded-t-3xl border-t border-white/10 p-5 shadow-2xl safe-bottom"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between pb-3 border-b border-white/10">
          <span className="text-sm font-bold text-white">Share Video</span>
          <button onClick={onClose} className="p-1 text-neutral-400 hover:text-white">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Share Targets */}
        <div className="grid grid-cols-4 gap-3 py-5 text-center">
          <button 
            onClick={handleNativeShare}
            className="flex flex-col items-center gap-1.5 p-2 rounded-2xl hover:bg-white/5 transition-colors"
          >
            <div className="w-12 h-12 rounded-full bg-gradient-to-tr from-[#FE2C55] to-[#A825FF] flex items-center justify-center text-white shadow-lg">
              <Share2 className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-neutral-300">Share</span>
          </button>

          <button 
            onClick={handleCopyLink}
            className="flex flex-col items-center gap-1.5 p-2 rounded-2xl hover:bg-white/5 transition-colors"
          >
            <div className="w-12 h-12 rounded-full bg-[#20202A] flex items-center justify-center text-white border border-white/10">
              {copied ? <Check className="w-5 h-5 text-green-400" /> : <Copy className="w-5 h-5 text-neutral-300" />}
            </div>
            <span className="text-[11px] font-medium text-neutral-300">
              {copied ? 'Copied!' : 'Copy Link'}
            </span>
          </button>

          <a 
            href={`https://wa.me/?text=${encodeURIComponent(video.caption + ' ' + shareUrl)}`}
            target="_blank"
            rel="noopener noreferrer"
            className="flex flex-col items-center gap-1.5 p-2 rounded-2xl hover:bg-white/5 transition-colors"
          >
            <div className="w-12 h-12 rounded-full bg-[#25D366] flex items-center justify-center text-white shadow-lg">
              <MessageCircle className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-neutral-300">WhatsApp</span>
          </a>

          <a 
            href={`https://twitter.com/intent/tweet?text=${encodeURIComponent(video.caption)}&url=${encodeURIComponent(shareUrl)}`}
            target="_blank"
            rel="noopener noreferrer"
            className="flex flex-col items-center gap-1.5 p-2 rounded-2xl hover:bg-white/5 transition-colors"
          >
            <div className="w-12 h-12 rounded-full bg-[#1DA1F2] flex items-center justify-center text-white shadow-lg">
              <Twitter className="w-5 h-5" />
            </div>
            <span className="text-[11px] font-medium text-neutral-300">X / Twitter</span>
          </a>
        </div>
      </div>
    </div>
  );
};
