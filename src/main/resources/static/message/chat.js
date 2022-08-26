var currentLocationOfHtml = document.location.protocol + "//" + document.location.host;
const userToInformation = document.querySelector("#userToInformation")
const userToNoneInformation = document.querySelector("#userToNoneInformation")
const deletingPreviousChat = document.querySelector("#chat")

const getChatMessages = async (event) => {
    let form = event.currentTarget
    let username = form.id

    userToInformation.style = "display: block"
    userToNoneInformation.style = "display: none"
    deletingPreviousChat.innerHTML = ""

    jQuery.ajax({
        type: 'GET',
        url: currentLocationOfHtml + "/chat/" + username,
        contentType: 'application/json',
        success: creatingTheChat
    });
}

function creatingTheChat(data){
    userTo = data[0]
    setName(data[0])
    createMessages(data[1])
    connect()
}

function setName(user){
    userToInformation.querySelector("#userToAvatar").src = user["image"] != null ? '/image/' +
        user["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'
    userToInformation.querySelector("#userToName").innerHTML = user["profileInfo"]["name"] + " "
        + user["profileInfo"]["surname"]
}

function createMessages(allMessages){
    for (let i = 0; i < allMessages.length; i++)
        createMessageLine(allMessages[i]);
}