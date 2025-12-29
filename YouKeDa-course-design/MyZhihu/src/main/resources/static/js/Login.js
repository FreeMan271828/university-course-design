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

var typeParamDiv = loginForm.querySelector('.typeParam');
if(loginParams.has('type')){
    const paramValue = loginParams.get('type');
    typeParamDiv.value=paramValue;
}