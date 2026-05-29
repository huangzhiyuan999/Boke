<template>
  <div class="chart-wrap">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
      <div class="chart-legend">
        <span v-for="(s, i) in series" :key="i" class="legend-item">
          <span class="legend-dot" :style="{ background: s.color }"></span>
          {{ s.name }}
        </span>
      </div>
    </div>
    <svg class="chart-svg" :viewBox="`0 0 ${w} ${h}`" preserveAspectRatio="none">
      <!-- Grid lines -->
      <line
        v-for="i in 5"
        :key="'g'+i"
        :x1="0" :y1="(h * (i - 1)) / 4 - 20"
        :x2="w" :y2="(h * (i - 1)) / 4 - 20"
        stroke="#e8ecf1"
        stroke-width="1"
      />
      <!-- Data lines -->
      <polyline
        v-for="(s, si) in series"
        :key="'l'+si"
        :points="linePoints(s.data)"
        :stroke="s.color"
stroke-width="2"
        fill="none"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
      <!-- Data points -->
      <g v-for="(s, si) in series" :key="'p'+si">
        <circle
          v-for="(v, vi) in s.data"
          :key="vi"
          :cx="x(vi)"
          :cy="y(v)"
          r="3"
          :fill="s.color"
          stroke="#fff"
          stroke-width="1.5"
        />
      </g>
      <!-- X labels -->
      <text
        v-for="(label, i) in labels"
        :key="'xl'+i"
        :x="x(i)"
        :y="h - 4"
        text-anchor="middle"
        font-size="10"
        fill="#a0aec0"
      >{{ label }}</text>
    </svg>
  </div>
</template>

<script setup>
const props = defineProps({
  title: { type: String, default: '' },
  labels: { type: Array, default: () => [] },
  series: { type: Array, default: () => [] }
})

const w = 480
const h = 160
const padding = { top: 14, bottom: 22, left: 6, right: 6 }

const maxVal = () => {
  let max = 0
  props.series.forEach(s => {
    s.data.forEach(v => { if (v > max) max = v })
  })
  return max || 100
}

function x(i) {
  return padding.left + (i / (props.labels.length - 1 || 1)) * (w - padding.left - padding.right)
}

function y(v) {
  const m = maxVal()
  return padding.top + ((m - v) / m) * (h - padding.top - padding.bottom)
}

function linePoints(data) {
  return data.map((v, i) => `${x(i)},${y(v)}`).join(' ')
}
</script>

<style scoped>
.chart-wrap {
  background: #fff;
  border-radius: var(--radius-md);
  padding: 14px 18px;
  box-shadow: var(--shadow-sm);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  flex-wrap: wrap;
  gap: 8px;
}

.chart-title {
  font-size: 0.88rem;
  font-weight: 600;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 14px;
}

.legend-item {
  font-size: 0.72rem;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.chart-svg {
  width: 100%;
  height: auto;
  overflow: visible;
}
</style>
