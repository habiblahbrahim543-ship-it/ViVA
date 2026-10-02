import React, { useState } from 'react';
import { X, LogIn, UserPlus, Sparkles, AlertCircle } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { VerifiedBadge } from './VerifiedBadge';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({ isOpen, onClose }) => {
  const { sampleUsers, currentUser, switchAccount } = useAuth();
  const [isSignUp, setIsSignUp] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [username, setUsername] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage('');

    if (isSignUp) {
      const cleanUser = username.trim().replace(/^@/, '').toLowerCase();
      if (!cleanUser) {
        setErrorMessage('Username is required.');
        return;
      }
      if (['viva', 'admin', 'owner', 'support'].includes(cleanUser)) {
        setErrorMessage(`@${cleanUser} is strictly reserved for official VIVA platform administration.`);
        return;
      }
      alert(`Welcome to VIVA, ${displayName || cleanUser}! Your account has been registered.`);
      onClose();
    } else {
      if (!email.trim() || !password.trim()) {
        setErrorMessage('Please provide your email and password.');
        return;
      }
      alert('Welcome back to VIVA!');
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-md p-4 animate-fade-in">
      <div 
        className="w-full max-w-md bg-[#161620] rounded-3xl border border-white/10 p-6 shadow-2xl safe-bottom max-h-[90dvh] overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between pb-4 border-b border-white/10">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-[#FE2C55] to-[#A825FF] flex items-center justify-center font-black text-white text-xs">
              V
            </div>
            <span className="font-extrabold text-base text-white">
              {isSignUp ? 'Create VIVA Account' : 'Sign In to VIVA'}
            </span>
          </div>
          <button onClick={onClose} className="p-1 text-neutral-400 hover:text-white">
            <X className="w-5 h-5" />
          </button>
        </div>

        {errorMessage && (
          <div className="mt-4 p-3 bg-red-500/15 border border-red-500/30 rounded-2xl flex items-center gap-2 text-xs text-red-300">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{errorMessage}</span>
          </div>
        )}

        {/* Email Form */}
        <form onSubmit={handleSubmit} className="mt-5 space-y-3.5">
          {isSignUp && (
            <>
              <div>
                <label className="text-xs font-semibold text-neutral-400 mb-1 block">Display Name</label>
                <input
                  type="text"
                  value={displayName}
                  onChange={(e) => setDisplayName(e.target.value)}
                  placeholder="e.g. Alex Rivera"
                  className="w-full bg-[#20202E] text-white text-xs px-4 py-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-neutral-400 mb-1 block">Username</label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. alex_viva"
                  className="w-full bg-[#20202E] text-white text-xs px-4 py-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
                />
              </div>
            </>
          )}

          <div>
            <label className="text-xs font-semibold text-neutral-400 mb-1 block">Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="user@example.com"
              className="w-full bg-[#20202E] text-white text-xs px-4 py-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-neutral-400 mb-1 block">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••••••"
              className="w-full bg-[#20202E] text-white text-xs px-4 py-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl viva-gradient-btn text-white text-xs font-bold transition-transform active:scale-95 shadow-lg mt-2 flex items-center justify-center gap-1.5"
          >
            {isSignUp ? <UserPlus className="w-4 h-4" /> : <LogIn className="w-4 h-4" />}
            <span>{isSignUp ? 'Create Account' : 'Sign In'}</span>
          </button>
        </form>

        <div className="mt-4 text-center">
          <button
            type="button"
            onClick={() => {
              setIsSignUp(!isSignUp);
              setErrorMessage('');
            }}
            className="text-xs text-neutral-400 hover:text-white"
          >
            {isSignUp ? 'Already have an account? Sign In' : "Don't have an account? Create one"}
          </button>
        </div>

        {/* Quick Demo Switcher Section */}
        <div className="mt-6 pt-5 border-t border-white/10">
          <div className="flex items-center gap-1.5 text-xs font-bold text-neutral-300 mb-3">
            <Sparkles className="w-4 h-4 text-[#00F2FE]" />
            <span>Switch Seed Accounts (Admin / Owner / Creator)</span>
          </div>

          <div className="space-y-2">
            {sampleUsers.map((user) => {
              const isSelected = currentUser?.id === user.id;
              return (
                <button
                  key={user.id}
                  onClick={() => {
                    switchAccount(user.id);
                    onClose();
                  }}
                  className={`w-full flex items-center justify-between p-2.5 rounded-xl border transition-all text-left ${
                    isSelected
                      ? 'bg-gradient-to-r from-[#FE2C55]/20 to-[#A825FF]/20 border-[#FE2C55]'
                      : 'bg-[#1C1C26] border-white/5 hover:border-white/20'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <img src={user.avatarUrl} alt={user.username} className="w-8 h-8 rounded-full object-cover" />
                    <div>
                      <div className="flex items-center gap-1">
                        <span className="text-xs font-bold text-white">{user.displayName}</span>
                        {user.isVerified && <VerifiedBadge size={12} />}
                      </div>
                      <span className="text-[10px] text-neutral-400">@{user.username}</span>
                    </div>
                  </div>
                  <span className={`text-[10px] px-2 py-0.5 rounded font-black ${
                    user.role === 'OWNER'
                      ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                      : user.role === 'ADMIN'
                      ? 'bg-purple-500/20 text-purple-300 border border-purple-500/30'
                      : 'bg-white/10 text-neutral-300'
                  }`}>
                    {user.role}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};
