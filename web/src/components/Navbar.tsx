import React from 'react';
import { Home, Compass, Plus, MessageSquare, User } from 'lucide-react';

export type TabType = 'home' | 'discover' | 'create' | 'inbox' | 'profile';

interface NavbarProps {
  currentTab: TabType;
  onTabChange: (tab: TabType) => void;
  unreadCount?: number;
}

export const Navbar: React.FC<NavbarProps> = ({ currentTab, onTabChange, unreadCount = 0 }) => {
  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-black/95 backdrop-blur-lg border-t border-white/10 safe-bottom">
      <div className="flex items-center justify-around h-14 max-w-lg mx-auto px-2">
        {/* Home Tab */}
        <button
          onClick={() => onTabChange('home')}
          className={`flex flex-col items-center justify-center flex-1 h-full transition-colors ${
            currentTab === 'home' ? 'text-white' : 'text-neutral-500 hover:text-neutral-300'
          }`}
        >
          <Home className={`w-5 h-5 ${currentTab === 'home' ? 'stroke-[2.5]' : 'stroke-[1.8]'}`} />
          <span className={`text-[10px] mt-1 font-semibold ${currentTab === 'home' ? 'text-white' : 'text-neutral-500'}`}>
            Home
          </span>
        </button>

        {/* Discover Tab */}
        <button
          onClick={() => onTabChange('discover')}
          className={`flex flex-col items-center justify-center flex-1 h-full transition-colors ${
            currentTab === 'discover' ? 'text-white' : 'text-neutral-500 hover:text-neutral-300'
          }`}
        >
          <Compass className={`w-5 h-5 ${currentTab === 'discover' ? 'stroke-[2.5]' : 'stroke-[1.8]'}`} />
          <span className={`text-[10px] mt-1 font-semibold ${currentTab === 'discover' ? 'text-white' : 'text-neutral-500'}`}>
            Discover
          </span>
        </button>

        {/* Create (+) Central Button */}
        <button
          onClick={() => onTabChange('create')}
          className="flex items-center justify-center flex-1 h-full relative group"
          title="Create Video"
        >
          <div className="w-11 h-8 rounded-xl p-[2px] bg-gradient-to-r from-[#FE2C55] via-[#A825FF] to-[#00F2FE] shadow-lg transition-transform active:scale-90">
            <div className="w-full h-full bg-black rounded-[10px] flex items-center justify-center">
              <Plus className="w-5 h-5 text-white stroke-[3]" />
            </div>
          </div>
        </button>

        {/* Inbox Tab */}
        <button
          onClick={() => onTabChange('inbox')}
          className={`flex flex-col items-center justify-center flex-1 h-full relative transition-colors ${
            currentTab === 'inbox' ? 'text-white' : 'text-neutral-500 hover:text-neutral-300'
          }`}
        >
          <div className="relative">
            <MessageSquare className={`w-5 h-5 ${currentTab === 'inbox' ? 'stroke-[2.5]' : 'stroke-[1.8]'}`} />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -right-2 bg-[#FE2C55] text-white text-[9px] font-black px-1.5 py-0.2 rounded-full min-w-4 text-center">
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </div>
          <span className={`text-[10px] mt-1 font-semibold ${currentTab === 'inbox' ? 'text-white' : 'text-neutral-500'}`}>
            Inbox
          </span>
        </button>

        {/* Profile Tab */}
        <button
          onClick={() => onTabChange('profile')}
          className={`flex flex-col items-center justify-center flex-1 h-full transition-colors ${
            currentTab === 'profile' ? 'text-white' : 'text-neutral-500 hover:text-neutral-300'
          }`}
        >
          <User className={`w-5 h-5 ${currentTab === 'profile' ? 'stroke-[2.5]' : 'stroke-[1.8]'}`} />
          <span className={`text-[10px] mt-1 font-semibold ${currentTab === 'profile' ? 'text-white' : 'text-neutral-500'}`}>
            Profile
          </span>
        </button>
      </div>
    </nav>
  );
};
