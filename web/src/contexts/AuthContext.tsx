import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { auth, db, doc, getDoc, setDoc, onAuthStateChanged, signOut } from '../firebase';

interface AuthContextType {
  currentUser: User | null;
  loading: boolean;
  isAdmin: boolean;
  isOwner: boolean;
  switchAccount: (userId: string) => void;
  updateUserProfile: (data: Partial<User>) => Promise<{ success: boolean; error?: string }>;
  logout: () => Promise<void>;
  sampleUsers: User[];
}

// Preset production-seed users matching Android Room Database
const SEED_USERS: User[] = [
  {
    id: 'user_me',
    username: 'alex_viva',
    displayName: 'Alex Rivera',
    avatarUrl: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
    bio: 'Digital Creator & Visual Explorer ✨ Creating inspiring short stories every day.',
    website: 'https://alexrivera.art',
    followersCount: 14200,
    followingCount: 238,
    likesCount: 94800,
    isVerified: true,
    verificationCategory: 'Creator',
    role: 'USER',
    accountStatus: 'ACTIVE',
    email: 'alex@viva.social',
    createdAt: Date.now() - 86400000 * 60
  },
  {
    id: 'user_official_viva',
    username: 'VIVA',
    displayName: 'VIVA Official',
    avatarUrl: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80',
    bio: 'Official VIVA Support & Trust Safety Desk. Verified Administrative Communications.',
    website: 'https://viva.social/support',
    followersCount: 1250000,
    followingCount: 1,
    likesCount: 8900000,
    isVerified: true,
    verificationCategory: 'Official Platform',
    role: 'OWNER',
    accountStatus: 'ACTIVE',
    email: 'support@viva.social',
    createdAt: Date.now() - 86400000 * 365
  },
  {
    id: 'user_owner',
    username: 'viva_owner',
    displayName: 'Platform Owner',
    avatarUrl: 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&auto=format&fit=crop&q=80',
    bio: 'Founder & Platform Administrator of VIVA Short Video Network.',
    website: 'https://viva.social',
    followersCount: 45000,
    followingCount: 12,
    likesCount: 320000,
    isVerified: true,
    verificationCategory: 'Executive',
    role: 'OWNER',
    accountStatus: 'ACTIVE',
    email: 'owner@viva.social',
    createdAt: Date.now() - 86400000 * 300
  },
  {
    id: 'user_admin',
    username: 'admin_viva',
    displayName: 'VIVA Staff Moderator',
    avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80',
    bio: 'VIVA Moderation & Compliance Operations team member.',
    website: 'https://viva.social/safety',
    followersCount: 8900,
    followingCount: 45,
    likesCount: 54000,
    isVerified: true,
    verificationCategory: 'Staff',
    role: 'ADMIN',
    accountStatus: 'ACTIVE',
    email: 'admin@viva.social',
    createdAt: Date.now() - 86400000 * 180
  },
  {
    id: 'user_sarah',
    username: 'sarah_dance',
    displayName: 'Sarah Jenkins',
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80',
    bio: 'Choreographer & Movement Director 💃 Teaching rhythm & modern dance.',
    website: 'https://sarahdance.com',
    followersCount: 84000,
    followingCount: 320,
    likesCount: 412000,
    isVerified: true,
    verificationCategory: 'Choreographer',
    role: 'USER',
    accountStatus: 'ACTIVE',
    email: 'sarah@dance.org',
    createdAt: Date.now() - 86400000 * 120
  }
];

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('viva_current_user_id');
    const matched = SEED_USERS.find(u => u.id === saved);
    return matched || SEED_USERS[0];
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Listen to Firebase Auth if connected
    const unsubscribe = onAuthStateChanged(auth, async (fbUser) => {
      if (fbUser) {
        try {
          const userDoc = await getDoc(doc(db, 'users', fbUser.uid));
          if (userDoc.exists()) {
            setCurrentUser(userDoc.data() as User);
          } else {
            // Initialize new user with non-privileged role
            const newUser: User = {
              id: fbUser.uid,
              username: fbUser.displayName?.toLowerCase().replace(/\s+/g, '_') || `user_${fbUser.uid.slice(0, 6)}`,
              displayName: fbUser.displayName || 'VIVA Creator',
              avatarUrl: fbUser.photoURL || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80',
              bio: 'New VIVA creator. Exploring visual stories.',
              website: '',
              followersCount: 0,
              followingCount: 0,
              likesCount: 0,
              isVerified: false,
              role: 'USER',
              accountStatus: 'ACTIVE',
              email: fbUser.email || '',
              createdAt: Date.now()
            };
            await setDoc(doc(db, 'users', fbUser.uid), newUser);
            setCurrentUser(newUser);
          }
        } catch (e) {
          console.warn('Firestore offline or permission pending, using local session');
        }
      }
    });

    return () => unsubscribe();
  }, []);

  const switchAccount = (userId: string) => {
    const target = SEED_USERS.find(u => u.id === userId);
    if (target) {
      setCurrentUser(target);
      localStorage.setItem('viva_current_user_id', target.id);
    }
  };

  const updateUserProfile = async (data: Partial<User>): Promise<{ success: boolean; error?: string }> => {
    if (!currentUser) return { success: false, error: 'Not authenticated' };

    // Strict reserved username verification
    if (data.username) {
      const clean = data.username.trim().replace(/^@/, '').toLowerCase();
      if (clean === 'viva' || clean === 'admin' || clean === 'owner' || clean === 'support') {
        if (currentUser.username.toLowerCase() !== clean) {
          return { success: false, error: `Username @${clean} is strictly reserved for official VIVA platform administration.` };
        }
      }
    }

    // Role protection: Ordinary users cannot self-escalate role or verification status
    const sanitizedData = { ...data };
    delete (sanitizedData as any).role;
    delete (sanitizedData as any).isVerified;
    delete (sanitizedData as any).accountStatus;

    const updated = { ...currentUser, ...sanitizedData };
    setCurrentUser(updated);

    try {
      await setDoc(doc(db, 'users', currentUser.id), updated, { merge: true });
    } catch (e) {
      // Local session updated
    }

    return { success: true };
  };

  const logout = async () => {
    try {
      await signOut(auth);
    } catch (e) {}
    setCurrentUser(SEED_USERS[0]);
    localStorage.removeItem('viva_current_user_id');
  };

  const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.role === 'OWNER';
  const isOwner = currentUser?.role === 'OWNER';

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        loading,
        isAdmin,
        isOwner,
        switchAccount,
        updateUserProfile,
        logout,
        sampleUsers: SEED_USERS
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
