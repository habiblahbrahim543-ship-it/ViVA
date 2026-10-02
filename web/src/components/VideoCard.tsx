import React, { useState, useRef, useEffect } from 'react';
import { Heart, MessageCircle, Share2, Bookmark, Music, ShieldAlert, Plus, Check } from 'lucide-react';
import { Video } from '../types';
import { VerifiedBadge } from './VerifiedBadge';

interface VideoCardProps {
  video: Video;
  isActive: boolean;
  onOpenComments: (videoId: string) => void;
  onOpenShare: (video: Video) => void;
  onOpenReport: (videoId: string) => void;
  onCreatorClick: (creatorId: string) => void;
}

export const VideoCard: React.FC<VideoCardProps> = ({
  video,
  isActive,
  onOpenComments,
  onOpenShare,
  onOpenReport,
  onCreatorClick
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);
  const [isPlaying, setIsPlaying] = useState(true);
  const [isLiked, setIsLiked] = useState(false);
  const [likesCount, setLikesCount] = useState(video.likesCount);
  const [isSaved, setIsSaved] = useState(false);
  const [isFollowing, setIsFollowing] = useState(false);
  const [showHeartBurst, setShowHeartBurst] = useState(false);
  const [burstCoords, setBurstCoords] = useState({ x: 0, y: 0 });

  // Autoplay or pause based on active visibility
  useEffect(() => {
    if (!videoRef.current) return;
    if (isActive) {
      const playPromise = videoRef.current.play();
      if (playPromise !== undefined) {
        playPromise.catch(() => {
          // Autoplay policy: mute to play
          if (videoRef.current) {
            videoRef.current.muted = true;
            videoRef.current.play().catch(() => {});
          }
        });
      }
      setIsPlaying(true);
    } else {
      videoRef.current.pause();
      setIsPlaying(false);
    }
  }, [isActive]);

  const handleVideoTap = (e: React.MouseEvent) => {
    if (!videoRef.current) return;
    if (isPlaying) {
      videoRef.current.pause();
      setIsPlaying(false);
    } else {
      videoRef.current.play().catch(() => {});
      setIsPlaying(true);
    }
  };

  const handleDoubleTap = (e: React.MouseEvent) => {
    e.stopPropagation();
    const rect = e.currentTarget.getBoundingClientRect();
    setBurstCoords({
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    });
    setShowHeartBurst(true);
    setTimeout(() => setShowHeartBurst(false), 700);

    if (!isLiked) {
      setIsLiked(true);
      setLikesCount(prev => prev + 1);
    }
  };

  const toggleLike = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsLiked(prev => !prev);
    setLikesCount(prev => prev + (isLiked ? -1 : 1));
  };

  const toggleSave = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsSaved(prev => !prev);
  };

  const toggleFollow = (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsFollowing(prev => !prev);
  };

  return (
    <div 
      className="snap-feed-item bg-black relative flex items-center justify-center overflow-hidden"
      onClick={handleVideoTap}
      onDoubleClick={handleDoubleTap}
    >
      {/* HTML5 Video Player */}
      <video
        ref={videoRef}
        src={video.videoUrl}
        poster={video.thumbnailUrl}
        loop
        playsInline
        muted={false}
        className="w-full h-full object-cover select-none pointer-events-none"
      />

      {/* Play/Pause Center Indicator */}
      {!isPlaying && (
        <div className="absolute inset-0 flex items-center justify-center bg-black/25 pointer-events-none">
          <div className="w-16 h-16 rounded-full bg-black/60 backdrop-blur-md flex items-center justify-center border border-white/20">
            <div className="w-0 h-0 border-y-[10px] border-y-transparent border-l-[18px] border-l-white ml-1"></div>
          </div>
        </div>
      )}

      {/* Double Tap Heart Burst */}
      {showHeartBurst && (
        <div 
          className="absolute z-30 pointer-events-none animate-heart-burst"
          style={{ left: burstCoords.x - 40, top: burstCoords.y - 40 }}
        >
          <Heart className="w-20 h-20 fill-[#FE2C55] text-[#FE2C55] drop-shadow-xl" />
        </div>
      )}

      {/* Bottom Gradient overlay */}
      <div className="absolute inset-x-0 bottom-0 h-64 bg-gradient-to-t from-black/95 via-black/40 to-transparent pointer-events-none" />

      {/* Top Gradient overlay */}
      <div className="absolute inset-x-0 top-0 h-24 bg-gradient-to-b from-black/70 to-transparent pointer-events-none" />

      {/* Right Action Bar */}
      <div className="absolute right-3 bottom-20 z-20 flex flex-col items-center gap-4">
        {/* Creator Avatar & Follow Button */}
        <div className="relative mb-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              onCreatorClick(video.creatorId);
            }}
            className="w-11 h-11 rounded-full p-[2px] bg-gradient-to-tr from-[#FE2C55] to-[#00F2FE] block"
          >
            <img
              src={video.creatorAvatarUrl}
              alt={video.creatorUsername}
              className="w-full h-full rounded-full object-cover"
            />
          </button>
          {!isFollowing && (
            <button
              onClick={toggleFollow}
              className="absolute -bottom-1.5 left-1/2 -translate-x-1/2 w-5 h-5 bg-[#FE2C55] text-white rounded-full flex items-center justify-center shadow-md transition-transform active:scale-75"
              title="Follow"
            >
              <Plus className="w-3.5 h-3.5 stroke-[3]" />
            </button>
          )}
        </div>

        {/* Like Button */}
        <button onClick={toggleLike} className="flex flex-col items-center gap-1 group">
          <div className="w-11 h-11 rounded-full bg-black/40 backdrop-blur-md flex items-center justify-center text-white border border-white/10 group-active:scale-90 transition-transform">
            <Heart 
              className={`w-6 h-6 transition-colors ${
                isLiked ? 'fill-[#FE2C55] text-[#FE2C55]' : 'text-white'
              }`} 
            />
          </div>
          <span className="text-[11px] font-bold text-white shadow-black drop-shadow">
            {likesCount > 999 ? `${(likesCount / 1000).toFixed(1)}K` : likesCount}
          </span>
        </button>

        {/* Comment Button */}
        <button 
          onClick={(e) => {
            e.stopPropagation();
            onOpenComments(video.id);
          }} 
          className="flex flex-col items-center gap-1 group"
        >
          <div className="w-11 h-11 rounded-full bg-black/40 backdrop-blur-md flex items-center justify-center text-white border border-white/10 group-active:scale-90 transition-transform">
            <MessageCircle className="w-6 h-6" />
          </div>
          <span className="text-[11px] font-bold text-white shadow-black drop-shadow">
            {video.commentsCount}
          </span>
        </button>

        {/* Save / Bookmark Button */}
        <button onClick={toggleSave} className="flex flex-col items-center gap-1 group">
          <div className="w-11 h-11 rounded-full bg-black/40 backdrop-blur-md flex items-center justify-center text-white border border-white/10 group-active:scale-90 transition-transform">
            <Bookmark 
              className={`w-5 h-5 transition-colors ${
                isSaved ? 'fill-[#00F2FE] text-[#00F2FE]' : 'text-white'
              }`} 
            />
          </div>
          <span className="text-[11px] font-bold text-white shadow-black drop-shadow">
            {isSaved ? 'Saved' : 'Save'}
          </span>
        </button>

        {/* Share Button */}
        <button 
          onClick={(e) => {
            e.stopPropagation();
            onOpenShare(video);
          }} 
          className="flex flex-col items-center gap-1 group"
        >
          <div className="w-11 h-11 rounded-full bg-black/40 backdrop-blur-md flex items-center justify-center text-white border border-white/10 group-active:scale-90 transition-transform">
            <Share2 className="w-5 h-5" />
          </div>
          <span className="text-[11px] font-bold text-white shadow-black drop-shadow">
            {video.sharesCount}
          </span>
        </button>

        {/* Report Button */}
        <button 
          onClick={(e) => {
            e.stopPropagation();
            onOpenReport(video.id);
          }} 
          className="p-1 text-neutral-400 hover:text-white"
          title="Report content"
        >
          <ShieldAlert className="w-4 h-4" />
        </button>

        {/* Rotating Vinyl Sound Disc */}
        <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-neutral-800 to-black p-1 border-2 border-neutral-700 animate-spin-slow shadow-xl mt-1">
          <img
            src={video.creatorAvatarUrl}
            alt="Sound track"
            className="w-full h-full rounded-full object-cover"
          />
        </div>
      </div>

      {/* Bottom Information Overlay */}
      <div className="absolute left-3 right-16 bottom-16 z-20 select-text">
        {/* Creator Username & Verified Badge */}
        <div 
          onClick={(e) => {
            e.stopPropagation();
            onCreatorClick(video.creatorId);
          }}
          className="inline-flex items-center gap-1.5 cursor-pointer hover:underline mb-1"
        >
          <span className="font-extrabold text-white text-base drop-shadow-md">
            @{video.creatorUsername}
          </span>
          <VerifiedBadge size={16} />
        </div>

        {/* Video Caption & Hashtags */}
        <p className="text-sm text-neutral-100 font-medium leading-relaxed drop-shadow line-clamp-2">
          {video.caption}{' '}
          {video.hashtags.map((tag) => (
            <span key={tag} className="font-bold text-[#00F2FE] mr-1.5 hover:underline cursor-pointer">
              #{tag}
            </span>
          ))}
        </p>

        {/* Sound Music Track Ticker */}
        <div className="flex items-center gap-2 mt-2.5 max-w-[260px]">
          <Music className="w-3.5 h-3.5 text-neutral-300 flex-shrink-0 animate-bounce" />
          <span className="text-xs text-neutral-300 font-semibold truncate">
            {video.soundTitle} • {video.soundCreator}
          </span>
        </div>
      </div>
    </div>
  );
};
