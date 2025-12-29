const bodyDiv = document.getElementsByTagName('body');


const input = document.querySelector('.search-input');
const searchResult = document.querySelector('.searchResult');
input.addEventListener('keyup',function(){
    if(this.value==='奥密克戎变异'){
        searchResult.style.display='block';
    }
    else{
        searchResult.style.display='none';
    }
});


const navDiv = document.querySelector('.nav');
const navUl= navDiv.getElementsByTagName('ul');
var navigations = navUl[0].getElementsByTagName('button');
const queryString = window.location.search;
const loginParams = new URLSearchParams(queryString);
const navToWeb = new Map();
navToWeb.set("综合","home");
navToWeb.set("用户","user");
for(let i=0;i<2;i++){
    navigations[i].onclick=function(){
        //修改网址
        var userParam = null;
        //判断是否已经登录
        const isloginParamsExist = loginParams.has('userId');
        if(isloginParamsExist===true){
            userParam=loginParams.get('userId');
        }
        //根据点击的按钮来确定要跳转的页面
        const typeParam = navToWeb.get(navigations[i].innerText);
        switch (typeParam){
            case 'home':
                if(isloginParamsExist===true){
                    //已经登录且在home界面下
                    sessionStorage.setItem('activeButtonIndex', i);
                    window.location.href="/zhihu/homepage_logsuccess?userId="+userParam+"&type="+typeParam;
                }
                else{
                    //未登录且在home目录下
                    sessionStorage.setItem('activeButtonIndex', i);
                    window.location.href="/zhihu/homepage?type="+typeParam;
                }
                break;
            case 'user':
                if(isloginParamsExist===true){
                    //已登录且在user目录下
                    sessionStorage.setItem('activeButtonIndex', i);
                    window.location.href = "/zhihu/userpage_logsuccess?userId=" + userParam + "&type=" + typeParam;
                }
                else{
                    //未登录且在user目录下
                    sessionStorage.setItem('activeButtonIndex', i);
                    window.location.href="/zhihu/userpage?type="+typeParam;
                }
                break;
            default:
                sessionStorage.setItem('navigations', JSON.stringify(navigations));
                sessionStorage.setItem('i', 0);
                window.location.href="/zhihu/homepage?type=home";
                break;
        };
    }
}

var quitDiv = document.querySelector('.right-box .quit');
quitDiv.onmouseover=function(){
    this.style.cursor="pointer";
}
quitDiv.onmouseout=function(){
    this.style.cursor="auto";
}
quitDiv.onclick=function(){
    const typeParam = loginParams.get("type");
    switch (typeParam){
        default:
        case "home":
            window.location.href="/zhihu/homepage?type="+typeParam;
        break;
        case "user":
            window.location.href="/zhihu/userpage?type="+typeParam;
        break;
    }
}

// 在目标页面 中
document.addEventListener('DOMContentLoaded', () => {
    const activeButtonIndex = sessionStorage.getItem('activeButtonIndex');
    const buttons = navUl[0].querySelectorAll('button');
    if (activeButtonIndex !== null && buttons.length > activeButtonIndex) {
    buttons[activeButtonIndex].classList.add('active');
    }
});