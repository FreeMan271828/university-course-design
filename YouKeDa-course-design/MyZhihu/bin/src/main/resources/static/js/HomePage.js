// 存储滚动位置
window.addEventListener('scroll', function() {
  localStorage.setItem('scrollTop', window.pageYOffset);
});

// 恢复滚动位置
window.addEventListener('load', function() {
  const scrollTop = localStorage.getItem('scrollTop');
  if (scrollTop) {
    window.scrollTo(0, scrollTop);
  }
});