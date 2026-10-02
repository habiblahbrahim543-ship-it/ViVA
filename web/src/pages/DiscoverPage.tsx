import React, { useState } from 'react';
import { Search, Flame, Music, Hash, TrendingUp, ChevronRight } from 'lucide-react';
import { VerifiedBadge } from '../components/VerifiedBadge';

const TRENDING_HASHTAGS = [
  { tag: 'viva', count: '14.8M', videos: '124K' },
  { tag: 'dance', count: '9.2M', videos: '89K' },
  { tag: 'cyberpunk', count: '6.4M', videos: '42K' },
  { tag: 'foodie', count: '5.1M', videos: '55K' },
  { tag: 'creator', count: '3.8M', videos: '31K' }
];

const TOP_CREATORS = [
  {
    id: 'user_official_viva',
    username: 'VIVA',
    displayName: 'VIVA Official',
    avatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
    followers: '1.25M',
    isVerified: true
  },
  {
    id: 'user_sarah',
    username: 'sarah_dance',
    displayName: 'Sarah Jenkins',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
    followers: '84.2K',
    isVerified: true
  },
  {
    id: 'user_alex',
    username: 'alex_viva',
    displayName: 'Alex Rivera',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
    followers: '14.2K',
    isVerified: true
  }
];

export const DiscoverPage: React.FC<{ onSelectCreator: (id: string) => void }> = ({ onSelectCreator }) => {
  const [query, setQuery] = useState('');

  return (
    <div className="h-full w-full bg-[#0B0B0E] overflow-y-auto safe-top pb-24">
      {/* Search Bar */}
      <div className="px-4 py-3 sticky top-0 bg-[#0B0B0E]/95 backdrop-blur-md z-10 border-b border-white/5">
        <div className="relative flex items-center">
          <Search className="w-4 h-4 text-neutral-400 absolute left-3.5" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search creators, hashtags, sounds..."
            className="w-full bg-[#181820] text-white text-xs pl-10 pr-4 py-2.5 rounded-2xl border border-white/10 focus:outline-none focus:border-[#FE2C55] placeholder-neutral-500"
          />
        </div>
      </div>

      {/* Hero Banner: Trending on VIVA */}
      <div className="px-4 py-4">
        <div className="relative overflow-hidden rounded-3xl p-5 bg-gradient-to-r from-[#FE2C55]/30 via-[#A825FF]/20 to-[#00F2FE]/20 border border-white/10 shadow-xl">
          <div className="relative z-10 max-w-[240px]">
            <span className="text-[10px] font-black uppercase tracking-wider text-[#00F2FE] bg-[#00F2FE]/20 px-2 py-0.5 rounded-full inline-block mb-1.5">
              Featured Challenge
            </span>
            <h2 className="text-xl font-black text-white leading-tight">
              #VivaRhythm 2026
            </h2>
            <p className="text-xs text-neutral-300 mt-1 leading-relaxed">
              Showcase your original beats, dance sequences, and visual aesthetics.
            </p>
          </div>
          <Flame className="w-24 h-24 text-[#FE2C55]/20 absolute -right-2 -bottom-2 pointer-events-none" />
        </div>
      </div>

      {/* Trending Hashtags */}
      <div className="px-4 py-2">
        <div className="flex items-center gap-2 mb-3">
          <TrendingUp className="w-4 h-4 text-[#FE2C55]" />
          <h3 className="text-sm font-extrabold text-white">Trending Hashtags</h3>
        </div>

        <div className="space-y-2.5">
          {TRENDING_HASHTAGS.map((item) => (
            <div
              key={item.tag}
              className="flex items-center justify-between p-3 rounded-2xl bg-[#14141B] border border-white/5 hover:border-white/20 transition-all cursor-pointer"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-neutral-800/80 flex items-center justify-center text-[#FE2C55]">
                  <Hash className="w-4 h-4 stroke-[2.5]" />
                </div>
                <div>
                  <h4 className="text-sm font-bold text-white">#{item.tag}</h4>
                  <span className="text-[11px] text-neutral-400">{item.count} views • {item.videos} clips</span>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-neutral-500" />
            </div>
          ))}
        </div>
      </div>

      {/* Verified Creators Spotlight */}
      <div className="px-4 py-4">
        <h3 className="text-sm font-extrabold text-white mb-3">Featured Creators</h3>
        <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-none">
          {TOP_CREATORS.map((creator) => (
            <div
              key={creator.id}
              onClick={() => onSelectCreator(creator.id)}
              className="flex flex-col items-center p-3.5 rounded-2xl bg-[#14141B] border border-white/5 min-w-[125px] flex-shrink-0 cursor-pointer hover:border-white/20 transition-all"
            >
              <img
                src={creator.avatar}
                alt={creator.displayName}
                className="w-14 h-14 rounded-full object-cover border-2 border-[#20D5EC] mb-2"
              />
              <div className="flex items-center gap-1">
                <span className="text-xs font-bold text-white truncate max-w-[90px]">
                  {creator.displayName}
                </span>
                <VerifiedBadge size={13} />
              </div>
              <span className="text-[11px] text-neutral-400 mt-0.5">@{creator.username}</span>
              <span className="text-[10px] text-[#FE2C55] font-semibold mt-1">{creator.followers}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
