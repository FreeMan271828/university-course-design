var postsDiv = document.querySelectorAll('.posts');

//这一块完成评论数量获取
var commentsNumDiv = document.getElementsByClassName('commentNum');
var commentsDataNumDiv = document.getElementsByClassName('commentNumData');
var commentDiv = document.querySelectorAll('.comments-replies');
for(var i=0;i<postsDiv.length;i++){
    var comments = commentsDataNumDiv[i].value;
    // 获取评论数量
    var commentCount = comments.length; 
    commentsNumDiv[i].innerHTML = commentCount + "条评论";
}
//完成userId的配置
var userIdInfo = document.querySelectorAll('.footer form input[name=userId]');
for(let i=0;i<userIdInfo.length;i++){
    if(loginParams.has("userId"))
        userIdInfo[i].value = loginParams.get("userId");
}
//完成点击评论按钮跳出评论
var commentBtns = document.querySelectorAll('.footer .comment')
var commentBlocks = document.getElementsByClassName('comments-replies');
for(let i=0;i<commentBtns.length;i++){
    commentBtns[i].onclick=function(){
        commentBlocks[i].style.display='block';
        maskDiv.style.display='block';
        //获取comment的高度并返回
        let halfHeight = -commentDiv[i].offsetHeight/2;
        commentDiv[i].style.setProperty('--element-halfHeight', halfHeight + 'px');
    };
}
//完成跳出评论
var deleteBtns = document.getElementsByClassName('delete');
for(let i=0; i<deleteBtns.length;i++){
    deleteBtns[i].onclick=function(){
        commentBlocks[i].style.display='none';
        maskDiv.style.display='none';
    };
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
var mainData = document.querySelectorAll('.main');
//图片
for(let i=0;i<swingButtons.length;i++){
    var titleDiv = postsDiv[i].querySelector('.title');
    titleDiv.onmouseover=function(){
        this.style.color="#285094";
        this.style.cursor="pointer";
    }
    titleDiv.onmouseout=function(){
        this.style.color="#1C1C1C";
        this.style.cursor="auto";
    }
    swingButtons[i].onclick=function(){
        var photoDiv = mainData[i].querySelector('.photo');
        mainData.innerHTML='<div class="right"><div class="text"><strong>[[${post.user.name}]]:</strong>[[${post.content}]]</div><img class="photo" th:src="${post.photo}"><button class="expand">阅读全文</button></div>';
        photoDiv.style.width="590px";
        photoDiv.style.height="403px";
        textDivs[i].style.width="650px";
        mainData[i].style.display = "block";
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
        var photoDiv = mainData[i].querySelector('.photo');
        photoDiv.style.width="190px";
        photoDiv.style.height="105px";
        mainData.innerHTML='<img class="photo" th:src="${post.photo}"><div class="right"><div class="text"><strong>[[${post.user.name}]]:</strong>[[${post.content}]]</div><button class="expand">阅读全文</button></div>';
        textDivs[i].style.width="433px";
        mainData[i].style.display = "flex";
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

//提交评论
const commentForm = document.querySelector('.write-input');
const commentInput = commentForm.querySelector("input[name='content']");
const urlParams = new URLSearchParams(window.location.search);
const userId = urlParams.get('userId');
if(userId){
}else{
    console.error("未登录");
}
commentForm.addEventListener("submit",(event)=>{
    if(commentInput.value.trim()===""){
        alert("请输入评论内容");
        event.preventDefault();
    }
});