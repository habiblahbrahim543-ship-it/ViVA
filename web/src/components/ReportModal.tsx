import React, { useState } from 'react';
import { X, ShieldAlert, Check } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

interface ReportModalProps {
  targetType: 'VIDEO' | 'USER' | 'LIVE';
  targetId: string;
  isOpen: boolean;
  onClose: () => void;
}

const REPORT_REASONS = [
  'Hate Speech or Harassment',
  'Misinformation or Spam',
  'Dangerous Content / Safety Hazard',
  'Copyright or Intellectual Property Infringement',
  'Nudity or Sexual Activity',
  'Violence or Graphic Content'
];

export const ReportModal: React.FC<ReportModalProps> = ({
  targetType,
  targetId,
  isOpen,
  onClose
}) => {
  const { currentUser } = useAuth();
  const [selectedReason, setSelectedReason] = useState(REPORT_REASONS[0]);
  const [notes, setNotes] = useState('');
  const [submitted, setSubmitted] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    setTimeout(() => {
      setSubmitted(false);
      onClose();
    }, 1500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-md p-4 animate-fade-in">
      <div 
        className="w-full max-w-sm bg-[#16161F] rounded-3xl border border-white/10 p-5 shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between pb-3 border-b border-white/10">
          <div className="flex items-center gap-2">
            <ShieldAlert className="w-5 h-5 text-[#FE2C55]" />
            <span className="text-sm font-bold text-white">Report {targetType}</span>
          </div>
          <button onClick={onClose} className="p-1 text-neutral-400 hover:text-white">
            <X className="w-5 h-5" />
          </button>
        </div>

        {submitted ? (
          <div className="py-8 text-center flex flex-col items-center">
            <div className="w-14 h-14 rounded-full bg-green-500/20 text-green-400 flex items-center justify-center mb-3">
              <Check className="w-8 h-8" />
            </div>
            <h4 className="font-bold text-white text-base">Report Submitted</h4>
            <p className="text-xs text-neutral-400 mt-1 max-w-[220px]">
              Thank you for helping keep VIVA safe. Our Trust & Safety team will review this shortly.
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="mt-4 space-y-3.5">
            <div>
              <label className="text-xs font-semibold text-neutral-400 mb-1.5 block">
                Select Violation Reason
              </label>
              <select
                value={selectedReason}
                onChange={(e) => setSelectedReason(e.target.value)}
                className="w-full bg-[#20202B] text-white text-xs p-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
              >
                {REPORT_REASONS.map((reason) => (
                  <option key={reason} value={reason} className="bg-[#181822]">
                    {reason}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="text-xs font-semibold text-neutral-400 mb-1.5 block">
                Additional Details (Optional)
              </label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Describe what happened..."
                rows={3}
                className="w-full bg-[#20202B] text-white text-xs p-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55] placeholder-neutral-500 resize-none"
              />
            </div>

            <div className="flex gap-2 pt-2">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-white text-xs font-bold transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="flex-1 py-2.5 rounded-xl bg-[#FE2C55] text-white text-xs font-bold transition-transform active:scale-95 shadow-lg shadow-[#FE2C55]/30"
              >
                Submit Report
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
