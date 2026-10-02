import React, { useState, useRef, useEffect } from 'react';
import { Search, Radio } from 'lucide-react';
import { Video } from '../types';
import { VideoCard } from '../components/VideoCard';
import { CommentsModal } from '../components/CommentsModal';
import { ShareModal } from '../components/ShareModal';
import { ReportModal } from '../components/ReportModal';

// High quality video streams for mobile Safari & Chrome
const DEFAULT_VIDEOS: Video[] = [
  {
    id: 'v_dance_1',
    creatorId: 'user_sarah',
    creatorUsername: 'sarah_dance',
    creatorDisplayName: 'Sarah Jenkins',
    creatorAvatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
    caption: 'Neon choreography in central downtown ✨ Learning the 8-count routine with the crew!',
    hashtags: ['dance', 'vibes', 'choreography', 'viva'],
    soundId: 'sound_1',
    soundTitle: 'Midnight Echoes - VIVA Original',
    soundCreator: 'VIVA Sound Studio',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1547153760-18fc86324498?w=800&auto=format&fit=crop&q=80',
    likesCount: 14200,
    commentsCount: 384,
    sharesCount: 1205,
    viewsCount: 89400,
    createdAt: Date.now() - 3600000 * 8,
    category: 'Dance',
    allowComments: true,
    allowDownloads: true,
    isPrivate: false
  },
  {
    id: 'v_cyber_2',
    creatorId: 'user_me',
    creatorUsername: 'alex_viva',
    creatorDisplayName: 'Alex Rivera',
    creatorAvatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
    caption: 'Cyberpunk visuals captured during midnight rain in Shinjuku 🌧️ Neon reflections everywhere.',
    hashtags: ['tokyo', 'cyberpunk', 'cinematic', 'nightwalk'],
    soundId: 'sound_2',
    soundTitle: 'Neon Pulse Waves (Cyber Remix)',
    soundCreator: 'SynthMaster',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&auto=format&fit=crop&q=80',
    likesCount: 28500,
    commentsCount: 912,
    sharesCount: 3410,
    viewsCount: 145000,
    createdAt: Date.now() - 3600000 * 20,
    category: 'Visual Arts',
    allowComments: true,
    allowDownloads: true,
    isPrivate: false
  },
  {
    id: 'v_cook_3',
    creatorId: 'user_chef',
    creatorUsername: 'chef_marco',
    creatorDisplayName: 'Marco Rossi',
    creatorAvatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80',
    caption: 'Authentic 60-second hand-rolled artisan pasta with fresh basil pesto 🍝 Perfection takes time.',
    hashtags: ['foodie', 'pasta', 'chef', 'delicious'],
    soundId: 'sound_3',
    soundTitle: 'Acoustic Morning Cooking Beats',
    soundCreator: 'VIVA Kitchen',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80',
    likesCount: 9340,
    commentsCount: 184,
    sharesCount: 420,
    viewsCount: 45000,
    createdAt: Date.now() - 3600000 * 36,
    category: 'Food',
    allowComments: true,
    allowDownloads: true,
    isPrivate: false
  }
];

interface FeedPageProps {
  onOpenDiscover?: () => void;
  onOpenCreatorProfile?: (creatorId: string) => void;
}

export const FeedPage: React.FC<FeedPageProps> = ({
  onOpenDiscover,
  onOpenCreatorProfile
}) => {
  const [feedType, setFeedType] = useState<'foryou' | 'following'>('foryou');
  const [activeIndex, setActiveIndex] = useState(0);
  const containerRef = useRef<HTMLDivElement>(null);

  // Modals state
  const [activeCommentsVideoId, setActiveCommentsVideoId] = useState<string | null>(null);
  const [activeShareVideo, setActiveShareVideo] = useState<Video | null>(null);
  const [activeReportVideoId, setActiveReportVideoId] = useState<string | null>(null);

  // Monitor scroll snap to know which video is in view
  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;

    const handleScroll = () => {
      const index = Math.round(el.scrollTop / el.clientHeight);
      if (index !== activeIndex) {
        setActiveIndex(index);
      }
    };

    el.addEventListener('scroll', handleScroll, { passive: true });
    return () => el.removeEventListener('scroll', handleScroll);
  }, [activeIndex]);

  const displayedVideos = feedType === 'following' ? [DEFAULT_VIDEOS[0]] : DEFAULT_VIDEOS;

  return (
    <div className="relative h-full w-full bg-black overflow-hidden">
      {/* Top Floating Feed Navigation */}
      <div className="absolute top-0 inset-x-0 z-30 flex items-center justify-between px-4 py-3 safe-top bg-gradient-to-b from-black/80 via-black/40 to-transparent">
        {/* LIVE Stream Button */}
        <button 
          onClick={() => alert('LIVE: 4 broadcasts currently streaming on VIVA')}
          className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-white/10 backdrop-blur-md text-white text-xs font-bold border border-white/10 hover:bg-white/20 transition-colors"
        >
          <Radio className="w-3.5 h-3.5 text-[#FE2C55] animate-pulse" />
          <span>LIVE</span>
        </button>

        {/* Following / For You Tabs */}
        <div className="flex items-center gap-5 text-base font-extrabold tracking-wide">
          <button
            onClick={() => setFeedType('following')}
            className={`transition-colors relative pb-1 ${
              feedType === 'following' ? 'text-white' : 'text-neutral-400'
            }`}
          >
            Following
            {feedType === 'following' && (
              <div className="absolute bottom-0 inset-x-2 h-[3px] bg-[#FE2C55] rounded-full" />
            )}
          </button>

          <span className="text-neutral-600 text-xs">•</span>

          <button
            onClick={() => setFeedType('foryou')}
            className={`transition-colors relative pb-1 ${
              feedType === 'foryou' ? 'text-white' : 'text-neutral-400'
            }`}
          >
            For You
            {feedType === 'foryou' && (
              <div className="absolute bottom-0 inset-x-2 h-[3px] bg-white rounded-full" />
            )}
          </button>
        </div>

        {/* Search Shortcut */}
        <button
          onClick={onOpenDiscover}
          className="p-1.5 rounded-full text-white hover:bg-white/10 transition-colors"
          title="Search"
        >
          <Search className="w-5 h-5 stroke-[2.5]" />
        </button>
      </div>

      {/* Vertical Video Snap Scroll Feed */}
      <div ref={containerRef} className="snap-feed-container">
        {displayedVideos.map((video, idx) => (
          <VideoCard
            key={video.id}
            video={video}
            isActive={idx === activeIndex}
            onOpenComments={(vid) => setActiveCommentsVideoId(vid)}
            onOpenShare={(v) => setActiveShareVideo(v)}
            onOpenReport={(vid) => setActiveReportVideoId(vid)}
            onCreatorClick={(cid) => onOpenCreatorProfile(cid)}
          />
        ))}
      </div>

      {/* Modals */}
      <CommentsModal
        videoId={activeCommentsVideoId || ''}
        isOpen={!!activeCommentsVideoId}
        onClose={() => setActiveCommentsVideoId(null)}
        commentsCount={DEFAULT_VIDEOS.find(v => v.id === activeCommentsVideoId)?.commentsCount || 0}
      />

      <ShareModal
        video={activeShareVideo}
        isOpen={!!activeShareVideo}
        onClose={() => setActiveShareVideo(null)}
      />

      <ReportModal
        targetType="VIDEO"
        targetId={activeReportVideoId || ''}
        isOpen={!!activeReportVideoId}
        onClose={() => setActiveReportVideoId(null)}
      />
    </div>
  );
};
