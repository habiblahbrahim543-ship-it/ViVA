import React, { useState } from 'react';
import { useAuth } from './contexts/AuthContext';
import { Navbar, TabType } from './components/Navbar';
import { FeedPage } from './pages/FeedPage';
import { DiscoverPage } from './pages/DiscoverPage';
import { CreatePage } from './pages/CreatePage';
import { InboxPage } from './pages/InboxPage';
import { ProfilePage } from './pages/ProfilePage';
import { AdminPage } from './pages/AdminPage';
import { SupportChatModal } from './components/SupportChatModal';
import { AuthModal } from './components/AuthModal';
import { InstallPwaPrompt } from './components/InstallPwaPrompt';

export const App: React.FC = () => {
  const { currentUser, isAdmin } = useAuth();
  const [currentTab, setCurrentTab] = useState<TabType>('home');
  const [isAdminMode, setIsAdminMode] = useState(false);
  const [showSupportModal, setShowSupportModal] = useState(false);
  const [showAuthModal, setShowAuthModal] = useState(false);

  // If in dedicated admin view
  if (isAdminMode) {
    return (
      <div className="h-full w-full bg-black text-white flex justify-center">
        <div className="w-full max-w-2xl h-full flex flex-col bg-[#0B0B0E] relative shadow-2xl overflow-hidden">
          <AdminPage onBackToApp={() => setIsAdminMode(false)} />
        </div>
      </div>
    );
  }

  return (
    <div className="h-full w-full bg-black text-white flex justify-center select-none overflow-hidden">
      {/* Mobile container - Responsive frame: Max 500px wide on desktop / tablet, 100% on phones */}
      <div className="w-full max-w-[500px] h-full flex flex-col bg-black relative shadow-2xl overflow-hidden">
        {/* PWA Add to Home Screen Prompt */}
        <InstallPwaPrompt />

        {/* Dynamic Pages */}
        <main className="flex-1 w-full h-full relative overflow-hidden">
          {currentTab === 'home' && (
            <FeedPage
              onOpenDiscover={() => setCurrentTab('discover')}
              onOpenCreatorProfile={() => setCurrentTab('profile')}
            />
          )}

          {currentTab === 'discover' && (
            <DiscoverPage
              onSelectCreator={() => setCurrentTab('profile')}
            />
          )}

          {currentTab === 'create' && (
            <CreatePage
              onPublishSuccess={() => setCurrentTab('home')}
              onCancel={() => setCurrentTab('home')}
            />
          )}

          {currentTab === 'inbox' && (
            <InboxPage
              onOpenSupportChat={() => setShowSupportModal(true)}
            />
          )}

          {currentTab === 'profile' && (
            <ProfilePage
              onOpenAdmin={() => setIsAdminMode(true)}
              onOpenSupportChat={() => setShowSupportModal(true)}
              onOpenAuth={() => setShowAuthModal(true)}
            />
          )}
        </main>

        {/* Global Bottom Navigation (hidden on Create page for maximum canvas space) */}
        {currentTab !== 'create' && (
          <Navbar
            currentTab={currentTab}
            onTabChange={(tab) => setCurrentTab(tab)}
            unreadCount={1}
          />
        )}

        {/* Support Chat Overlay Modal */}
        <SupportChatModal
          isOpen={showSupportModal}
          onClose={() => setShowSupportModal(false)}
        />

        {/* Authentication & Account Switcher Modal */}
        <AuthModal
          isOpen={showAuthModal}
          onClose={() => setShowAuthModal(false)}
        />
      </div>
    </div>
  );
};
export default App;
