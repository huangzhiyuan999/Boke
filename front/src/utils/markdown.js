export function renderMarkdown(text) {
  if (!text) return ''

  let html = text

  // 1. Code blocks (protect from other rules)
  const codeBlocks = []
  html = html.replace(/```(\w*)\n([\s\S]*?)```/g, (_, lang, code) => {
    const idx = codeBlocks.length
    codeBlocks.push(`<pre><code>${escapeHtml(code.trim())}</code></pre>`)
    return `\x00CODE${idx}\x00`
  })

  // 2. Inline code
  html = html.replace(/`([^`]+)`/g, (_, code) => {
    const idx = codeBlocks.length
    codeBlocks.push(`<code>${escapeHtml(code)}</code>`)
    return `\x00CODE${idx}\x00`
  })

  // 3. Markdown images ![alt](url)
  html = html.replace(/!\[([^\]]*)\]\(([^)\s]+)(?:\s+"([^"]*)")?\)/g,
    (_, alt, url, title) => {
      const t = title ? ` title="${escapeAttr(title)}"` : ''
      return `<img src="${escapeAttr(url)}" alt="${escapeAttr(alt)}"${t}>`
    })

  // 4. Raw image URLs on their own line (auto-detect by extension)
  html = html.replace(/^(\s*)(https?:\/\/\S+\.(?:jpg|jpeg|png|gif|webp|svg|bmp|ico|avif)(?:\?\S*)?)\s*$/gim,
    (_, space, url) => `${space}<img src="${escapeAttr(url)}" alt="">`)

  // 5. Raw URLs from known image hosts on their own line
  html = html.replace(/^(\s*)(https?:\/\/\S+)\s*$/gim,
    (_, space, url) => {
      if (isImageHost(url)) return `${space}<img src="${escapeAttr(url)}" alt="">`
      return _
    })

  // 6. Markdown links [text](url)
  html = html.replace(/\[([^\]]+)\]\(([^)\s]+)(?:\s+"([^"]*)")?\)/g,
    (_, txt, url, title) => {
      const t = title ? ` title="${escapeAttr(title)}"` : ''
      return `<a href="${escapeAttr(url)}"${t} target="_blank" rel="noopener">${txt}</a>`
    })

  // 7. Headers (must be at line start)
  html = html.replace(/^#### (.+)$/gm, '<h4>$1</h4>')
  html = html.replace(/^### (.+)$/gm, '<h3>$1</h3>')
  html = html.replace(/^## (.+)$/gm, '<h2>$1</h2>')
  html = html.replace(/^# (.+)$/gm, '<h1>$1</h1>')

  // 8. Horizontal rules
  html = html.replace(/^(---|\*\*\*|___)\s*$/gm, '<hr>')

  // 9. Blockquote
  html = html.replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>')
  html = html.replace(/^> (.+)$/gm, '<blockquote>$1</blockquote>')

  // 10. Bold + italic
  html = html.replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\b_(.+?)_\b/g, '<em>$1</em>')
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>')

  // 11. Strikethrough
  html = html.replace(/~~(.+?)~~/g, '<del>$1</del>')

  // 12. Ordered lists
  html = html.replace(/^(\d+)\. (.+)$/gm, '<li>$2</li>')

  // 13. Unordered lists
  html = html.replace(/^[-*+] (.+)$/gm, '<li>$1</li>')

  // 14. Wrap consecutive list items
  html = html.replace(/((?:<li>.*<\/li>\n?)+)/g, '<ul>$1</ul>')

  // 15. Paragraphs: blank lines separate paragraphs
  html = html.replace(/\n\s*\n/g, '</p><p>')

  // 16. Single newline → line break
  html = html.replace(/\n/g, '<br>')

  // 17. Wrap in paragraph
  html = '<p>' + html + '</p>'

  // 18. Clean up: empty paragraphs
  html = html.replace(/<p>\s*<\/p>/g, '')

  // 19. Pull block elements out of paragraphs
  const blocks = 'h[1-4]|pre|ul|ol|blockquote|hr|img|table'
  html = html.replace(new RegExp(`<p>\\s*(<(${blocks})[^>]*>)`, 'g'), '$1')
  html = html.replace(new RegExp(`(<\\/(${blocks})>)\\s*<\\/p>`, 'g'), '$1')
  html = html.replace(new RegExp(`(<\\/(${blocks})>)\\s*<p>`, 'g'), '$1')
  html = html.replace(/<p>\s*$/, '')
  html = html.replace(/^\s*<\/p>/, '')

  // 20. Restore code blocks
  html = html.replace(/\x00CODE(\d+)\x00/g, (_, idx) => codeBlocks[+idx] || '')

  return html
}

const IMAGE_CDN_PATTERNS = [
  /\.?hbimg\./i,
  /\.?imgur\.com$/i,
  /\.?pinimg\.com$/i,
  /images\.unsplash\.com/i,
  /\.staticflickr\.com$/i,
  /sinaimg\.cn/i,
  /i\.loli\.net$/i,
  /i\.ibb\.co$/i,
  /qpic\.cn/i,
  /\.alicdn\.com$/i,
  /\.aliyuncs\.com$/i,
  /\.cloudinary\.com$/i,
  /i\.postimg\.cc$/i,
  /i\dv?\.wp\.com$/i,
  /cdninstagram\.com/i,
  /\.fbcdn\.net$/i,
  /twimg\.com/i,
]

function isImageHost(url) {
  try {
    const host = new URL(url).hostname.toLowerCase()
    return IMAGE_CDN_PATTERNS.some(p => p.test(host))
  } catch {
    return false
  }
}

function escapeHtml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

function escapeAttr(s) {
  return s.replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}
