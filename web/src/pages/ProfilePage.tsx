import React, { useState } from 'react';
import { 
  Settings, 
  Edit3, 
  ShieldCheck, 
  HelpCircle, 
  Grid, 
  Heart, 
  Bookmark, 
  ExternalLink, 
  LogOut, 
  Shield, 
  Check, 
  X,
  Share2,
  Lock
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { VerifiedBadge } from '../components/VerifiedBadge';

interface ProfilePageProps {
  onOpenAdmin: () => void;
  onOpenSupportChat: () => void;
  onOpenAuth: () => void;
}

export const ProfilePage: React.FC<ProfilePageProps> = ({
  onOpenAdmin,
  onOpenSupportChat,
  onOpenAuth
}) => {
  const { currentUser, isAdmin, isOwner, updateUserProfile, logout } = useAuth();
  
  const [activeTab, setActiveTab] = useState<'videos' | 'liked' | 'saved'>('videos');
  const [showEditModal, setShowEditModal] = useState(false);
  const [showVerificationModal, setShowVerificationModal] = useState(false);

  // Edit profile state
  const [editDisplayName, setEditDisplayName] = useState(currentUser?.displayName || '');
  const [editBio, setEditBio] = useState(currentUser?.bio || '');
  const [editWebsite, setEditWebsite] = useState(currentUser?.website || '');
  const [editError, setEditError] = useState('');

  // Verification request form state
  const [vFullName, setVFullName] = useState('');
  const [vCategory, setVCategory] = useState('Creator / Influencer');
  const [vCountry, setVCountry] = useState('United States');
  const [vReason, setVReason] = useState('');
  const [vLinks, setVLinks] = useState('');
  const [verificationSubmitted, setVerificationSubmitted] = useState(false);

  if (!currentUser) {
    return (
      <div className="h-full w-full bg-[#0B0B0E] flex flex-col items-center justify-center p-6 text-center text-white">
        <h2 className="text-lg font-bold">Sign in to view your profile</h2>
        <p className="text-xs text-neutral-400 mt-2 mb-4">Connect with creators, upload videos, and request verification.</p>
        <button
          onClick={onOpenAuth}
          className="viva-gradient-btn px-6 py-2.5 rounded-xl font-bold text-xs"
        >
          Sign In / Create Account
        </button>
      </div>
    );
  }

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setEditError('');
    const res = await updateUserProfile({
      displayName: editDisplayName,
      bio: editBio,
      website: editWebsite
    });
    if (!res.success) {
      setEditError(res.error || 'Failed to update profile');
      return;
    }
    setShowEditModal(false);
  };

  const handleSubmitVerification = (e: React.FormEvent) => {
    e.preventDefault();
    if (!vFullName.trim() || !vReason.trim()) {
      alert('Please fill out all required fields.');
      return;
    }
    setVerificationSubmitted(true);
    setTimeout(() => {
      setVerificationSubmitted(false);
      setShowVerificationModal(false);
      alert('Verification application submitted to VIVA Trust & Safety!');
    }, 1200);
  };

  return (
    <div className="h-full w-full bg-[#0B0B0E] overflow-y-auto safe-top pb-24 text-white">
      {/* Top App Bar */}
      <div className="px-4 py-3 sticky top-0 bg-[#0B0B0E]/95 backdrop-blur-md z-10 border-b border-white/5 flex items-center justify-between">
        <div className="flex items-center gap-1.5">
          <span className="font-extrabold text-sm tracking-wide">@{currentUser.username}</span>
          {currentUser.isVerified && <VerifiedBadge size={15} />}
        </div>
        <div className="flex items-center gap-2">
          {isAdmin && (
            <button
              onClick={onOpenAdmin}
              className="px-2.5 py-1 rounded-xl bg-purple-500/20 text-purple-300 border border-purple-500/40 text-[11px] font-bold flex items-center gap-1 hover:bg-purple-500/30 transition-all"
            >
              <Shield className="w-3.5 h-3.5" />
              <span>Admin Panel</span>
            </button>
          )}
          <button
            onClick={onOpenAuth}
            className="p-1.5 rounded-xl hover:bg-white/10 text-neutral-300"
            title="Switch Accounts"
          >
            <Settings className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Profile Bio Section */}
      <div className="px-5 pt-4 pb-2 text-center flex flex-col items-center">
        <div className="relative">
          <img
            src={currentUser.avatarUrl}
            alt={currentUser.displayName}
            className="w-24 h-24 rounded-full object-cover border-2 border-white/20 shadow-xl"
          />
          {currentUser.role !== 'USER' && (
            <span className="absolute bottom-0 right-0 bg-amber-400 text-black text-[9px] font-black px-1.5 py-0.5 rounded-full shadow">
              {currentUser.role}
            </span>
          )}
        </div>

        <div className="flex items-center gap-1.5 mt-3">
          <h2 className="text-base font-extrabold">{currentUser.displayName}</h2>
          {currentUser.isVerified && <VerifiedBadge size={16} />}
        </div>

        <p className="text-xs text-neutral-400 mt-0.5 font-medium">@{currentUser.username}</p>

        {/* Stats Row */}
        <div className="flex items-center justify-center gap-6 mt-4 py-2 border-y border-white/5 w-full max-w-xs">
          <div className="text-center">
            <span className="block font-black text-sm">{currentUser.followingCount.toLocaleString()}</span>
            <span className="text-[10px] text-neutral-400 uppercase font-semibold">Following</span>
          </div>
          <div className="h-6 w-px bg-white/10" />
          <div className="text-center">
            <span className="block font-black text-sm">{currentUser.followersCount.toLocaleString()}</span>
            <span className="text-[10px] text-neutral-400 uppercase font-semibold">Followers</span>
          </div>
          <div className="h-6 w-px bg-white/10" />
          <div className="text-center">
            <span className="block font-black text-sm">{currentUser.likesCount.toLocaleString()}</span>
            <span className="text-[10px] text-neutral-400 uppercase font-semibold">Likes</span>
          </div>
        </div>

        {/* Bio & Link */}
        <p className="text-xs text-neutral-300 mt-3 max-w-sm px-4 leading-relaxed">
          {currentUser.bio}
        </p>

        {currentUser.website && (
          <a
            href={currentUser.website}
            target="_blank"
            rel="noopener noreferrer"
            className="text-xs text-[#00F2FE] hover:underline flex items-center gap-1 mt-1.5 font-medium"
          >
            <ExternalLink className="w-3.5 h-3.5" />
            <span>{currentUser.website.replace(/^https?:\/\//, '')}</span>
          </a>
        )}

        {/* Action Buttons Row */}
        <div className="flex flex-wrap items-center justify-center gap-2 mt-4 w-full max-w-sm">
          <button
            onClick={() => {
              setEditDisplayName(currentUser.displayName);
              setEditBio(currentUser.bio);
              setEditWebsite(currentUser.website);
              setShowEditModal(true);
            }}
            className="flex-1 py-2 px-3 rounded-xl bg-white/10 hover:bg-white/15 text-white text-xs font-bold flex items-center justify-center gap-1.5 transition-colors border border-white/10"
          >
            <Edit3 className="w-3.5 h-3.5" />
            <span>Edit Profile</span>
          </button>

          {!currentUser.isVerified && (
            <button
              onClick={() => setShowVerificationModal(true)}
              className="py-2 px-3 rounded-xl bg-[#20D5EC]/15 hover:bg-[#20D5EC]/25 text-[#20D5EC] text-xs font-bold flex items-center justify-center gap-1.5 transition-colors border border-[#20D5EC]/30"
            >
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Get Verified</span>
            </button>
          )}

          <button
            onClick={onOpenSupportChat}
            className="py-2 px-3 rounded-xl bg-white/10 hover:bg-white/15 text-white text-xs font-bold flex items-center justify-center gap-1.5 transition-colors border border-white/10"
            title="Contact VIVA Support"
          >
            <HelpCircle className="w-3.5 h-3.5 text-[#00F2FE]" />
            <span>Contact VIVA</span>
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center justify-around border-t border-b border-white/10 mt-4 bg-[#111116]">
        <button
          onClick={() => setActiveTab('videos')}
          className={`flex-1 py-3 flex justify-center border-b-2 transition-all ${
            activeTab === 'videos' ? 'border-[#FE2C55] text-white' : 'border-transparent text-neutral-500'
          }`}
        >
          <Grid className="w-5 h-5" />
        </button>
        <button
          onClick={() => setActiveTab('liked')}
          className={`flex-1 py-3 flex justify-center border-b-2 transition-all ${
            activeTab === 'liked' ? 'border-[#FE2C55] text-white' : 'border-transparent text-neutral-500'
          }`}
        >
          <Heart className="w-5 h-5" />
        </button>
        <button
          onClick={() => setActiveTab('saved')}
          className={`flex-1 py-3 flex justify-center border-b-2 transition-all ${
            activeTab === 'saved' ? 'border-[#FE2C55] text-white' : 'border-transparent text-neutral-500'
          }`}
        >
          <Bookmark className="w-5 h-5" />
        </button>
      </div>

      {/* Video Grid */}
      <div className="grid grid-cols-3 gap-0.5 p-0.5">
        {[
          { id: '1', views: '89.4K', thumb: 'https://images.unsplash.com/photo-1547153760-18fc86324498?w=800&auto=format&fit=crop&q=80' },
          { id: '2', views: '145K', thumb: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&auto=format&fit=crop&q=80' },
          { id: '3', views: '42.1K', thumb: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80' }
        ].map((item) => (
          <div key={item.id} className="relative aspect-[9/16] bg-neutral-900 group cursor-pointer overflow-hidden">
            <img src={item.thumb} alt="thumbnail" className="w-full h-full object-cover group-hover:scale-105 transition-transform" />
            <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent flex items-end p-2">
              <span className="text-[10px] font-bold text-white flex items-center gap-1">
                ▶ {item.views}
              </span>
            </div>
          </div>
        ))}
      </div>

      {/* Edit Profile Modal */}
      {showEditModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-fade-in">
          <div className="w-full max-w-sm bg-[#161622] rounded-3xl border border-white/10 p-5 shadow-2xl safe-bottom">
            <div className="flex items-center justify-between pb-3 border-b border-white/10">
              <span className="text-sm font-bold text-white">Edit Profile</span>
              <button onClick={() => setShowEditModal(false)} className="p-1 text-neutral-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            {editError && (
              <p className="mt-3 text-xs text-red-400 bg-red-500/10 p-2.5 rounded-xl border border-red-500/20">{editError}</p>
            )}

            <form onSubmit={handleSaveProfile} className="mt-4 space-y-3">
              <div>
                <label className="text-xs text-neutral-400 block mb-1">Display Name</label>
                <input
                  type="text"
                  value={editDisplayName}
                  onChange={(e) => setEditDisplayName(e.target.value)}
                  className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
                />
              </div>

              <div>
                <label className="text-xs text-neutral-400 block mb-1">Bio</label>
                <textarea
                  value={editBio}
                  onChange={(e) => setEditBio(e.target.value)}
                  rows={3}
                  className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55] resize-none"
                />
              </div>

              <div>
                <label className="text-xs text-neutral-400 block mb-1">Website URL</label>
                <input
                  type="url"
                  value={editWebsite}
                  onChange={(e) => setEditWebsite(e.target.value)}
                  className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="flex-1 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-white text-xs font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl viva-gradient-btn text-white text-xs font-bold shadow-lg"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Verification Request Modal */}
      {showVerificationModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-fade-in">
          <div className="w-full max-w-md bg-[#161622] rounded-3xl border border-white/10 p-5 shadow-2xl safe-bottom max-h-[90dvh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-white/10">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-[#20D5EC]" />
                <span className="text-sm font-bold text-white">Apply for Verified Badge</span>
              </div>
              <button onClick={() => setShowVerificationModal(false)} className="p-1 text-neutral-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            {verificationSubmitted ? (
              <div className="py-8 text-center flex flex-col items-center">
                <div className="w-14 h-14 rounded-full bg-[#20D5EC]/20 text-[#20D5EC] flex items-center justify-center mb-3">
                  <Check className="w-8 h-8" />
                </div>
                <h4 className="font-bold text-white text-base">Application Submitted</h4>
                <p className="text-xs text-neutral-400 mt-1 max-w-[260px]">
                  VIVA Trust & Safety will verify your identity and public authenticity within 24-48 hours.
                </p>
              </div>
            ) : (
              <form onSubmit={handleSubmitVerification} className="mt-4 space-y-3">
                <div>
                  <label className="text-xs font-semibold text-neutral-400 block mb-1">Full Legal Name *</label>
                  <input
                    type="text"
                    required
                    value={vFullName}
                    onChange={(e) => setVFullName(e.target.value)}
                    placeholder="e.g. Alexander Rivera"
                    className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#20D5EC]"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-neutral-400 block mb-1">Category *</label>
                  <select
                    value={vCategory}
                    onChange={(e) => setVCategory(e.target.value)}
                    className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#20D5EC]"
                  >
                    <option value="Creator / Influencer">Creator / Influencer</option>
                    <option value="Artist / Musician">Artist / Musician</option>
                    <option value="Brand / Organization">Brand / Organization</option>
                    <option value="Athlete / Sports">Athlete / Sports</option>
                    <option value="Journalist / Media">Journalist / Media</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-neutral-400 block mb-1">Country / Region</label>
                  <input
                    type="text"
                    value={vCountry}
                    onChange={(e) => setVCountry(e.target.value)}
                    className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#20D5EC]"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-neutral-400 block mb-1">Social Profiles & Press Links</label>
                  <textarea
                    rows={2}
                    value={vLinks}
                    onChange={(e) => setVLinks(e.target.value)}
                    placeholder="Instagram: @alex, YouTube: @alex, Press article links..."
                    className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#20D5EC] resize-none"
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-neutral-400 block mb-1">Reason for Verification *</label>
                  <textarea
                    rows={2}
                    required
                    value={vReason}
                    onChange={(e) => setVReason(e.target.value)}
                    placeholder="Describe your public interest and reason to prevent impersonation..."
                    className="w-full bg-[#20202E] text-white text-xs px-3 py-2.5 rounded-xl border border-white/10 focus:outline-none focus:border-[#20D5EC] resize-none"
                  />
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setShowVerificationModal(false)}
                    className="flex-1 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-white text-xs font-bold"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="flex-1 py-2.5 rounded-xl bg-[#20D5EC] text-black text-xs font-bold hover:bg-[#20D5EC]/90 shadow-lg"
                  >
                    Submit Request
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
