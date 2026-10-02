import React, { useState, useRef } from 'react';
import { Upload, Video, Music, Sparkles, Check, ArrowLeft } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

interface CreatePageProps {
  onPublishSuccess: () => void;
  onCancel: () => void;
}

export const CreatePage: React.FC<CreatePageProps> = ({ onPublishSuccess, onCancel }) => {
  const { currentUser } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [videoFile, setVideoFile] = useState<File | null>(null);
  const [videoPreviewUrl, setVideoPreviewUrl] = useState<string>('');
  const [caption, setCaption] = useState('');
  const [hashtags, setHashtags] = useState('viva, creator');
  const [soundTitle, setSoundTitle] = useState('VIVA Original Beat');
  const [category, setCategory] = useState('Creative');
  const [allowComments, setAllowComments] = useState(true);
  const [isPrivate, setIsPrivate] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setVideoFile(file);
      const url = URL.createObjectURL(file);
      setVideoPreviewUrl(url);
    }
  };

  const handlePublish = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!caption.trim()) {
      alert('Please enter a caption for your video.');
      return;
    }

    setUploading(true);
    setProgress(20);
    await new Promise(r => setTimeout(r, 300));
    setProgress(65);
    await new Promise(r => setTimeout(r, 400));
    setProgress(100);

    setTimeout(() => {
      setUploading(false);
      alert('Video published to VIVA successfully!');
      onPublishSuccess();
    }, 400);
  };

  return (
    <div className="h-full w-full bg-[#0A0A0D] overflow-y-auto safe-top pb-24 text-white">
      {/* Top Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-white/10 sticky top-0 bg-[#0A0A0D]/90 backdrop-blur-md z-10">
        <button onClick={onCancel} className="p-1 text-neutral-400 hover:text-white">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <span className="text-sm font-bold">New VIVA Post</span>
        <div className="w-5" />
      </div>

      <form onSubmit={handlePublish} className="p-4 space-y-4 max-w-md mx-auto">
        {/* Video Selector / Camera Roll Uploader */}
        <input
          ref={fileInputRef}
          type="file"
          accept="video/*"
          capture="environment"
          onChange={handleFileChange}
          className="hidden"
        />

        {videoPreviewUrl ? (
          <div className="relative rounded-2xl overflow-hidden aspect-[9/16] max-h-[340px] mx-auto bg-black border border-white/20 shadow-xl">
            <video
              src={videoPreviewUrl}
              controls
              playsInline
              className="w-full h-full object-cover"
            />
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              className="absolute top-3 right-3 bg-black/60 backdrop-blur-md text-xs font-bold px-3 py-1.5 rounded-full border border-white/20"
            >
              Change Video
            </button>
          </div>
        ) : (
          <div
            onClick={() => fileInputRef.current?.click()}
            className="border-2 border-dashed border-white/20 hover:border-[#FE2C55] rounded-3xl p-8 flex flex-col items-center justify-center cursor-pointer bg-white/[0.02] hover:bg-white/[0.04] transition-all"
          >
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-[#FE2C55] to-[#00F2FE] flex items-center justify-center text-white mb-3 shadow-lg">
              <Upload className="w-7 h-7" />
            </div>
            <h3 className="font-bold text-sm text-white">Select Video or Record</h3>
            <p className="text-xs text-neutral-400 mt-1 text-center max-w-[200px]">
              MP4, MOV, or WebM from your iPhone Camera Roll or camera
            </p>
          </div>
        )}

        {/* Caption */}
        <div>
          <label className="text-xs font-bold text-neutral-400 mb-1.5 block">Caption</label>
          <textarea
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
            placeholder="Share a story, challenge, or description..."
            rows={3}
            className="w-full bg-[#16161E] text-white text-xs p-3.5 rounded-2xl border border-white/10 focus:outline-none focus:border-[#FE2C55] placeholder-neutral-500 resize-none"
          />
        </div>

        {/* Hashtags */}
        <div>
          <label className="text-xs font-bold text-neutral-400 mb-1.5 block">Hashtags (comma separated)</label>
          <input
            type="text"
            value={hashtags}
            onChange={(e) => setHashtags(e.target.value)}
            placeholder="viva, dance, tokyo, music"
            className="w-full bg-[#16161E] text-white text-xs p-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#00F2FE]"
          />
        </div>

        {/* Sound Track */}
        <div>
          <label className="text-xs font-bold text-neutral-400 mb-1.5 flex items-center gap-1.5">
            <Music className="w-3.5 h-3.5 text-[#00F2FE]" /> Sound Track Title
          </label>
          <input
            type="text"
            value={soundTitle}
            onChange={(e) => setSoundTitle(e.target.value)}
            placeholder="Track name or original audio"
            className="w-full bg-[#16161E] text-white text-xs p-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#00F2FE]"
          />
        </div>

        {/* Category */}
        <div>
          <label className="text-xs font-bold text-neutral-400 mb-1.5 block">Category</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full bg-[#16161E] text-white text-xs p-3 rounded-xl border border-white/10 focus:outline-none focus:border-[#FE2C55]"
          >
            <option value="Dance">Dance & Movement</option>
            <option value="Music">Music & Beats</option>
            <option value="Visual Arts">Visual Arts & Effects</option>
            <option value="Food">Food & Lifestyle</option>
            <option value="Tech">Technology</option>
          </select>
        </div>

        {/* Toggles */}
        <div className="bg-[#14141B] rounded-2xl p-4 border border-white/5 space-y-3">
          <label className="flex items-center justify-between cursor-pointer">
            <span className="text-xs font-semibold text-neutral-200">Allow Comments</span>
            <input
              type="checkbox"
              checked={allowComments}
              onChange={(e) => setAllowComments(e.target.checked)}
              className="accent-[#FE2C55] w-4 h-4 rounded"
            />
          </label>
          <div className="h-[1px] bg-white/5" />
          <label className="flex items-center justify-between cursor-pointer">
            <span className="text-xs font-semibold text-neutral-200">Private (Only Followers)</span>
            <input
              type="checkbox"
              checked={isPrivate}
              onChange={(e) => setIsPrivate(e.target.checked)}
              className="accent-[#FE2C55] w-4 h-4 rounded"
            />
          </label>
        </div>

        {/* Progress Bar */}
        {uploading && (
          <div className="space-y-1.5">
            <div className="flex justify-between text-xs text-neutral-400 font-bold">
              <span>Uploading Video...</span>
              <span>{progress}%</span>
            </div>
            <div className="h-2 w-full bg-neutral-800 rounded-full overflow-hidden">
              <div 
                className="h-full bg-gradient-to-r from-[#FE2C55] to-[#00F2FE] transition-all duration-300"
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>
        )}

        {/* Submit Button */}
        <button
          type="submit"
          disabled={uploading}
          className="w-full py-3.5 rounded-2xl viva-gradient-btn text-white font-extrabold text-sm shadow-xl flex items-center justify-center gap-2 mt-4"
        >
          <Sparkles className="w-4 h-4" />
          <span>{uploading ? 'Processing Video...' : 'Publish to VIVA'}</span>
        </button>
      </form>
    </div>
  );
};
