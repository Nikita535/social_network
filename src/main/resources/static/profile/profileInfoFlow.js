let $name=document.getElementById("friendStatus");
let currentLocation = document.location.protocol + "//" + document.location.host;



//Добавление в друзья
function addButton($mainObj){
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-primary", "btn-icon-text", "btn-edit-profile");
    $friendButton.id = "addFriend"
    $friendButton.innerHTML += "Добавить в друзья";
    $friendButton.onclick=function(){addFriend()};
    $mainObj.appendChild($friendButton);
}

function addFriend(){
    createAjaxQuery('/user/' + usernameOfPage + '/friend', successAddFriendHandler())
}

function successAddFriendHandler(){
    let obj=document.getElementById("addFriend");
    let $mainObj = obj.parentNode
    $mainObj.removeChild(obj);
    sendButton($mainObj)
}



//Заявка отправлена
function sendButton($mainObj){
    const $friendButton = document.createElement('div');
    $friendButton.classList.add("btn", "btn-dark")
    $friendButton.id = "sendFriend"
    $friendButton.innerHTML += "Заявка отправлена";
    $mainObj.appendChild($friendButton);
}



//Удаление из друзей
function deleteButton($mainObj){
    const $friendButton = document.createElement('div');
    $friendButton.classList.add("btn", "btn-dark","m-2")
    $friendButton.innerHTML += "В друзьях";
    $friendButton.id = "deleteFriend1";
    $mainObj.appendChild($friendButton);

    const $friendButton2 = document.createElement('button');
    $friendButton2.classList.add("btn", "btn-danger");
    $friendButton2.innerHTML += "Удалить из друзей";
    $friendButton2.id = "deleteFriend2"
    $friendButton2.onclick=function(){deleteFriend()};
    $mainObj.appendChild($friendButton2);
}

function deleteFriend(){
    createAjaxQuery('/user/' + usernameOfPage + '/unfriend', successDeleteFriendHandler())
}

function successDeleteFriendHandler(){
    let obj1=document.getElementById("deleteFriend1");
    let obj2=document.getElementById("deleteFriend2");
    let $mainObj = obj1.parentNode
    $mainObj.removeChild(obj1);
    $mainObj.removeChild(obj2);
    addButton($mainObj)
}

//Принятие или отклонение заявки
function acceptAndReceiveButton($mainObj){
    const $friendButton1 = document.createElement('button');
    $friendButton1.classList.add("btn", "btn-success","m-2");
    $friendButton1.innerHTML += "Принять заявку";
    $friendButton1.id = "acceptFriend";
    $friendButton1.onclick=function(){acceptFriend()};
    $mainObj.appendChild($friendButton1);

    const $friendButton2 = document.createElement('button');
    $friendButton2.classList.add("btn", "btn-danger");
    $friendButton2.innerHTML += "Отклонить заявку";
    $friendButton2.id = "receiveFriend";
    $friendButton2.onclick=function(){receiveFriend()};
    $mainObj.appendChild($friendButton2);
}

function  acceptFriend(){
    createAjaxQuery('/user/' + usernameOfPage + '/friend/1', acceptFriendHandler())
}

function  receiveFriend(){
    createAjaxQuery('/user/' + usernameOfPage + '/friend/2', receiveFriendHandler())
}

function acceptFriendHandler(){
    let obj1=document.getElementById("acceptFriend");
    let obj2=document.getElementById("receiveFriend");
    let $mainObj = obj1.parentNode
    $mainObj.removeChild(obj1);
    $mainObj.removeChild(obj2);
    deleteButton($mainObj)
}

function receiveFriendHandler(){
    let obj1=document.getElementById("acceptFriend");
    let obj2=document.getElementById("receiveFriend");
    let $mainObj = obj1.parentNode
    $mainObj.removeChild(obj1);
    $mainObj.removeChild(obj2);
    addButton($mainObj)
}


//Создание запросов
function createAjaxQuery(url, toFunction)
{
    jQuery.ajax({
        type       : 'GET',
        url        : currentLocation + url,
        contentType: 'application/json',
        success    : toFunction
    });
}


$(function () {
    if (isFriend === false && usernameOfPage !== usernameOfSession) {
        addButton($name);
    }else if (isFriend === true && friendAccepted === true){
        deleteButton($name)
    }else if (isFriend === true && friendAccepted === false && isInviteSend === true){
        sendButton($name)
    }else if(isFriend === true && friendAccepted === false && isInviteRecieved === true){
        acceptAndReceiveButton($name)
    }
    else if(usernameOfPage === usernameOfSession){
        const $friendButton = document.createElement('a');
        $friendButton.href = '/logout';
        $friendButton.classList.add("btn", "btn-danger");
        $friendButton.text = "Выйти";
        $name.appendChild($friendButton);
    }
})

