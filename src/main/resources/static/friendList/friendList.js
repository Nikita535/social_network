let $name=document.getElementById("friendLists");
let $receivedList=document.getElementById("receivedList");
var currentLocation = document.location.protocol + "//" + document.location.host;
var userNameOfPage = window.userNameOfPage;
var searchLine = window.searchLine;
var jsonDataOfThePage;
var profilesOfStrangers;
var friendProfiles;
var profilesChecked;
var showFriends;
var ShowStrangers;
var maxPages;
var errorNoSuchFriends;
var errorNoSuchStrangers;
var profilesOfReceivedStrangers;
var allImages;
var page = 1;


// ПОИСК ПО ПОЛЬЗОВАТЕЛЯМ
function addSearchButton($mainObj){
    $mainObj.innerHTML += "<div class=\"input-group\">\n" +
        "                                    <input id=\"searchLine\" class=\"form-control rounded\" placeholder=\"Search\" aria-label=\"Search\" aria-describedby=\"search-addon\" />\n" +
        "                                    <button onclick='toSearch()' class=\"btn btn-outline-primary\">Искать</button>\n" +
        "                                </div>";


}

function toSearch(){
    const value = document.getElementById("searchLine").value;
    $name.innerHTML = "";
    page = 1;
    addSearchButton($name);
    reloadData(page, value);
}


// КНОПКА УДАЛЕНИЯ ДРУГА
function deleteButton($name){
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3")
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-danger", "pull-right");
    $friendButton.innerHTML += "Удалить из друзей";
    $friendButton.id = $name;
    $friendButton.onclick=function(){deleteFriend($name)};
    buttonContainer.appendChild($friendButton)
    return buttonContainer;
}

function deleteFriend($name){
    createAjaxQuery('/user/' + $name + '/unfriend', successDeleteFriendHandler($name))
}

function successDeleteFriendHandler($username){
    let obj=document.querySelector("." + $username).parentNode;
    document.getElementById($username).parentNode.parentNode.appendChild(sendButton($username));
    document.getElementById($username).parentNode.parentNode.removeChild(document.getElementById($username).parentNode);
    $name.removeChild(obj);
    $name.appendChild(obj);
}

