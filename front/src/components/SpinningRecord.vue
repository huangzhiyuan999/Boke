<template>
  <div class="record-wrapper" :class="{ playing }" title="赵雷 - 彩虹下面" @click="toggle">
    <div class="record">
      <div class="record-center">
        <span class="record-label">赵雷</span>
      </div>
    </div>
    <div class="hover-overlay">
      <span class="play-icon" v-if="playing">&#10074;&#10074;</span>
      <span class="play-icon" v-else>&#9654;</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const musicSrc = '/music/赵雷 - 彩虹下面.mp3'
const playing = ref(false)

let audio = null
let fallbackCleanup = null

function getOrCreateAudio() {
  // Reuse existing audio in DOM (survives HMR)
  let el = document.getElementById('bg-music')
  if (el) {
    audio = el
    return
  }
  audio = new Audio()
  audio.id = 'bg-music'
  audio.src = musicSrc
  audio.loop = true
  audio.preload = 'auto'
  audio.volume = 0.5
  document.body.appendChild(audio)
}

function play() {
  getOrCreateAudio()
  audio.play().then(() => {
    playing.value = true
  }).catch(() => {})
}

function pause() {
  audio?.pause()
  playing.value = false
}

function toggle() {
  console.log('toggle clicked, playing:', playing.value)
  // Kill any pending fallback listeners first
  if (fallbackCleanup) {
    fallbackCleanup()
    fallbackCleanup = null
  }
  if (playing.value) {
    pause()
  } else {
    play()
  }
}

onMounted(() => {
  getOrCreateAudio()

  audio.play().then(() => {
    playing.value = true
  }).catch(() => {
    // Browser blocked autoplay, wait for user interaction
    const handler = () => {
      if (audio && audio.paused) {
        audio.play().then(() => {
          playing.value = true
        }).catch(() => {})
      }
      if (fallbackCleanup) {
        fallbackCleanup()
        fallbackCleanup = null
      }
    }
    const events = ['scroll', 'keydown', 'touchstart']
    events.forEach(e => document.addEventListener(e, handler, { once: true, passive: true }))
    // Click: don't use once, we remove manually to avoid conflict with toggle
    document.addEventListener('click', handler, { passive: true })
    fallbackCleanup = () => {
      events.forEach(e => document.removeEventListener(e, handler))
      document.removeEventListener('click', handler)
    }
  })
})

onUnmounted(() => {
  // Don't remove audio on unmount, just clean up listeners
  if (fallbackCleanup) {
    fallbackCleanup()
    fallbackCleanup = null
  }
})
</script>

<style scoped>
.record-wrapper {
  position: relative;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  cursor: pointer;
}

.record {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: radial-gradient(
    circle at center,
    #e63946 0%,
    #e63946 14%,
    #222 15%,
    #333 20%,
    #1a1a1a 25%,
    #2a2a2a 35%,
    #1a1a1a 45%,
    #2a2a2a 55%,
    #1a1a1a 65%,
    #2a2a2a 75%,
    #1a1a1a 85%,
    #222 100%
  );
  box-shadow: 0 3px 12px rgba(0,0,0,0.35);
  animation: spin 3s linear infinite;
  animation-play-state: running;
  position: relative;
}

.record-wrapper:not(.playing) .record {
  animation-play-state: paused;
}

.record-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #e63946;
  display: flex;
  align-items: center;
  justify-content: center;
}

.record-label {
  font-size: 11px;
  color: #fff;
  font-weight: 700;
  white-space: nowrap;
}

.hover-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  pointer-events: none;
}

.record-wrapper:hover .hover-overlay {
  background: rgba(0, 0, 0, 0.45);
}

.play-icon {
  color: #fff;
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.2s;
  text-shadow: 0 1px 3px rgba(0,0,0,0.5);
}

.record-wrapper:hover .play-icon {
  opacity: 1;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
