import React, { useState, useEffect } from 'react';
import { Share, Download, X } from 'lucide-react';

export const InstallPwaPrompt: React.FC = () => {
  const [showPrompt, setShowPrompt] = useState(false);
  const [isIOS, setIsIOS] = useState(false);
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);

  useEffect(() => {
    // Check if already running in standalone PWA mode
    const isStandalone = window.matchMedia('(display-mode: standalone)').matches || (window.navigator as any).standalone;
    if (isStandalone) return;

    // Check if dismissed recently
    const dismissed = localStorage.getItem('viva_pwa_dismissed');
    if (dismissed && Date.now() - parseInt(dismissed, 10) < 86400000) {
      return;
    }

    const ua = window.navigator.userAgent.toLowerCase();
    const isAppleDevice = /iphone|ipad|ipod/.test(ua);
    setIsIOS(isAppleDevice);

    if (isAppleDevice) {
      setShowPrompt(true);
    }

    const handleBeforeInstall = (e: any) => {
      e.preventDefault();
      setDeferredPrompt(e);
      setShowPrompt(true);
    };

    window.addEventListener('beforeinstallprompt', handleBeforeInstall);
    return () => window.removeEventListener('beforeinstallprompt', handleBeforeInstall);
  }, []);

  const handleInstallClick = async () => {
    if (deferredPrompt) {
      deferredPrompt.prompt();
      const choice = await deferredPrompt.userChoice;
      if (choice.outcome === 'accepted') {
        setShowPrompt(false);
      }
      setDeferredPrompt(null);
    }
  };

  const handleDismiss = () => {
    setShowPrompt(false);
    localStorage.setItem('viva_pwa_dismissed', Date.now().toString());
  };

  if (!showPrompt) return null;

  return (
    <div className="fixed top-3 left-3 right-3 z-50 animate-fade-in">
      <div className="viva-glass rounded-2xl p-3.5 shadow-2xl border border-white/20 flex items-center justify-between gap-3 bg-gradient-to-r from-neutral-900/95 via-neutral-900/90 to-purple-950/80">
        <img src="/icon.svg" alt="VIVA" className="w-11 h-11 rounded-xl shadow-md border border-white/10 flex-shrink-0" />
        
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-1.5">
            <span className="font-extrabold text-sm text-white tracking-wide">Install VIVA</span>
            <span className="text-[10px] bg-[#FE2C55]/20 text-[#FE2C55] font-bold px-1.5 py-0.5 rounded">PWA</span>
          </div>
          {isIOS ? (
            <p className="text-xs text-neutral-300 mt-0.5 leading-snug">
              Tap <Share className="inline w-3.5 h-3.5 text-[#00F2FE] mx-0.5" /> then <strong className="text-white">"Add to Home Screen"</strong> on iPhone
            </p>
          ) : (
            <p className="text-xs text-neutral-300 mt-0.5 leading-snug">
              Install the app for instant fullscreen video & notifications
            </p>
          )}
        </div>

        <div className="flex items-center gap-1.5 flex-shrink-0">
          {!isIOS && deferredPrompt && (
            <button
              onClick={handleInstallClick}
              className="viva-gradient-btn px-3 py-1.5 rounded-xl font-bold text-xs text-white shadow-lg flex items-center gap-1"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Install</span>
            </button>
          )}
          <button 
            onClick={handleDismiss} 
            className="p-1.5 text-neutral-400 hover:text-white rounded-lg transition-colors"
            title="Dismiss"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
