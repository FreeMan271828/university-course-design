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

var btns = document.getElementsByTagName("button");
for(var i=0;i<btns.length;i++){
  btns[i].onmouseover=function(){
      this.style.cursor="pointer";
  }
  btns[i].onmouseout=function(){
      this.style.cursor="auto";
  }
}

var backToEnd = document.querySelector('.homepage-footer .back-to-end');
backToEnd.onmouseover=function(){
  this.style.cursor="pointer";
}
backToEnd.onmouseout=function(){
  this.style.cursor="auto";
}
backToEnd.onclick=function(){
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}

// 在目标页面 中
document.addEventListener('DOMContentLoaded', () => {
  const activeButtonIndex = sessionStorage.getItem('activeButtonIndex');
  const buttons = navUl[0].querySelectorAll('button');
  if (activeButtonIndex !== null && buttons.length > activeButtonIndex) {
  buttons[activeButtonIndex].classList.add('active');
  }
});