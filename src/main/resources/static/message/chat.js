let currentLocationOfHtml = document.location.protocol + "//" + document.location.host;
const userToInformation = document.querySelector("#userToInformation")
const userToNoneInformation = document.querySelector("#userToNoneInformation")
const deletingPreviousChat = document.querySelector("#chat")
let messagePage
let friendUsername

const getChatMessages = async (event) => {
    let form = event.currentTarget
    friendUsername = form.id
    messagePage = 0
    userToInformation.style = "display: block"
    document.querySelector("#deleteMessage").addEventListener('click', deleteMessage, true)
    userToNoneInformation.style = "display: none"
    deletingPreviousChat.innerHTML = ""

    if (userTo !== null) {
        if (userFrom["id"] < userTo["id"])
            stompClient.unsubscribe('/topic/' + userFrom["id"] + "/" + userTo["id"])
        else
            stompClient.unsubscribe('/topic/' + userTo["id"] + "/" + userFrom["id"])
    }


    jQuery.ajax({
        type: 'GET',
        url: currentLocationOfHtml + "/chat/" + friendUsername + "/" + messagePage,
        contentType: 'application/json',
        success: creatingTheChat
    });
}

function creatingTheChat(data) {
    userTo = data[0]
    setName(data[0])
    createMessages(data[1])
    chat.scrollTop = chat.scrollHeight
    connect()
    $(window).scrollTop($(document).height())
}

function setName(user) {
    userToInformation.querySelector("#userToAvatar").src = user["image"] != null ? '/image/' +
        user["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'
    userToInformation.querySelector("#userToName").innerHTML = user["profileInfo"]["name"] + " "
        + user["profileInfo"]["surname"]
}

function createMessages(allMessages) {
    for (let i = 0; i < allMessages.length; i++)
        fetchMessages(allMessages[i]);
}

$(chat).scroll(function () {

    if (chat.getBoundingClientRect().y - chat.firstChild.getBoundingClientRect().y === 0) {
        messagePage++
        jQuery.ajax({
            type: 'GET',
            url: currentLocationOfHtml + "/chat/" + friendUsername + "/" + messagePage,
            contentType: 'application/json',
            success: uploadMessages
        });
    }
})

function uploadMessages(data) {
    let lastScrollHeight = chat.scrollHeight
    createMessages(data[1])
    let scrollDif = chat.scrollHeight - lastScrollHeight
    chat.scrollTop += scrollDif
}