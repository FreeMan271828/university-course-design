const bodyDiv = document.getElementsByTagName('body');

var btns = document.getElementsByTagName('button');
for(var i=0;i<btns.length;i++){
    btns[i].onmouseover=function(){
        this.style.cursor="pointer";
    }
    btns[i].onmouseout=function(){
        this.style.cursor-"auto";
    }
}

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

const loginButton = document.querySelector('.login');
const loginForm = document.querySelector('.login-box');
const maskDiv = document.querySelector('.mask');
loginButton.onclick=function(){
    loginForm.style.display='block';
    maskDiv.style.display='block';
}
const loginQuitButton = document.querySelector('.quit-box');
loginQuitButton.onmouseover=function(){
    this.style.cursor="pointer";
};
loginQuitButton.onmouseout=function(){
    this.style.cursor="auto";
};
loginQuitButton.onclick=function(){
    loginForm.style.display='none';
    maskDiv.style.display='none';
}