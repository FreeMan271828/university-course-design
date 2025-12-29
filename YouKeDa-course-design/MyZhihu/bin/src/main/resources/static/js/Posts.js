var commentsDiv = document.getElementsByClassName('comments');
var commentsData = document.getElementsByClassName('commentsData');
for(var i=0;i<commentsDiv.length;i++){
    commentsDiv[i].innerHTML=commentsData[i].length+"条评论";
}



//展开按钮
var swingButtons = document.querySelectorAll('.expand');
//收起按钮
var putButtons = document.querySelectorAll('.put-away');
//文本
var textDivs = document.querySelectorAll('.right .text');
//功能按钮
var favorButtons = document.querySelectorAll('.favorite');
var shareButtons = document.querySelectorAll('.share');
var reportButtons = document.querySelectorAll('.report');
var dateMiniButton = document.querySelectorAll('.date-mini');
var dateFullButton = document.querySelectorAll('.date-full');
for(let i=0;i<swingButtons.length;i++){
    swingButtons[i].onclick=function(){
        swingButtons[i].style.display = 'none';
        putButtons[i].style.display = 'block';
        textDivs[i].style.display = 'block';
        favorButtons[i].style.display = 'block';
        shareButtons[i].style.display = 'block';
        reportButtons[i].style.display = 'block';
        dateMiniButton[i].style.display='none';
        dateFullButton[i].style.display='block';
    };
}
for(let i=0;i<putButtons.length;i++){
    putButtons[i].onclick=function(){
        swingButtons[i].style.display = 'block';
        putButtons[i].style.display = 'none';
        textDivs[i].style.display = '-webkit-box';
        favorButtons[i].style.display = 'none';
        shareButtons[i].style.display = 'none';
        reportButtons[i].style.display = 'none';
        dateMiniButton[i].style.display='block';
        dateFullButton[i].style.display='none';
    };
}