// КНОПКА ОТПРАВКИ ЗАПРОСА В ДРУЗЬЯ
function  sendButton($name){
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3");
    const $friendButton = document.createElement('button');
    $friendButton.classList.add("btn", "btn-primary", "pull-right");
    $friendButton.innerHTML += "Добавить в друзья";
    $friendButton.id = $name;
    $friendButton.onclick=function(){sendFriend($name)};
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function sendFriend($name){
    createAjaxQuery('/user/' + $name + '/friend', successSendFriendHandler($name))
}

function successSendFriendHandler($name){
    let obj=document.getElementById($name);
    let $mainObj = obj.parentNode;
    let parent = $mainObj.parentNode;
    parent.removeChild($mainObj);
    parent.appendChild(alreadySendButton($name));

}

// КНОПКА ЗАЯВКА ОТПРАВЛЕНА
function alreadySendButton($name){
    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add("ol-md-3", "col-sm-3");
    const $friendButton = document.createElement('div');
    $friendButton.classList.add("btn", "btn-dark", "pull-right");
    $friendButton.innerHTML += "Заявка отправлена";
    $friendButton.id = $name;
    buttonContainer.appendChild($friendButton);
    return buttonContainer;
}

function addFriend($name){
    createAjaxQuery('/user/' + $name + '/friend/1', successAddAndDenyFriendHandler($name, 1))
}

function denyFriend($name){
    createAjaxQuery('/user/' + $name + '/friend/2', successAddAndDenyFriendHandler($name, 2))
}

function successAddAndDenyFriendHandler($username, from){
    let obj=document.querySelector(".rec" + $username);
    let findName = obj.getElementsByTagName("a").item(0);
    var request = {"user":{}, "name":"", "surname":""};
    request.user.images = obj.getElementsByTagName("img").item(0).src;
    request.user.username = findName.href.split("/").at(-1);
    request.name = findName.text.split(" ").at(1);
    request.surname = findName.text.split(" ").at(0);
    request.city = obj.getElementsByTagName("span").item(0).innerText;
    obj.parentNode.removeChild(obj);
    if (from === 1)
        addPerson(request, false, false, true);
    else
        addPerson(request, true, false, true);
}


function addReceivedPerson(person){
    var source = person["user"]["images"].length!==0 ? '/image/' +
        person["user"]["username"] : 'https://bootdey.com/img/Content/avatar/avatar6.png';

    $receivedList.innerHTML += "<div  class=\"d-flex justify-content-between mb-2 pb-2 border-bottom rec" + person["user"]["username"] + "\">\n" +
        "                                        <div class=\"d-flex align-items-center hover-pointer\">\n" +
        "                                            <img src=\"" + source + "\"" +
        "                                                 alt=\"\" class=\"img-xs rounded-circle\">\n" +
        "                                            <div class=\"ml-2\">\n" +
        "                                            <p><a href=\" " + '/user/' + person["user"]["username"] + " \" class=\"profile-link\">" + person["surname"] + " " + person["name"] + "</a></p>\n" +
        "                                            <span style='display: none'>" + person["city"] + "</span>  " +
        "                                         </div>\n" +
        "                                        </div>\n" +
        "                                        <button onclick=\"addFriend(\'" + person["user"]["username"] + "\')\" class=\"btn btn-icon\">\n" +
        "                                            <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
        "                                                 viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
        "                                                 stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
        "                                                 class=\"feather feather-user-plus\" data-toggle=\"tooltip\" title=\"\"\n" +
        "                                                 data-original-title=\"Connect\">\n" +
        "                                                <path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path>\n" +
        "                                                <circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle>\n" +
        "                                                <line x1=\"20\" y1=\"8\" x2=\"20\" y2=\"14\"></line>\n" +
        "                                                <line x1=\"23\" y1=\"11\" x2=\"17\" y2=\"11\"></line>\n" +
        "                                            </svg>\n" +
        "                                        </button>\n" +
        "                                        <button onclick=\"denyFriend(\'" + person["user"]["username"] + "\')\" class=\"btn btn-icon\">\n" +
        "                                            <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
        "                                                 viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
        "                                                 stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
        "                                                 class=\"feather feather-user-plus\" data-toggle=\"tooltip\" title=\"\"\n" +
        "                                                 data-original-title=\"Connect\">\n" +
        "                                                <path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path>\n" +
        "                                                <circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle>\n" +
        "                                                <line x1=\"23\" y1=\"11\" x2=\"17\" y2=\"11\"></line>\n" +
        "                                            </svg>\n" +
        "                                        </button>\n" +
        "                                    </div>"
}


// ФУНКЦИЯ ДОБАВЛЕНИЯ 1 БЛОКА USER
function addPerson(person, isStranger=false, isSend = true, fromRight = false){
    var source;
    if (fromRight === false)
        source = person["user"]["images"].length!==0 ? '/image/' +
                allImages[person["user"]["username"]] : 'https://bootdey.com/img/Content/avatar/avatar6.png';
    else
        source = person["user"]["images"];
    const personContainer = document.createElement('div');
    personContainer.classList.add("nearby-user")
    personContainer.innerHTML += "<div class=\"row " + person["user"]["username"] + "\">\n" +
        "                                        <div class=\"col-md-2 col-sm-2\">\n" +
        "                                            <img src=\"" + source + "\"\n" +
        "                                                 alt=\"user\" class=\"profile-photo-lg\">\n" +
        "                                        </div>\n" +
        "                                        <div class=\"col-md-7 col-sm-7\">\n" +
        "                                            <h5><a href=\" " + '/user/' + person["user"]["username"] + " \" class=\"profile-link\">" + person["surname"] + " " + person["name"] + "</a></h5>\n" +
        "                                            <p class=\"text-muted\">" + person["city"] + "</p>\n" +
        "                                        </div>\n" +
        "                                    </div>\n";

    if (isStranger === false) {
        personContainer.querySelector("." + person["user"]["username"]).appendChild(deleteButton(person["user"]["username"]));
        document.getElementById("friendStatusText").insertAdjacentElement('afterEnd', personContainer);
    }else if (isSend === false){
        personContainer.querySelector("." + person["user"]["username"]).appendChild(sendButton(person["user"]["username"]));
        $name.appendChild(personContainer);}
    else {
        personContainer.querySelector("." + person["user"]["username"]).appendChild(alreadySendButton(person["user"]["username"]));
        $name.appendChild(personContainer);
    }
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


var successHandler = function( data, textStatus, jqXHR ) {
    jsonDataOfThePage = data;
    friendProfiles = data["friendProfiles"];
    profilesOfStrangers = data["profilesOfStrangers"];
    allImages = data["allImages"];

    console.log(data);
    profilesChecked = data["profilesChecked"];
    showFriends = data["showFriends"];
    ShowStrangers = data["ShowStrangers"];
    maxPages = data["pages"];
    errorNoSuchFriends = data["errorNoSuchFriends"];
    errorNoSuchStrangers = data["errorNoSuchStrangers"];
    addAllPeople();
    if (typeof profilesOfReceivedStrangers === "undefined") {
        profilesOfReceivedStrangers = data["profilesOfReceivedStrangers"]
        addAllInvites();
    }
};

function addAllInvites(){
    if (profilesOfReceivedStrangers != null)
        for (let i = 0; i < profilesOfReceivedStrangers[0].length; i++)
            addReceivedPerson(profilesOfReceivedStrangers[0][i]);
}

function addAllPeople(){

    //if (showFriends != null && showFriends[0] === true)
    if (document.getElementById("friendStatusText") === null) {
        $name.appendChild(document.createElement('br'))
        $name.appendChild(createTextUnderPerson("Друзья", "friendStatusText"));
    }

    //if (errorNoSuchFriends != null && errorNoSuchFriends[0] === true)
    //    $name.appendChild(createErrorUnderPerson("Таких друзей нет"));

    if (friendProfiles != null)
        for (let i = 0; i < friendProfiles[0].length; i++)
            addPerson(friendProfiles[0][i]);



    //if (ShowStrangers != null && ShowStrangers[0] === true)
    if (document.getElementById("strangeStatusText") === null)
        $name.appendChild(createTextUnderPerson("Возможные друзья", "strangeStatusText"));
    //if (errorNoSuchStrangers != null && errorNoSuchStrangers[0] === true)
    //    $name.appendChild(createErrorUnderPerson("Таких незнакомцев нет"));

    if (profilesOfStrangers != null)
        for (let i = 0; i < profilesOfStrangers[0].length; i++)
            addPerson(profilesOfStrangers[0][i], true, profilesChecked[0][i]);
}

function createTextUnderPerson(text, id){
    const friendContainer = document.createElement('span');
    friendContainer.style.display = "flex";
    friendContainer.id = id;
    friendContainer.style.justifyContent = "center";
    friendContainer.innerText = text;
    return friendContainer;
}

function createErrorUnderPerson(text){
    const friendContainer = document.createElement('div');
    friendContainer.style.display = "flex";
    friendContainer.style.justifyContent = "center";
    friendContainer.style.color = "darkred"
    friendContainer.innerText = text;
    return friendContainer;
}


function reloadData(page, searchLine="") {
    // Process your request
    var request = new Object();
    request.searchLine = searchLine; // some data

    // Make the AJAX call
    jQuery.ajax({
        type       : 'POST',
        url        : currentLocation + "/user/" + userNameOfPage + "/reloadFriendList/" + page,
        contentType: 'application/json',
        data       : JSON.stringify(request),
        success    : successHandler
    });
}

$(function () {
    addSearchButton($name);
    reloadData(1);
})

$(window).scroll(function()
{
    if  ($(window).scrollTop() + 1000 >= $(document).height() - $(window).height() && page <= maxPages)
    {
        page++;
        reloadData(page);
    }
});