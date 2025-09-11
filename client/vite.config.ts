import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@features': '/src/features',
      '@api': '/src/api',
      '@assets': '/src/assets',
      '@routes': '/src/routes',
      '@types': '/src/types',
      '@components': '/src/components',
      '@store': '/src/store',
      '@utils': '/src/utils',
      '@': '/src',
    },
  },
  define: {
    global: 'globalThis',
  },
})
