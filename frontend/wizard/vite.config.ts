import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
 
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // Redirige /api/* al backend Spring Boot durante el desarrollo
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
 