import React from 'react';

interface VerifiedBadgeProps {
  size?: number;
  className?: string;
}

export const VerifiedBadge: React.FC<VerifiedBadgeProps> = ({ size = 16, className = '' }) => {
  return (
    <span 
      className={`inline-flex items-center justify-center select-none ${className}`}
      title="VIVA Verified Official Account"
      style={{ width: size, height: size }}
    >
      <svg
        viewBox="0 0 24 24"
        width={size}
        height={size}
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M12 2L14.7 4.9L18.6 4.3L19.8 8.1L23.3 9.9L22.4 13.8L24 17.5L20.4 19.1L19.4 23L15.6 22.3L13 25L10.3 22.3L6.5 23L5.5 19.1L1.9 17.5L2.8 13.8L1.8 9.9L5.3 8.1L6.5 4.3L10.4 4.9L12 2Z"
          fill="#20D5EC"
          transform="scale(0.85) translate(2, 0)"
        />
        <path
          d="M8.5 11.5L10.5 13.5L15.5 8.5"
          stroke="#000000"
          strokeWidth="2.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    </span>
  );
};
