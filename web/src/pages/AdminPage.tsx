import React, { useState } from 'react';
import { 
  Shield, 
  Users, 
  Video as VideoIcon, 
  CheckCircle2, 
  AlertTriangle, 
  MessageSquare, 
  Clock, 
  Search, 
  Check, 
  X, 
  Ban, 
  Trash2, 
  ArrowLeft,
  Eye,
  ExternalLink,
  Send,
  Lock,
  RefreshCw,
  Award
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { VerifiedBadge } from '../components/VerifiedBadge';
import { User, VerificationRequest, ModerationReport, SupportConversation, AdminAuditLog } from '../types';

interface AdminPageProps {
  onBackToApp: () => void;
}

export const AdminPage: React.FC<AdminPageProps> = ({ onBackToApp }) => {
  const { currentUser, isAdmin, isOwner, sampleUsers } = useAuth();

  const [activeTab, setActiveTab] = useState<'overview' | 'verifications' | 'users' | 'reports' | 'support' | 'audit'>('overview');

  // Sample data syncing with Android app structure
  const [usersList, setUsersList] = useState<User[]>(sampleUsers);
  const [searchUserQuery, setSearchUserQuery] = useState('');

  const [verificationRequests, setVerificationRequests] = useState<VerificationRequest[]>([
    {
      id: 'vr_1',
      userId: 'user_sarah',
      username: 'sarah_dance',
      fullName: 'Sarah Jenkins',
      category: 'Choreographer / Artist',
      country: 'United Kingdom',
      reason: 'Professional international dance choreographer with 84,000+ followers across platforms.',
      website: 'https://sarahdance.com',
      instagram: '@sarahjenkinsdance',
      youtube: 'SarahDanceOfficial',
      tiktok: '@sarah_dance',
      otherLinks: 'Featured on BBC Dance 2025',
      supportingDocuments: 'dance_credentials_doc.pdf',
      status: 'pending',
      adminNotes: '',
      createdAt: Date.now() - 3600000 * 18,
      updatedAt: Date.now() - 3600000 * 18
    },
    {
      id: 'vr_2',
      userId: 'user_chef',
      username: 'chef_marco',
      fullName: 'Marco Valenti',
      category: 'Culinary Creator',
      country: 'Italy',
      reason: 'Michelin star trained culinary creator sharing authentic regional Italian cuisine.',
      website: 'https://marcocuisine.it',
      instagram: '@chef_marco_valenti',
      youtube: 'MarcoKitchen',
      tiktok: '@chef_marco',
      otherLinks: 'Culinary Ambassador 2026',
      supportingDocuments: 'culinary_diploma.pdf',
      status: 'pending',
      adminNotes: '',
      createdAt: Date.now() - 3600000 * 36,
      updatedAt: Date.now() - 3600000 * 36
    }
  ]);

  const [reports, setReports] = useState<ModerationReport[]>([
    {
      id: 'rep_1',
      targetType: 'VIDEO',
      targetId: 'v_cyber_2',
      reporterId: 'user_sarah',
      reporterName: 'sarah_dance',
      reason: 'Copyright or Intellectual Property Infringement',
      notes: 'Contains background music sample that requires creator attribution.',
      status: 'PENDING',
      timestamp: Date.now() - 3600000 * 3
    }
  ]);

  const [supportConversations, setSupportConversations] = useState<SupportConversation[]>([
    {
      id: 'sc_1',
      userId: 'user_me',
      username: 'alex_viva',
      userDisplayName: 'Alex Rivera',
      userAvatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
      lastMessage: 'Hi VIVA team, I have a question regarding Creator Tools.',
      lastTimestamp: Date.now() - 3600000 * 1,
      unreadByAdmin: true,
      unreadByUser: false,
      status: 'OPEN'
    }
  ]);

  const [auditLogs, setAuditLogs] = useState<AdminAuditLog[]>([
    {
      id: 'log_1',
      adminId: 'user_owner',
      adminUsername: 'viva_owner',
      action: 'APPROVE_VERIFICATION',
      targetId: 'alex_viva',
      details: 'Approved Official Creator Verification Badge for @alex_viva',
      timestamp: Date.now() - 3600000 * 48
    },
    {
      id: 'log_2',
      adminId: 'user_admin',
      adminUsername: 'admin_viva',
      action: 'SYSTEM_AUDIT',
      targetId: 'viva_platform',
      details: 'Routine integrity audit across Android and Web client endpoints',
      timestamp: Date.now() - 3600000 * 24
    }
  ]);

  // Access Control Gatekeeper
  if (!isAdmin && !isOwner) {
    return (
      <div className="h-full w-full bg-[#0B0B0E] flex flex-col items-center justify-center p-6 text-center text-white">
        <div className="w-16 h-16 rounded-2xl bg-red-500/20 text-red-400 flex items-center justify-center mb-4 border border-red-500/30">
          <Lock className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-black">Access Denied</h2>
        <p className="text-xs text-neutral-400 mt-2 max-w-sm">
          The VIVA Administrative Control Panel is restricted to authorized VIVA staff moderators and owners.
        </p>
        <button
          onClick={onBackToApp}
          className="mt-6 px-6 py-2.5 rounded-xl bg-white/10 hover:bg-white/15 text-white text-xs font-bold transition-colors"
        >
          Return to Feed
        </button>
      </div>
    );
  }

  // Verification handling
  const handleApproveVerification = (reqId: string, userId: string, category: string) => {
    setVerificationRequests(prev => prev.map(r => r.id === reqId ? { ...r, status: 'approved' } : r));
    setUsersList(prev => prev.map(u => u.id === userId ? { ...u, isVerified: true, verificationCategory: category } : u));
    
    // Add audit log
    const log: AdminAuditLog = {
      id: `log_${Date.now()}`,
      adminId: currentUser?.id || 'admin',
      adminUsername: currentUser?.username || 'admin',
      action: 'APPROVE_VERIFICATION',
      targetId: userId,
      details: `Approved verification request ${reqId} for user ${userId}`,
      timestamp: Date.now()
    };
    setAuditLogs(prev => [log, ...prev]);
    alert('Verification approved! The creator now displays the verified badge across Web and Android.');
  };

  const handleRejectVerification = (reqId: string, userId: string) => {
    const reason = prompt('Reason for rejection:') || 'Does not meet current public authenticity criteria.';
    setVerificationRequests(prev => prev.map(r => r.id === reqId ? { ...r, status: 'rejected', adminNotes: reason } : r));
    alert('Verification request marked as rejected.');
  };

  // User management handling
  const handleToggleUserBan = (userId: string) => {
    setUsersList(prev => prev.map(u => {
      if (u.id === userId) {
        const nextStatus = u.accountStatus === 'BANNED' ? 'ACTIVE' : 'BANNED';
        return { ...u, accountStatus: nextStatus };
      }
      return u;
    }));
  };

  const handleToggleUserVerification = (userId: string) => {
    setUsersList(prev => prev.map(u => {
      if (u.id === userId) {
        return { ...u, isVerified: !u.isVerified };
      }
      return u;
    }));
  };

  return (
    <div className="h-full w-full bg-[#0B0B0E] overflow-y-auto safe-top pb-24 text-white flex flex-col">
      {/* Admin Top Header */}
      <div className="px-4 py-3 bg-[#13131C] border-b border-white/10 flex items-center justify-between sticky top-0 z-20">
        <div className="flex items-center gap-2.5">
          <button
            onClick={onBackToApp}
            className="p-1 text-neutral-400 hover:text-white rounded-lg hover:bg-white/5 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-purple-600 to-pink-500 flex items-center justify-center font-black text-xs text-white">
              V
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-black tracking-wider uppercase">VIVA Control Panel</span>
                <span className="text-[9px] bg-purple-500/20 text-purple-300 font-extrabold px-1.5 py-0.5 rounded border border-purple-500/30">
                  {currentUser?.role}
                </span>
              </div>
              <p className="text-[10px] text-neutral-400">Production Unified Cloud Manager</p>
            </div>
          </div>
        </div>
      </div>

      {/* Admin Navigation Tabs */}
      <div className="px-3 py-2 bg-[#101016] border-b border-white/5 flex gap-1.5 overflow-x-auto no-scrollbar">
        {[
          { id: 'overview', label: 'Overview', icon: Shield },
          { id: 'verifications', label: `Verifications (${verificationRequests.filter(r => r.status === 'pending').length})`, icon: Award },
          { id: 'users', label: 'Users', icon: Users },
          { id: 'reports', label: `Reports (${reports.length})`, icon: AlertTriangle },
          { id: 'support', label: 'Support Desk', icon: MessageSquare },
          { id: 'audit', label: 'Audit Trail', icon: Clock }
        ].map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`px-3 py-2 rounded-xl text-xs font-bold flex items-center gap-1.5 whitespace-nowrap transition-all flex-shrink-0 ${
                isActive
                  ? 'bg-purple-600 text-white shadow-md shadow-purple-600/30'
                  : 'bg-white/5 text-neutral-400 hover:text-white'
              }`}
            >
              <Icon className="w-3.5 h-3.5" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Tab Contents */}
      <div className="p-4 flex-1">
        {/* OVERVIEW TAB */}
        {activeTab === 'overview' && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              <div className="bg-[#14141E] p-4 rounded-2xl border border-white/5">
                <span className="text-[11px] text-neutral-400 font-medium">Total Registered Users</span>
                <h3 className="text-xl font-black text-white mt-1">1,348,290</h3>
                <span className="text-[10px] text-green-400 font-semibold mt-1 inline-block">+14.2% this week</span>
              </div>
              <div className="bg-[#14141E] p-4 rounded-2xl border border-white/5">
                <span className="text-[11px] text-neutral-400 font-medium">Pending Verifications</span>
                <h3 className="text-xl font-black text-[#20D5EC] mt-1">{verificationRequests.filter(r => r.status === 'pending').length}</h3>
                <span className="text-[10px] text-neutral-400 font-semibold mt-1 inline-block">Requires Staff Review</span>
              </div>
              <div className="bg-[#14141E] p-4 rounded-2xl border border-white/5">
                <span className="text-[11px] text-neutral-400 font-medium">Active Moderation Reports</span>
                <h3 className="text-xl font-black text-[#FE2C55] mt-1">{reports.length}</h3>
                <span className="text-[10px] text-neutral-400 font-semibold mt-1 inline-block">Pending Review</span>
              </div>
              <div className="bg-[#14141E] p-4 rounded-2xl border border-white/5">
                <span className="text-[11px] text-neutral-400 font-medium">Open Support Tickets</span>
                <h3 className="text-xl font-black text-amber-400 mt-1">{supportConversations.length}</h3>
                <span className="text-[10px] text-neutral-400 font-semibold mt-1 inline-block">Official Desk Queue</span>
              </div>
            </div>

            {/* Quick Actions Card */}
            <div className="bg-[#14141E] p-4 rounded-2xl border border-white/5">
              <h4 className="text-xs font-bold text-white mb-3">Quick Administrative Controls</h4>
              <div className="flex flex-wrap gap-2">
                <button
                  onClick={() => setActiveTab('verifications')}
                  className="px-3.5 py-2 rounded-xl bg-[#20D5EC]/15 hover:bg-[#20D5EC]/25 text-[#20D5EC] text-xs font-bold flex items-center gap-1.5 transition-colors border border-[#20D5EC]/30"
                >
                  <Award className="w-3.5 h-3.5" />
                  <span>Review Pending Verifications</span>
                </button>
                <button
                  onClick={() => setActiveTab('users')}
                  className="px-3.5 py-2 rounded-xl bg-white/5 hover:bg-white/10 text-white text-xs font-bold flex items-center gap-1.5 transition-colors border border-white/10"
                >
                  <Users className="w-3.5 h-3.5" />
                  <span>Search All Platform Accounts</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* VERIFICATIONS TAB */}
        {activeTab === 'verifications' && (
          <div className="space-y-3">
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-xs font-extrabold uppercase text-neutral-400 tracking-wider">
                Pending Verification Inquiries
              </h3>
            </div>

            {verificationRequests.length === 0 ? (
              <p className="text-xs text-neutral-400 py-6 text-center">No pending verification requests.</p>
            ) : (
              verificationRequests.map((req) => (
                <div key={req.id} className="p-4 rounded-2xl bg-[#14141E] border border-white/10 space-y-3">
                  <div className="flex items-start justify-between">
                    <div>
                      <div className="flex items-center gap-1.5">
                        <span className="font-extrabold text-sm text-white">{req.fullName}</span>
                        <span className="text-xs text-neutral-400">(@{req.username})</span>
                      </div>
                      <span className="inline-block mt-1 text-[10px] px-2 py-0.5 rounded-full bg-[#20D5EC]/15 text-[#20D5EC] font-bold border border-[#20D5EC]/30">
                        {req.category} • {req.country}
                      </span>
                    </div>
                    <span className={`text-[10px] font-black px-2 py-0.5 rounded uppercase ${
                      req.status === 'approved'
                        ? 'bg-green-500/20 text-green-300'
                        : req.status === 'rejected'
                        ? 'bg-red-500/20 text-red-300'
                        : 'bg-amber-500/20 text-amber-300'
                    }`}>
                      {req.status}
                    </span>
                  </div>

                  <p className="text-xs text-neutral-300 leading-relaxed bg-[#1A1A26] p-3 rounded-xl">
                    <strong className="text-white block mb-0.5">Application Reason:</strong>
                    {req.reason}
                  </p>

                  {req.otherLinks && (
                    <div className="text-[11px] text-neutral-400">
                      <strong>Submitted Links:</strong> {req.otherLinks}
                    </div>
                  )}

                  {req.status === 'pending' && (
                    <div className="flex gap-2 pt-2 border-t border-white/5">
                      <button
                        onClick={() => handleApproveVerification(req.id, req.userId, req.category)}
                        className="flex-1 py-2 px-3 rounded-xl bg-gradient-to-r from-teal-500 to-[#20D5EC] text-black font-bold text-xs flex items-center justify-center gap-1.5 shadow-md"
                      >
                        <Check className="w-3.5 h-3.5 stroke-[3]" />
                        <span>Approve Badge</span>
                      </button>
                      <button
                        onClick={() => handleRejectVerification(req.id, req.userId)}
                        className="flex-1 py-2 px-3 rounded-xl bg-red-500/20 hover:bg-red-500/30 text-red-300 font-bold text-xs flex items-center justify-center gap-1.5 border border-red-500/30"
                      >
                        <X className="w-3.5 h-3.5" />
                        <span>Reject</span>
                      </button>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        )}

        {/* USERS TAB */}
        {activeTab === 'users' && (
          <div className="space-y-3">
            <div className="relative flex items-center">
              <Search className="w-4 h-4 text-neutral-400 absolute left-3.5" />
              <input
                type="text"
                value={searchUserQuery}
                onChange={(e) => setSearchUserQuery(e.target.value)}
                placeholder="Search user by username or email..."
                className="w-full bg-[#161622] text-white text-xs pl-10 pr-4 py-2.5 rounded-2xl border border-white/10 focus:outline-none focus:border-purple-500 placeholder-neutral-500"
              />
            </div>

            <div className="space-y-2">
              {usersList
                .filter(u => u.username.toLowerCase().includes(searchUserQuery.toLowerCase()) || u.email.toLowerCase().includes(searchUserQuery.toLowerCase()))
                .map((u) => (
                  <div key={u.id} className="p-3.5 rounded-2xl bg-[#14141E] border border-white/5 flex items-center justify-between flex-wrap gap-2">
                    <div className="flex items-center gap-3">
                      <img src={u.avatarUrl} alt={u.username} className="w-10 h-10 rounded-full object-cover" />
                      <div>
                        <div className="flex items-center gap-1.5">
                          <span className="font-bold text-xs text-white">{u.displayName}</span>
                          {u.isVerified && <VerifiedBadge size={13} />}
                          <span className="text-[9px] px-1.5 py-0.2 rounded font-black bg-white/10 text-neutral-300">
                            {u.role}
                          </span>
                        </div>
                        <span className="text-[11px] text-neutral-400">@{u.username} • {u.email}</span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleToggleUserVerification(u.id)}
                        className={`text-[10px] px-2.5 py-1.5 rounded-xl font-bold border transition-colors ${
                          u.isVerified
                            ? 'bg-[#20D5EC]/20 text-[#20D5EC] border-[#20D5EC]/40'
                            : 'bg-white/5 text-neutral-400 border-white/10'
                        }`}
                      >
                        {u.isVerified ? 'Verified ✓' : 'Set Verified'}
                      </button>

                      <button
                        onClick={() => handleToggleUserBan(u.id)}
                        className={`text-[10px] px-2.5 py-1.5 rounded-xl font-bold border transition-colors ${
                          u.accountStatus === 'BANNED'
                            ? 'bg-green-500/20 text-green-300 border-green-500/40'
                            : 'bg-red-500/20 text-red-300 border-red-500/40'
                        }`}
                      >
                        {u.accountStatus === 'BANNED' ? 'Unban' : 'Ban User'}
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* REPORTS TAB */}
        {activeTab === 'reports' && (
          <div className="space-y-3">
            {reports.map((rep) => (
              <div key={rep.id} className="p-4 rounded-2xl bg-[#14141E] border border-white/10 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-[#FE2C55] flex items-center gap-1">
                    <AlertTriangle className="w-3.5 h-3.5" /> {rep.reason}
                  </span>
                  <span className="text-[10px] bg-amber-500/20 text-amber-300 px-2 py-0.5 rounded font-black">
                    {rep.status}
                  </span>
                </div>
                <p className="text-xs text-neutral-300">
                  Reported target ID: <span className="font-mono text-white">{rep.targetId}</span> by @{rep.reporterName}
                </p>
                {rep.notes && (
                  <p className="text-xs text-neutral-400 bg-[#191924] p-2.5 rounded-xl">
                    {rep.notes}
                  </p>
                )}
                <div className="flex gap-2 pt-2 border-t border-white/5">
                  <button
                    onClick={() => {
                      setReports(prev => prev.filter(r => r.id !== rep.id));
                      alert('Target content removed.');
                    }}
                    className="flex-1 py-1.5 rounded-xl bg-red-500/20 text-red-300 font-bold text-xs"
                  >
                    Remove Content
                  </button>
                  <button
                    onClick={() => {
                      setReports(prev => prev.filter(r => r.id !== rep.id));
                      alert('Report dismissed.');
                    }}
                    className="flex-1 py-1.5 rounded-xl bg-white/5 text-neutral-300 font-bold text-xs"
                  >
                    Dismiss
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* AUDIT TRAIL TAB */}
        {activeTab === 'audit' && (
          <div className="space-y-2">
            {auditLogs.map((log) => (
              <div key={log.id} className="p-3.5 rounded-2xl bg-[#14141E] border border-white/5 text-xs">
                <div className="flex items-center justify-between text-neutral-400 text-[10px]">
                  <span>Admin: <strong className="text-white">@{log.adminUsername}</strong></span>
                  <span>{new Date(log.timestamp).toLocaleString()}</span>
                </div>
                <p className="text-white font-semibold mt-1">{log.details}</p>
                <span className="text-[10px] font-mono text-purple-400 mt-1 inline-block">Action: {log.action}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
