/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode:false,

  typescript: {
    // !! WARN !!
    // Dangerously allow production builds to successfully complete even if
    // your project has type errors.
    // !! WARN !!
    ignoreBuildErrors: true,
  },
  eslint: {
    ignoreDuringBuilds: true,
  },
  env: {
    NEXT_PUBLIC_API_BASE: process.env.NEXT_PUBLIC_API_BASE,
  },

  // async rewrites() {
  //   return [
  //     {
  //       source: '/api/:path*',
  //       destination: 'http://techinterview-backend:8101/api/:path*',
  //     },
  //   ];
  // },
};
console.log("NEXT_PUBLIC_API_BASE:", process.env.NEXT_PUBLIC_API_BASE);
module.exports = nextConfig;
